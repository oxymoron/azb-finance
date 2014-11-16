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

package com.azubkov.azbfinance;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.azubkov.azbfinance.data.FinContract.Accounts;
import com.azubkov.azbfinance.data.FinContract.Currencies;
import com.azubkov.azbfinance.data.FinContract.Rates;
import com.azubkov.azbfinance.data.FinDatabase;
import com.azubkov.azbfinance.data.FinDatabase.Tables;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created on 10.11.14.
 *
 * @author Andrey Zubkov
 */
public class TestDatabase extends AndroidTestCase {

    private static String LOG_TAG = TestDatabase.class.getSimpleName();

    private SQLiteDatabase db;

    @Override
    protected void setUp() throws Exception {
        db = new FinDatabase(mContext).getWritableDatabase();
    }

    public void testCreateDb() throws Exception {
        mContext.deleteDatabase(FinDatabase.DB_NAME);
        assertTrue(db.isOpen());
        db.close();
    }

    public void testAccounts() throws Exception {
        ContentValues values = new ContentValues();
        values.put(Accounts._ID, 1);
        values.put(Accounts.ACCOUNT_ID, "1");
        values.put(Accounts.ACCOUNT_BANK, "KEB");
        values.put(Accounts.ACCOUNT_AMOUNT, 10000.2);
        values.put(Accounts.ACCOUNT_CURRENCY, 1);
        validateInsertAndQuery(Tables.Accounts, values);
    }

    public void testCurrency() throws Exception {
        ContentValues values = new ContentValues();
        values.put(Currencies.CURRENCY_ID, "1");
        values.put(Currencies.CURRENCY_NAME, Curr.KRW.toString());
        validateInsertAndQuery(Tables.Currencies, values);
    }

    public void testRate() throws Exception {
        ContentValues values = new ContentValues();
        values.put(Rates.RATE_TIMESTAMP, new Date().getTime());
        values.put(Rates.RATE_CURRENCY, 1);
        values.put(Rates.RATE_VALUE, 46.2345);
        validateInsertAndQuery(Tables.Rates, values);
    }

    private void validateInsertAndQuery(String table, ContentValues values) {
        long id = db.insertOrThrow(table, null, values);
        assertTrue(id != -1);
        Cursor cursor = db.query(
                table,
                null,
                null,
                null,
                null,
                null,
                null
        );
        validateCursor(cursor, values);
        db.close();
    }

    private void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }
}
