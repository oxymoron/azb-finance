/*
 * Copyright 2014 Andrey Zubkov. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.azubkov.azbfinance.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.azubkov.azbfinance.data.FinContract.Accounts;
import com.azubkov.azbfinance.data.FinContract.Currencies;
import com.azubkov.azbfinance.data.FinContract.Rates;
import com.azubkov.azbfinance.data.FinDatabase.Tables;
import com.azubkov.azbfinance.util.SelectionBuilder;

import java.util.Arrays;

/**
 * Created on 09.11.14.
 *
 * @author Andrey Zubkov
 */
public class FinProvider extends ContentProvider {

    private static String LOG_TAG = FinProvider.class.getSimpleName();

    private static final int ACCOUNTS = 100;
    private static final int ACCOUNTS_ID = 101;

    private static final int RATES = 200;
    private static final int RATES_ID = 201;

    private static final int CURRENCIES = 300;
    private static final int CURRENCIES_ID = 301;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private FinDatabase openHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FinContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "accounts", ACCOUNTS);
        matcher.addURI(authority, "accounts/*", ACCOUNTS_ID);

        matcher.addURI(authority, "rates", RATES);
        matcher.addURI(authority, "rates/*", RATES_ID);

        matcher.addURI(authority, "currencies", CURRENCIES);
        matcher.addURI(authority, "currencies/*", CURRENCIES_ID);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        openHelper = new FinDatabase(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case ACCOUNTS:
                return Accounts.CONTENT_TYPE;
            case ACCOUNTS_ID:
                return Accounts.CONTENT_ITEM_TYPE;
            case RATES:
                return Rates.CONTENT_TYPE;
            case RATES_ID:
                return Rates.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        final SQLiteDatabase db = openHelper.getReadableDatabase();
        final int match = uriMatcher.match(uri);
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
            Log.v(LOG_TAG, "uri=" + uri + " match=" + match + " proj=" + Arrays.toString(projection) +
                    " selection=" + selection + " args=" + Arrays.toString(selectionArgs) + ")");
        }
        final SelectionBuilder builder = buildExpandedSelection(uri, match);
        Cursor cursor = builder
                .where(selection, selectionArgs)
                .query(db, projection, sortOrder);
        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.v(LOG_TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case ACCOUNTS: {
                db.insertOrThrow(Tables.Accounts, null, values);
                notifyChanges(uri);
                return Accounts.buildAccountUri(values.getAsString(Accounts.ACCOUNT_ID));
            }
            case RATES: {
                db.insertOrThrow(Tables.Rates, null, values);
                notifyChanges(uri);
                return Rates.buildRateUri(values.getAsString(Rates._ID));
            }
            case CURRENCIES: {
                db.insertOrThrow(Tables.Currencies, null, values);
                notifyChanges(uri);
                return Currencies.buildCurrencyUri(values.getAsString(Currencies.CURRENCY_ID));
            }
            default: {
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
            }
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v(LOG_TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int result = builder.where(selection, selectionArgs).update(db, values);
        notifyChanges(uri);
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.v(LOG_TAG, "delete(uri=" + uri + ")");
        if (uri == FinContract.BASE_CONTENT_URI) {
            deleteDatabase();
            notifyChanges(uri);
            return 1;
        }
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int result = builder.where(selection, selectionArgs).delete(db);
        notifyChanges(uri);
        return result;
    }

    private void deleteDatabase() {
        openHelper.close();
        Context context = getContext();
        FinDatabase.deleteDatabase(context);
        openHelper = new FinDatabase(context);
    }

    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case RATES: {
                return builder.table(Tables.Rates);
            }
            case RATES_ID: {
                final String rateId = Rates.getRateId(uri);
                return builder.table(Tables.Rates)
                        .where(Rates._ID + "=?", rateId);
            }
            case ACCOUNTS: {
                return builder.table(Tables.Accounts);
            }
            case ACCOUNTS_ID: {
                final String accountId = Accounts.getAccountId(uri);
                return builder.table(Tables.Accounts)
                        .where(Accounts.ACCOUNT_ID + "=?", accountId);
            }
            case CURRENCIES: {
                return builder.table(Tables.Currencies);
            }
            case CURRENCIES_ID: {
                final String currencyId = Currencies.getCurrencyId(uri);
                return builder.table(Tables.Currencies)
                        .where(Currencies.CURRENCY_ID + "=?", currencyId);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri for " + match + ": " + uri);
            }
        }
    }

    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case RATES: {
                return builder.table(Tables.Rates);
            }
            case RATES_ID: {
                final String rateId = Rates.getRateId(uri);
                return builder.table(Tables.Rates)
                        .where(Rates._ID + "=?", rateId);
            }
            case ACCOUNTS: {
                return builder.table(Tables.Accounts);
            }
            case ACCOUNTS_ID: {
                final String accountId = Accounts.getAccountId(uri);
                return builder.table(Tables.Accounts)
                        .where(Accounts.ACCOUNT_ID + "=?", accountId);
            }
            case CURRENCIES: {
                return builder.table(Tables.Currencies);
            }
            case CURRENCIES_ID: {
                final String currencyId = Currencies.getCurrencyId(uri);
                return builder.table(Tables.Currencies)
                        .where(Currencies.CURRENCY_ID + "=?", currencyId);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri for " + match + ": " + uri);
            }
        }
    }

    private void notifyChanges(Uri uri) {
        // ToDo: apply separate logic for uris from sync adapter
        Context context = getContext();
        context.getContentResolver().notifyChange(uri, null);
    }
}
