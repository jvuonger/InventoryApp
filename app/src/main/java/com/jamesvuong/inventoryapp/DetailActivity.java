package com.jamesvuong.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jamesvuong.inventoryapp.data.ProductContract;
import com.jamesvuong.inventoryapp.data.ProductContract.ProductEntry;

import static android.content.ContentValues.TAG;

/**
 * Created by jvuonger on 9/30/16.
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int PRODUCT_LOADER = 1;

    private Uri mCurrentProductUri;

    TextView mNameTextView;
    TextView mCountTextView;
    TextView mPriceTextView;
    Button mDecreaseButton;
    Button mIncreaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mNameTextView = (TextView) findViewById(R.id.product_name);
        mCountTextView = (TextView) findViewById(R.id.product_availability);
        mPriceTextView = (TextView) findViewById(R.id.product_price);
        mDecreaseButton = (Button) findViewById(R.id.decrease_count_button);
        mIncreaseButton = (Button) findViewById(R.id.increase_count_button);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if(mCurrentProductUri != null) {
            setTitle(R.string.detail_view_title);
            getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }
    }

    private int adjustAvailability(Context context, Uri itemUri, int newCount) {
        if(newCount < 0) return 0;

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_COUNT, newCount );
        int numRowsUpdated = context.getContentResolver().update(itemUri, values, null, null);

        return numRowsUpdated;
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
            final int count = cursor.getInt(countColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);

            mNameTextView.setText(name);
            mCountTextView.setText(Integer.toString(count));
            mPriceTextView.setText(Double.toString(price));

            mDecreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adjustAvailability(getBaseContext(), mCurrentProductUri,count-1);
                }
            });

            mIncreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adjustAvailability(getBaseContext(), mCurrentProductUri,count+1);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
