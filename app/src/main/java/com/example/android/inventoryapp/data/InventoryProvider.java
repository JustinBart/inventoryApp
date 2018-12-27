package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import java.net.URI;

public class InventoryProvider extends ContentProvider {

    // Tag for logging
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    // URI code for the content URI in the inventory table
    public static final int INVENTORY = 100;

    // URI code for the content URI for a single inventory item in the inventory table
    public static final int INVENTORY_ID = 101;

    // UriMatch object to match content URI to a corresponding code
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // will match the form "content://com.example.android.inventoryapp/inventory" and will map
        // to integer code INVENTORY
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.PATH_INVENTORY,
                INVENTORY);

        // will match the form "content://com.example.android.inventoryapp/inventory/1" and will map
        // to integer code INVENTORY_ID
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.PATH_INVENTORY + "/#",
                INVENTORY_ID);
    }

    // Database helper object
    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get a readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // cursor will hold the reults of the query
        Cursor cursor;

        // Identify the specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                // For the INVENTORY code, query the stock table directly with the given projection,
                // selection, selection arguments, and sort order. The cursor can contain
                // multiple rows of the stock table
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the stock table where the _id is equal to the
                // associated row of the table
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknow URI " + uri);
        }

        // set the notification URI on the Cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertInventory(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert an inventory item into the database with the given content values.
     */
    private Uri insertInventory(Uri uri, ContentValues values) {
        // check that name is not null
        String name = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Inventory requires a name");
        }

        // check that price is not null
        String price = values.getAsString(InventoryEntry.COLUMN_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Inventory requires a price");
        }

        // check that quantity is not null
        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_QUANTITY);
        if (quantity == null && quantity < 0) {
            throw new IllegalArgumentException("Inventory requires a valid quantity");
        }

        // supplier and supplier number can be null

        // get a writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert fow for " + uri);
            return null;
        }

        // notify all listenerst hat the data has changed
        getContext().getContentResolver().notifyChange(uri, null);

        // return new URI with the ID of the newly inserted row
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateInventory(uri, values, selection, selectionArgs);
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInventory(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateInventory(Uri uri, ContentValues values, String selection,
                                String[] selectionArgs) {
        // check that name is not null
        String name = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Inventory requires a name");
        }

        // check that price is not null
        String price = values.getAsString(InventoryEntry.COLUMN_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Inventory requires a price");
        }

        // check that quantity is not null
        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_QUANTITY);
        if (quantity == null && quantity < 0) {
            throw new IllegalArgumentException("Inventory requires a valid quantity");
        }

        // supplier and supplier number can be null

        // if there are no values do not try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Get a writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Do the update
        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        // if one or more rows were updated then notify all listeners
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return the number of rows that were updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selction, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selction, selectionArgs);
                break;
            case INVENTORY_ID:
                selction = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selction, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // if one or more rows were deleted then notify all listeners
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return the number of rows that were deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
