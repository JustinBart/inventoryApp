package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {

    // Name of the database file
    private static final String DATABASE_NAME = "inventory.db";

    // database version, if the database version increments, this number must also increment
    private static final int DATABASE_VERSION = 1;

    /**
     * constructs a new instance of InventoryDbHelper
     * @param context context of the app
     */
    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create the inventory table
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_PRICE + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_SUPPLIER_NAME + " TEXT, "
                + InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT);";

        // execute the SQL statement
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, there is nothing yet to be done here
    }
}
