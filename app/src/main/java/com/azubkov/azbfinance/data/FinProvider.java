package com.azubkov.azbfinance.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by oxymoron on 02.10.2014.
 */
public class FinProvider extends ContentProvider{

    private static final UriMatcher matcher = buildUriMatcher();
    private static final int ACCOUNT = 100;
    private static final int ACCOUNT_ID = 101;

    private FinDbHelper openHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher m = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FinContract.CONTENT_AUTHORITY;

        m.addURI(authority, FinContract.PATH_ACCOUNT, ACCOUNT);
        m.addURI(authority, FinContract.PATH_ACCOUNT + "/#", ACCOUNT_ID);

        return m;
    }


    @Override
    public boolean onCreate() {
        openHelper = new FinDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;
        switch (matcher.match(uri)){
            case ACCOUNT:
                cursor = openHelper.getReadableDatabase().query(
                        FinContract.AccountEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case ACCOUNT_ID:
                cursor = openHelper.getReadableDatabase().query(
                        FinContract.AccountEntry.TABLE_NAME,
                        projection,
                        FinContract.AccountEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (matcher.match(uri)){
            case ACCOUNT:
                return FinContract.AccountEntry.CONTENT_TYPE;
            case ACCOUNT_ID:
                return FinContract.AccountEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
