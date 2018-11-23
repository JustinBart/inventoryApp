package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.android.inventoryapp.data.InventoryDbHelper;

public class MainActivity extends AppCompatActivity {

    // Database helper
    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new InventoryDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        // create and/or start database. Get it in read mode
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // populate db
        insetExampleInventoryItems();

        // Define the projection for which columns to use witht he query
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        Cursor cursor = db.query(
                InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        TextView displayView = findViewById(R.id.all_db_items);

        try {
            displayView.setText("The inventory table contains " + cursor.getCount() + " items.\n\n");
            displayView.append(InventoryEntry._ID + " - " +
                    InventoryEntry.COLUMN_PRODUCT_NAME + " - " +
                    InventoryEntry.COLUMN_PRICE + " - " +
                    InventoryEntry.COLUMN_QUANTITY + " - " +
                    InventoryEntry.COLUMN_SUPPLIER_NAME + " - " +
                    InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n"
            );

            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int sNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            while(cursor.moveToNext()) {
                int currentId = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentPrice = cursor.getString(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSName = cursor.getString(sNameColumnIndex);
                String currentPhone = cursor.getString(phoneColumnIndex);

                // append string to add in found db items
                displayView.append(("\n" + currentId + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSName + " - " +
                        currentPhone + " - "
                ));
            }
        } finally {
            // Always close the cursor
            cursor.close();
        }
    }

    private void insetExampleInventoryItems () {
        // get the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // create the first ContentValues object where coulmns names are the keys
        ContentValues valuesOne = new ContentValues();
        valuesOne.put(InventoryEntry.COLUMN_PRODUCT_NAME, "New Item");
        valuesOne.put(InventoryEntry.COLUMN_PRICE, "$30");
        valuesOne.put(InventoryEntry.COLUMN_QUANTITY, 3);
        valuesOne.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Bob's Items");
        valuesOne.put(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "9109109100");

        // create the second ContentValues object where coulmns names are the keys
        ContentValues valuesSecond = new ContentValues();
        valuesSecond.put(InventoryEntry.COLUMN_PRODUCT_NAME, "Other Item");
        valuesSecond.put(InventoryEntry.COLUMN_PRICE, "$10");
        valuesSecond.put(InventoryEntry.COLUMN_QUANTITY, 5);
        valuesSecond.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Shanna's Items");
        valuesSecond.put(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "1234567890");

        // inset the two new rows
        db.insert(InventoryEntry.TABLE_NAME, null, valuesOne);
        db.insert(InventoryEntry.TABLE_NAME, null, valuesSecond);
    }
}
