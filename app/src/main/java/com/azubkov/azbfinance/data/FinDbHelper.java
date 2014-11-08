package com.azubkov.azbfinance.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.azubkov.azbfinance.data.FinContract.AccountEntry;

/**
 * Created by oxymoron on 02.10.2014.
 */
public class FinDbHelper extends SQLiteOpenHelper{

    private static final int DB_VERSION = 1;

    public static final String DB_NAME = "azbfinance.db";

    private static final String SQL_ACCOUNT_CREATE =
            "create table " + AccountEntry.TABLE_NAME + " (" +
            AccountEntry._ID + " integer primary key," +
            AccountEntry.COLUMN_AMOUNT + " real not null, " +
            AccountEntry.COLUMN_BANK + " text not null, " +
            AccountEntry.COLUMN_CURRENCY + " real not null);";

    private static final String SQL_ACCOUNT_DELETE = "drop table if exists " + AccountEntry.TABLE_NAME + ";";


    public FinDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_ACCOUNT_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_ACCOUNT_DELETE);
        onCreate(db);
    }
}
