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

import com.azubkov.azbfinance.data.FinContract.AccountEntry;
import com.azubkov.azbfinance.data.FinContract.CurrencyEntry;
import com.azubkov.azbfinance.data.FinContract.RateEntry;
import com.azubkov.azbfinance.data.FinDbHelper;

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
        db = new FinDbHelper(mContext).getWritableDatabase();
    }

    public void testCreateDb() throws Exception {
        mContext.deleteDatabase(FinDbHelper.DB_NAME);
        assertTrue(db.isOpen());
        db.close();
    }

    public void testAccounts() throws Exception {
        ContentValues values = new ContentValues();
        values.put(AccountEntry.COLUMN_BANK, "KEB");
        values.put(AccountEntry.COLUMN_AMOUNT, 10000.2);
        values.put(AccountEntry.COLUMN_CURRENCY, 1);
        validateInsertAndQuery(AccountEntry.TABLE_NAME, values);
    }

    public void testCurrency() throws Exception {
        ContentValues values = new ContentValues();
        values.put(CurrencyEntry.COLUMN_CURR, Curr.KRW.toString());
        validateInsertAndQuery(CurrencyEntry.TABLE_NAME, values);
    }

    public void testRate() throws Exception {
        ContentValues values = new ContentValues();
        values.put(RateEntry.COLUMN_TIMESTAMP, new Date().getTime());
        values.put(RateEntry.COLUMN_FROM_CURR, 1);
        values.put(RateEntry.COLUMN_TO_CURR, 2);
        values.put(RateEntry.COLUMN_VALUE, 46.2345);
        validateInsertAndQuery(RateEntry.TABLE_NAME, values);
    }

    private void validateInsertAndQuery(String table, ContentValues values) {
        long id = db.insert(table, null, values);
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
