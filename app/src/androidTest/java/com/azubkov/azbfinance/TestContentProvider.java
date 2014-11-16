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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;

import com.azubkov.azbfinance.data.FinContract;
import com.azubkov.azbfinance.data.FinContract.Rates;

import java.util.Date;

/**
 * Created on 13.11.14.
 *
 * @author Andrey Zubkov
 */
public class TestContentProvider extends AndroidTestCase {

    private static String LOG_TAG = TestContentProvider.class.getSimpleName();

    private ContentResolver resolver;

    @Override
    public void setUp() throws Exception {
        resolver = getContext().getContentResolver();
        resolver.delete(FinContract.BASE_CONTENT_URI, null, null);
    }

    public void testRates() throws Exception {
        ContentValues r1 = createRate(10.3, Curr.KRW);
        ContentValues r2 = createRate(30.2, Curr.EUR);
        ContentValues r3 = createRate(20.1, Curr.RUB);

        resolver.insert(Rates.CONTENT_URI, r1);
        resolver.insert(Rates.CONTENT_URI, r2);
        resolver.insert(Rates.CONTENT_URI, r3);

        Cursor cursor = resolver.query(Rates.CONTENT_URI, null, null, null, Rates._ID + " desc");
        assertEquals(3, cursor.getCount());

        Curr[] expected = new Curr[]{Curr.KRW, Curr.RUB, Curr.EUR};
        int index = 0;
        for (cursor.moveToFirst(); cursor.moveToNext(); ) {
            int columnIndex = cursor.getColumnIndex(Rates.RATE_CURRENCY);
            String currName = cursor.getString(columnIndex);
//            assertEquals(expected[index++], Curr.valueOf(currName));
            Log.d(LOG_TAG, currName);
        }

    }

    private ContentValues createRate(Double value, Curr curr) {
        ContentValues values = new ContentValues();
        values.put(Rates.RATE_VALUE, value);
        values.put(Rates.RATE_CURRENCY, curr.name());
        values.put(Rates.RATE_TIMESTAMP, new Date().getTime());
        return values;
    }
}
