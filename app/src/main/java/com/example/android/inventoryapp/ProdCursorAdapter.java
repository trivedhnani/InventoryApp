package com.example.android.inventoryapp;

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
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;


/**
 * Created by Hp on 10/7/2018.
 */

public class ProdCursorAdapter extends CursorAdapter {
    Context mContext;

    public ProdCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the item data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current item can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        mContext = context;
        TextView nameTextView = view.findViewById(R.id.name);
        final TextView quantityTextView = view.findViewById(R.id.quant);
        TextView priceTextView = view.findViewById(R.id.price);

        int idColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_Product_Name);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_Quantity);

        String itemName = cursor.getString(nameColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        final String itemQuantity = cursor.getString(quantityColumnIndex);
        final int[] quantity = {Integer.parseInt(itemQuantity)};
        final String id = cursor.getString(idColumnIndex);

        nameTextView.setText(itemName);
        priceTextView.setText(itemPrice);
        quantityTextView.setText(itemQuantity);

        Button sellButton = (Button) view.findViewById(R.id.button_sell);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity[0] == 0) {
                    Toast.makeText(mContext, (R.string.sell_neg), Toast.LENGTH_SHORT).show();
                } else {
                    quantity[0] = quantity[0] - 1;
                    ContentValues values = new ContentValues();
                    values.put(InventoryContract.InventoryEntry.COLUMN_Quantity, quantity[0]);
                    quantityTextView.setText(quantity[0] + "");
                    Uri currentItemUri = Uri.withAppendedPath(InventoryContract.InventoryEntry.CONTENT_URI, id);
                    mContext.getContentResolver().update(currentItemUri,
                            values, null, null);
                }
            }
        });
    }
}
