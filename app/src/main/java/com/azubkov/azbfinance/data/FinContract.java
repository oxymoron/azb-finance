package com.azubkov.azbfinance.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by oxymoron on 02.10.2014.
 */
public class FinContract {

    public static final String CONTENT_AUTHORITY = "com.azubkov.azbfinance";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ACCOUNT = "account";

    public static final class AccountEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCOUNT).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_ACCOUNT;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_ACCOUNT;


        public static final String TABLE_NAME = "account";

        public static final String COLUMN_BANK = "bank";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_CURRENCY = "currency";

        public static Uri buildAccountUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
