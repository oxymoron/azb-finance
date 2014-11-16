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

import com.azubkov.azbfinance.data.FinContract.Accounts;
import com.azubkov.azbfinance.data.FinContract.Currencies;
import com.azubkov.azbfinance.data.FinContract.Rates;

/**
 * Created on 09.11.14.
 *
 * @author Andrey Zubkov
 */
public class FinDatabase extends SQLiteOpenHelper {

    private static final int DB_VERSION = 4;

    public static final String DB_NAME = "azbfinance.db";

    public interface Tables {
        String Accounts = "accounts";
        String Rates = "rates";
        String Currencies = "currencies";
    }

    private static final String SQL_ACCOUNT_CREATE =
            "create table " + Tables.Accounts + " (" +
                    Accounts._ID + " integer primary key autoincrement," +
                    Accounts.ACCOUNT_ID + " text not null," +
                    Accounts.ACCOUNT_AMOUNT + " real not null, " +
                    Accounts.ACCOUNT_BANK + " text not null, " +
                    Accounts.ACCOUNT_CURRENCY + " text not null);";

    private static final String SQL_CURRENCY_CREATE =
            "create table " + Tables.Currencies + " (" +
                    Currencies._ID + " integer primary key autoincrement," +
                    Currencies.CURRENCY_ID + " text not null," +
                    Currencies.CURRENCY_NAME + " text not null);";

    private static final String SQL_RATE_CREATE =
            "create table " + Tables.Rates + " (" +
                    Rates._ID + " integer primary key autoincrement," +
                    Rates.RATE_TIMESTAMP + " integer not null, " +
                    Rates.RATE_CURRENCY + " text not null, " +
                    Rates.RATE_VALUE + " real not null);";

    private static final String SQL_ACCOUNT_DELETE = "drop table if exists " + Tables.Accounts + ";";
    private static final String SQL_CURRENCY_DELETE = "drop table if exists " + Tables.Currencies + ";";
    private static final String SQL_RATE_DELETE = "drop table if exists " + Tables.Rates + ";";

    private final Context context;

    public FinDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
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

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DB_NAME);
    }


}
