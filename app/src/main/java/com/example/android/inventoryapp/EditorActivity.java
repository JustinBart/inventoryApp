package com.example.android.inventoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.LoaderManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the inventory loader
    private static final int EXISTING_INVENTORY_LOADER = 0;

    // Content URI for the existing inventory, it'll be null if it is a new pet
    private Uri mCurrentInventoryUri;

    // EditText field to enter a product name
    private EditText mProductNameEditText;

    // EditText field to enter a products price
    private EditText mPrice;

    // EditText field to enter the number of products
    private EditText mQuantity;

    // EditText field to enter a suppliers name
    private EditText mSupplierName;

    // EditText field to enter a suppliers phone number
    private EditText mSupplierPhoneNumber;

    // track if the inentory item has been updated
    private boolean mInventoryHasChanged = false;

    // assume if the user touches the view that they have modified the inentory item
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInventoryHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        FloatingActionButton plus = findViewById(R.id.fab_add);
        FloatingActionButton minus = findViewById(R.id.fab_minus);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOneQuantity();
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusOneQuantity();
            }
        });

        // check to see if we're creating a new inventory item or editing one that exists
        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();

        if (mCurrentInventoryUri == null) {
            // If this is a new pet change the title to say "Add new inventory"
            setTitle(getString(R.string.editor_activity_title_new_inventory));

            // invalidate the options menu, so that the "delete" menu option can be hidden
            invalidateOptionsMenu();
        } else {
            // set the title to read "Edit Inventory"
            setTitle(getString(R.string.editor_activity_title_edit_inventory));

            // init a loader to read the pet data from the database
            getLoaderManager()
                    .initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        // find all of the views to be edited
        mProductNameEditText = findViewById(R.id.edit_product_name);
        mPrice = findViewById(R.id.edit_price);
        mQuantity = findViewById(R.id.edit_quantity);
        mSupplierName = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumber = findViewById(R.id.edit_supplier_phone_number);

        // setup OnTouchListeners for all of the input fields
        mProductNameEditText.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mQuantity.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumber.setOnTouchListener(mTouchListener);
    }

    /**
     * Get the user inputs and save them to the database
     */
    private void saveInventory() {
        String productNameString = mProductNameEditText.getText().toString().trim();
        String priceString = mPrice.getText().toString().trim();
        String quantityString = mQuantity.getText().toString().trim();
        String supplierNameString = mSupplierName.getText().toString().trim();
        String supplierPhoneNumberString = mSupplierPhoneNumber.getText().toString().trim();

        // don't save record without required fields
        if (TextUtils.isEmpty(productNameString)) {
            Toast.makeText(this, getString(R.string.product_name_required),
                    Toast.LENGTH_LONG).show();
            return;
        }

        // don't save record without required
        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.price_is_required),
                    Toast.LENGTH_LONG).show();
            return;
        }

        // don't save record without required
        if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, getString(R.string.quntity_is_requried),
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Create a ContentValues object where the column names are the news
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(InventoryEntry.COLUMN_PRICE, priceString);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);

        // set the default value to 0 if no number or a negative number is entered
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        // If the quantity is less then 0 set it to 0, no negative numbers in the database
        if (quantity < 0) {
            quantity = 0;
        }
        values.put(InventoryEntry.COLUMN_QUANTITY, quantity);

        // Show a toast message depeding on whether or not the insertion was successful
        if (mCurrentInventoryUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            // If the new content URI is null, then there was an error with the insertion
            if (newUri == null) {
                Toast.makeText(
                        this,
                        getString(R.string.editor_insert_inventory_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // else the insertion was successful and we'll display the success toast
                Toast.makeText(
                        this,
                        getString(R.string.editor_insert_inventory_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Check if this is an existing pet
            int rowsAffected = getContentResolver()
                    .update(mCurrentInventoryUri, values, null, null);

            // show a toast depending on whether or not the update was successful
            if (rowsAffected == 0) {
                Toast.makeText(
                        this,
                        getString(R.string.editor_insert_inventory_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // else the insertion was successful and we'll display the success toast
                Toast.makeText(
                        this,
                        getString(R.string.editor_insert_inventory_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentInventoryUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    /**
     * Add one to the quantity
     */
    private void addOneQuantity() {
        Integer quantity = Integer.valueOf(mQuantity.getText().toString());
        if (quantity < 0) {
            quantity = 0;
        }
        quantity += 1;
        mQuantity.setText(quantity.toString());
    }

    /**
     * Subtract one from the quantity
     */
    private void minusOneQuantity() {
        Integer quantity = Integer.valueOf(mQuantity.getText().toString());
        if (quantity <= 0) {
            quantity = 0;
        } else {
            quantity -= 1;
        }
        mQuantity.setText(quantity.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // call the supplier
            case R.id.order:
                order();
                return true;
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save inventory to database
                saveInventory();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the inventory hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mInventoryHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Call supplier to order more
     */
    private void order() {
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(EditorActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);

                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + mSupplierPhoneNumber.getText().toString()));
                startActivity(callIntent);

            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + mSupplierPhoneNumber.getText().toString()));
                startActivity(callIntent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the inventory hasn't changed, continue with handling back button press
        if (!mInventoryHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all inventory attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentInventoryUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int productNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor
                    .getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER);


            // Extract out the value from the Cursor for the given column index
            String produceName = cursor.getString(productNameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            mProductNameEditText.setText(produceName);
            mPrice.setText(price);
            mQuantity.setText(Integer.toString(quantity));
            mSupplierName.setText(supplierName);
            mSupplierPhoneNumber.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mProductNameEditText.setText("");
        mPrice.setText("");
        mQuantity.setText("");
        mSupplierName.setText("");
        mSupplierPhoneNumber.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     *
     * @param discardButtonClickListener the click listener
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the inventory item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // Only perform the delete if this is an existing inventory.
        if (mCurrentInventoryUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentInventoryUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver()
                    .delete(mCurrentInventoryUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_inventory_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_inventory_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
