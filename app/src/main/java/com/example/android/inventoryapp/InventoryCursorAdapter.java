package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    /**
     * Create a new constructor
     */
    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // find the individual views that will be modified
        TextView productNameTextView = view.findViewById(R.id.product_name);
        TextView priceView = view.findViewById(R.id.price);
        TextView quantityView = view.findViewById(R.id.quantity);
        Button sale = view.findViewById(R.id.sale_button);

        // find the index of the data that is being looked for
        final int inventoryColumnIndex = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        int productNameIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        int quantityIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);

        // read the attributes from the cursor for the current inventory item
        final String productName = cursor.getString(productNameIndex);
        final String price = cursor.getString(priceIndex);
        final String quantity = String.valueOf(cursor.getInt(quantityIndex));

        productNameTextView.setText(productName);
        priceView.setText(price);
        quantityView.setText(quantity);

        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri productUri = ContentUris.withAppendedId(
                        InventoryEntry.CONTENT_URI,
                        inventoryColumnIndex
                );
                saleOneInventory(context,
                        productUri,
                        quantity,
                        productName,
                        price);
            }
        });
    }

    /**
     * Sales one inventory
     */
    private void saleOneInventory(Context context, Uri uri, String quantity, String name, String price) {
        Integer currentQuantity = Integer.valueOf(quantity);
        Integer newQuantity;
        if (currentQuantity <= 0) {
            newQuantity = 0;
        } else {
            newQuantity = currentQuantity - 1;
        }

        // Update table by using new value of quantity
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryEntry.COLUMN_QUANTITY, newQuantity.toString());
        contentValues.put(InventoryEntry.COLUMN_PRICE, price);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_NAME, name);
        context.getContentResolver().update(uri, contentValues, null, null);
    }
}
