package com.jamesvuong.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.jamesvuong.inventoryapp.data.ProductContract;
import com.jamesvuong.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by jvuonger on 9/30/16.
 */

public class EditorActivity extends AppCompatActivity{
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
    }

    private void saveProduct() {
        String nameString = mNameEditText.getText().toString().trim();
        String countString = mCountEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();

        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(countString) &&
                TextUtils.isEmpty(priceString)) {return;}

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_COUNT, Integer.valueOf(countString));
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, Double.valueOf(priceString));

        if(mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
                Toast.makeText(this, getString(R.string.editor_insert_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values,null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_successful),
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                saveProduct();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:

                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
