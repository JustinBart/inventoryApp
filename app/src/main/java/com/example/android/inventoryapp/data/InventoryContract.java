package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

public class InventoryContract {

    // empty constructor to prevent accidental instantiation
    private InventoryContract() {}

    public static final class InventoryEntry implements BaseColumns {

        // name of database table
        public final static String TABLE_NAME = "stock";

        // unique ID for each inventory item
        public final static String _ID = BaseColumns._ID;

        /**
         * name of product
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME = "name";

        /**
         * price of the inventory item
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRICE = "price";

        /**
         * How much stock do we have of the item
         *
         * Type: INTEGER
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * Name of the supplier
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_NAME = "supplier";

        /**
         * The phone number of the supplier
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "phone";
    }
}
