package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public void bindView(View view, Context context, Cursor cursor) {
        // find the individual views that will be modified
        TextView productNameTextView = view.findViewById(R.id.product_name);
        TextView priceView = view.findViewById(R.id.price);
        TextView quantityView = view.findViewById(R.id.quantity);

        // find the index of the data that is being looked for
        int productNameIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        int quantityIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);

        // read the attributes from the cursor for the current inventory item
        String productName = cursor.getString(productNameIndex);
        String price = cursor.getString(priceIndex);
        String quantity = String.valueOf(cursor.getInt(quantityIndex));

        productNameTextView.setText(productName);
        priceView.setText(price);
        quantityView.setText(quantity);
    }
}
