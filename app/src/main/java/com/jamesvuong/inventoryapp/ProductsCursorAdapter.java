package com.jamesvuong.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.jamesvuong.inventoryapp.data.ProductContract;
import com.jamesvuong.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by jvuonger on 9/30/16.
 */

public class ProductsCursorAdapter extends CursorAdapter {

    public ProductsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvName = (TextView) view.findViewById(R.id.name);
        TextView tvPrice = (TextView) view.findViewById(R.id.price);
        TextView tvCount = (TextView) view.findViewById(R.id.count);

        String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        Double price = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        int count = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_COUNT));

        tvName.setText(name);
        tvPrice.setText("$" + price.toString());
        tvCount.setText(String.valueOf(count));
    }

}
