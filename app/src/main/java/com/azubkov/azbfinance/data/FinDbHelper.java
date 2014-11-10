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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.azubkov.azbfinance.data.FinContract.AccountEntry;
import com.azubkov.azbfinance.data.FinContract.CurrencyEntry;
import com.azubkov.azbfinance.data.FinContract.RateEntry;

/**
 * Created on 09.11.14.
 *
 * @author Andrey Zubkov
 */
public class FinDbHelper extends SQLiteOpenHelper{

    private static final int DB_VERSION = 2;

    public static final String DB_NAME = "azbfinance.db";

    private static final String SQL_ACCOUNT_CREATE =
            "create table " + AccountEntry.TABLE_NAME + " (" +
            AccountEntry._ID + " integer primary key," +
            AccountEntry.COLUMN_AMOUNT + " real not null, " +
            AccountEntry.COLUMN_BANK + " text not null, " +
                    AccountEntry.COLUMN_CURRENCY + " integer not null);";

    private static final String SQL_CURRENCY_CREATE =
            "create table " + CurrencyEntry.TABLE_NAME + " (" +
                    CurrencyEntry._ID + " integer primary key," +
                    CurrencyEntry.COLUMN_CURR + " text not null);";

    private static final String SQL_RATE_CREATE =
            "create table " + RateEntry.TABLE_NAME + " (" +
                    RateEntry._ID + " integer primary key," +
                    RateEntry.COLUMN_TIMESTAMP + " integer not null, " +
                    RateEntry.COLUMN_FROM_CURR + " integer, " +
                    RateEntry.COLUMN_TO_CURR + " integer, " +
                    RateEntry.COLUMN_VALUE + " real not null);";

    private static final String SQL_ACCOUNT_DELETE = "drop table if exists " + AccountEntry.TABLE_NAME + ";";
    private static final String SQL_CURRENCY_DELETE = "drop table if exists " + CurrencyEntry.TABLE_NAME + ";";
    private static final String SQL_RATE_DELETE = "drop table if exists " + RateEntry.TABLE_NAME + ";";


    public FinDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_ACCOUNT_CREATE);
        db.execSQL(SQL_CURRENCY_CREATE);
        db.execSQL(SQL_RATE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_ACCOUNT_DELETE);
        db.execSQL(SQL_CURRENCY_DELETE);
        db.execSQL(SQL_RATE_DELETE);
        onCreate(db);
    }
}
