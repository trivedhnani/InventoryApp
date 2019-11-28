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
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by Hp on 10/8/2018.
 */

public class EditItem extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private TextView editname;
    private TextView editPrice;
    private TextView editQunatity;
    private TextView editsupname;
    private TextView editsupnum;
    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri mCurrentProductUri;
    private Button inc;
    private Button dec;
    private Button call;
    int quantity;
    long number;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_view);

        editname = (TextView) findViewById(R.id.item_name);
        editPrice = (TextView) findViewById(R.id.item_price);
        editQunatity = (TextView) findViewById(R.id.edit_item_quantity);
        editsupname = (TextView) findViewById(R.id.item_supplier_name);
        editsupnum = (TextView) findViewById(R.id.item_supplier_phone);
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if (mCurrentProductUri == null) {
            invalidateOptionsMenu();
        } else {
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }
        inc = (Button) findViewById(R.id.increment);
        dec = (Button) findViewById(R.id.decrement);
        call = (Button) findViewById(R.id.call);
        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = quantity + 1;
                editQunatity.setText(Integer.toString(quantity));
            }
        });
        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity <= 0) {
                    Toast.makeText(EditItem.this, R.string.sell_neg, Toast.LENGTH_SHORT).show();
                    return;
                }
                quantity = quantity - 1;
                editQunatity.setText(Integer.toString(quantity));
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
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
                mCurrentProductUri,
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
            long current_number = cursor.getLong(supNumColumnIndex);
            quantity = current_quantity;
            number = current_number;
            editname.setText(current_name);
            editPrice.setText(Integer.toString(current_price));
            editQunatity.setText(Integer.toString(current_quantity));
            editsupname.setText(current_sup_name);
            editsupnum.setText(Long.toString(current_number));
        }
    }

    private void saveProduct() {
        String quntityString = editQunatity.getText().toString().trim();
        ContentValues values = new ContentValues();
        if (mCurrentProductUri == null && TextUtils.isEmpty(quntityString)) {
            return;
        }
        quantity = Integer.parseInt(quntityString);
        values.put(InventoryContract.InventoryEntry.COLUMN_Quantity, quantity);
        // Determine if this is a new or existing item by checking if mCurrentItemUri is null or not
        if (mCurrentProductUri == null) {

            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mCurrentProductUri == null) {
            MenuItem menuItem = (MenuItem) findViewById(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.editpro:
                Intent intent = new Intent(this, EditorActivity.class);
                intent.setData(mCurrentProductUri);
                startActivity(intent);
                return true;
            case R.id.home:
                saveProduct();
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        // Only perform the delete if this is an existing item.
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
