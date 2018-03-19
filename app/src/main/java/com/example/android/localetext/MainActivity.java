/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.localetext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * This app demonstrates how to localize an app with text, an image,
 * a floating action button, an options menu, and the app bar.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private NumberFormat mNumberFormat = NumberFormat.getInstance();

    private int mInputQuantity = 1;

    // Fixed price in U.S. dollars and cents: ten cents.
    private double mPrice = 0.10;
    // Exchange rates for Spain (ES) and Israel (IW).
    private double mEsExchangeRate = 0.93; // 0.93 euros = $1
    private double mIwExchangeRate = 3.61; // 3.61 new shekels = $1

    private NumberFormat mCurrencyFormat = NumberFormat.getCurrencyInstance();

    /**
     * Creates the view with a toolbar for the options menu
     * and a floating action button, and initialize the
     * app data.
     *
     * @param savedInstanceState Bundle with activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHelp();
            }
        });

        // Get the current date, add 5 days (in ms) to create expiration date, set that to display.
        final Date myDate = new Date();
        final long expirationDate = myDate.getTime() + TimeUnit.DAYS.toMillis(5);
        myDate.setTime(expirationDate);
        // Format date for locale
        String myFormattedDate = DateFormat.getDateInstance().format(myDate);
        // Display formatted date
        TextView expirationDateView = findViewById(R.id.date);
        expirationDateView.setText(myFormattedDate);

        // Set up the price and currency format.
        String myFormattedPrice;
        String deviceLocale = Locale.getDefault().getCountry();

        switch (deviceLocale){
            case "ES":
                mPrice *= mEsExchangeRate;
                break;
            case "IL":
                mPrice *= mIwExchangeRate;
                break;
            default:
                mCurrencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        }
        myFormattedPrice = mCurrencyFormat.format(mPrice);
        

        /**
        if (deviceLocale.equals("ES") || deviceLocale.equals("IL")) {
            if (deviceLocale.equals("ES")) {
                mPrice *= mEsExchangeRate;
            } else {
                mPrice *= mIwExchangeRate;
            }
            myFormattedPrice = mCurrencyFormat.format(mPrice);
        } else {
            mCurrencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            myFormattedPrice = mCurrencyFormat.format(mPrice);
        }
        **/

        TextView localePrice = findViewById(R.id.price);
        localePrice.setText(myFormattedPrice);

        // Add an onEditorActionListener to the EditText
        final EditText enteredQuantity = findViewById(R.id.quantity);
        enteredQuantity.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // close keyboard
                    InputMethodManager imm = (InputMethodManager)
                            v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    // parse quantity EditText String in View v to int
                    try {
                        mInputQuantity = mNumberFormat.parse(v.getText().toString()).intValue();
                        v.setError(null);
                    } catch (ParseException e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                        v.setError(getText(R.string.quantity_hint));
                        return false;
                    }
                    // convert to string using locale's number format
                    String myFormattedQuantity = mNumberFormat.format(mInputQuantity);
                    v.setText(myFormattedQuantity);
                    return true;
                }
                return false;
            }
        });


    }

    /**
     * Shows the Help screen.
     */
    private void showHelp() {
        // Create the intent.
        Intent helpIntent = new Intent(this, HelpActivity.class);
        // Start the HelpActivity.
        startActivity(helpIntent);
    }

    /**
     * Show device Locale settings
     */
    private void showLocale() {
        Intent languageIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        startActivity(languageIntent);
    }

    /**
     * Creates the options menu and returns true.
     *
     * @param menu       Options menu
     * @return boolean   True after creating options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handles options menu item clicks.
     *
     * @param item      Menu item
     * @return boolean  True if menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle options menu item clicks here.
        switch (item.getItemId()) {
            case R.id.action_help:
                showHelp();
                return true;
            case R.id.action_language:
                showLocale();
                return true;
            default:
                // nada
        }
        return super.onOptionsItemSelected(item);
    }
}
