package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_ITEM_LOADER = 0;
    public EditText name;
    public EditText quantity;
    public EditText price;
    public EditText supplier_name;
    public EditText supplier_phnum;
    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private Uri mCurrentProdUri;
    private boolean productChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_layout);
        Intent intent = getIntent();
        mCurrentProdUri = intent.getData();


        if (mCurrentProdUri == null) {

            setTitle(getString(R.string.editor_activity_title_new_product));

            invalidateOptionsMenu();
        } else {

            setTitle(getString(R.string.editor_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }
        name = (EditText) findViewById(R.id.proname);
        quantity = (EditText) findViewById(R.id.quantity);
        price = (EditText) findViewById(R.id.price);
        supplier_name = (EditText) findViewById(R.id.supname);
        supplier_phnum = (EditText) findViewById(R.id.supnum);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        name.setOnTouchListener(mTouchListener);
        price.setOnTouchListener(mTouchListener);
        quantity.setOnTouchListener(mTouchListener);
        supplier_name.setOnTouchListener(mTouchListener);
        supplier_phnum.setOnTouchListener(mTouchListener);

    }

    private void saveproduct() {
        String nameString = name.getText().toString().trim();
        String quant = quantity.getText().toString().trim();
        String p = price.getText().toString().trim();
        String supplierString = supplier_name.getText().toString().trim();
        String numString = supplier_phnum.getText().toString().trim();
        int quantitystring = 0;
        int pricestring = 0;
        long num=0;
        ContentValues values= new ContentValues();
        if(TextUtils.isEmpty(nameString)&&TextUtils.isEmpty(quant)&&TextUtils.isEmpty(p)
                &&TextUtils.isEmpty(supplierString)&&TextUtils.isEmpty(numString)){

            Toast.makeText(this,R.string.enter_all_fields,Toast.LENGTH_SHORT).show();
            return;
        }
            if (TextUtils.isEmpty(nameString)) {
                Toast.makeText(this, R.string.name_required, Toast.LENGTH_SHORT).show();
                return;
            } else{
                values.put(InventoryContract.InventoryEntry.COLUMN_Product_Name, nameString);
            }

            if (TextUtils.isEmpty(quant)) {
                Toast.makeText(this, R.string.quantity_required, Toast.LENGTH_SHORT).show();
                return;
            } else {
                quantitystring = Integer.parseInt(quant);
                values.put(InventoryContract.InventoryEntry.COLUMN_Quantity, quantitystring);
            }
            if (TextUtils.isEmpty(p)) {
                Toast.makeText(this, R.string.price_required, Toast.LENGTH_SHORT).show();
                return;
            } else {
                pricestring = Integer.parseInt(p);
                values.put(InventoryContract.InventoryEntry.COLUMN_PRICE, pricestring);
            }
            if (TextUtils.isEmpty(supplierString)) {
                Toast.makeText(this, R.string.suppl_name_required, Toast.LENGTH_SHORT).show();
                return;
            } else
                values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, supplierString);

            if (TextUtils.isEmpty(numString)) {
                Toast.makeText(this, R.string.suppl_phno_required, Toast.LENGTH_SHORT).show();
                return;
            } else
                num = Long.parseLong(numString);
                values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_Phno, num);



        if(mCurrentProdUri==null){    // Show a toast message depending on whether or not the insertion was successful
            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_success),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
         }else {
            // Show a toast message depending on whether or not the insertion was successful
            int rowsAffected = getContentResolver().update(mCurrentProdUri, values,null,null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_success),
                        Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        if (mCurrentProdUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveproduct();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!productChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!productChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_Product_Name,
                InventoryContract.InventoryEntry.COLUMN_PRICE,
                InventoryContract.InventoryEntry.COLUMN_Quantity,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_Phno
        };
        return new CursorLoader(this,
                mCurrentProdUri,
                projection,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_Product_Name);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
            int quantColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_Quantity);
            int supNameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME);
            int supNumColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_Phno);

            String current_name = cursor.getString(nameColumnIndex);
            int current_price = cursor.getInt(priceColumnIndex);
            int current_quantity = cursor.getInt(quantColumnIndex);
            String current_sup_name = cursor.getString(supNameColumnIndex);
            Long current_number = cursor.getLong(supNumColumnIndex);
            name.setText(current_name);
            price.setText(Integer.toString(current_price));
            quantity.setText(Integer.toString(current_quantity));
            supplier_name.setText(current_sup_name);
            supplier_phnum.setText(Long.toString(current_number));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        name.setText("");
        price.setText("");
        quantity.setText("");
        supplier_phnum.setText("");
        supplier_name.setText("");
    }

    private void deleteItem() {
        // Only perform the delete if this is an existing item.
        if (mCurrentProdUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProdUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Intent i = new Intent(EditorActivity.this, CatalogActivity.class);
                startActivity(i);
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}


