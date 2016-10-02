package com.jamesvuong.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.jamesvuong.inventoryapp.data.ProductContract;
import com.jamesvuong.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by jvuonger on 9/30/16.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int PRODUCT_LOADER = 1;

    private Uri mCurrentProductUri;

    EditText mNameEditText;
    EditText mCountEditText;
    EditText mPriceEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mNameEditText = (EditText) findViewById(R.id.edit_text_product_name);
        mCountEditText = (EditText) findViewById(R.id.edit_text_product_count);
        mPriceEditText = (EditText) findViewById(R.id.edit_text_product_price);

        mPriceEditText.setFilters(new InputFilter[] {
                new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {
                    int beforeDecimal = 5, afterDecimal = 2;

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        String temp = mPriceEditText.getText() + source.toString();

                        if (temp.equals(".")) {
                            return "0.";
                        }
                        else if (temp.toString().indexOf(".") == -1) {
                            // no decimal point placed yet
                            if (temp.length() > beforeDecimal) {
                                return "";
                            }
                        } else {
                            temp = temp.substring(temp.indexOf(".") + 1);
                            if (temp.length() > afterDecimal) {
                                return "";
                            }
                        }

                        return super.filter(source, start, end, dest, dstart, dend);
                    }
                }
        });

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if(mCurrentProductUri == null) {
            setTitle(R.string.editor_title_add);
        } else {
            setTitle(R.string.editor_title_edit);
            getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,   // Parent activity context
                mCurrentProductUri,        // Table to query
                null,     // Projection to return
                null,            // No selection clause
                null,            // No selection arguments
                null             // Default sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int countColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_COUNT);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int count = cursor.getInt(countColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);

            mNameEditText.setText(name);
            mCountEditText.setText(Integer.toString(count));
            mPriceEditText.setText(Double.toString(price));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void saveProduct() {
        String nameString = mNameEditText.getText().toString().trim();
        String countString = mCountEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(countString)
                || TextUtils.isEmpty(priceString)) {
            Toast.makeText(this,"Please fill out all values", Toast.LENGTH_SHORT).show();
            return;
        }

        int count = Integer.valueOf(countString);
        double price = Double.valueOf(priceString);

        if(count < 0) {
            Toast.makeText(this,"You must input a real number for the count field.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(price < 0.0) {
            Toast.makeText(this,"You must input a real price.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_COUNT, count);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);

        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_failed), Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, getString(R.string.editor_insert_successful), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveProduct();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
