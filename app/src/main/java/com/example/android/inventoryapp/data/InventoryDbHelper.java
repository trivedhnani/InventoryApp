package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hp on 9/26/2018.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shop.db";
    private static final int DATABASE_VERSION = 2;

         @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_PRODUCTS_TABLE =  "CREATE TABLE " + InventoryContract.InventoryEntry.TABLE_NAME + " ("
                + InventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.InventoryEntry.COLUMN_Product_Name + " TEXT, "
                + InventoryContract.InventoryEntry.COLUMN_PRICE + " INTEGER, "
                + InventoryContract.InventoryEntry.COLUMN_Quantity + " INTEGER, "
                + InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME + " TEXT, "
                + InventoryContract.InventoryEntry.COLUMN_SUPPLIER_Phno + " INTEGER);";
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);


    }
}
