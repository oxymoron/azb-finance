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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created on 09.11.14.
 *
 * @author Andrey Zubkov
 */
public class FinContract {

    public static final String CONTENT_AUTHORITY = "com.azubkov.azbfinance";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ACCOUNTS = "accounts";
    public static final String PATH_CURRENCIES = "currencies";
    public static final String PATH_RATES = "rates";

    interface AccountColumns {
        String ACCOUNT_ID = "account_id";
        String ACCOUNT_BANK = "account_bank";
        String ACCOUNT_AMOUNT = "account_amount";
        String ACCOUNT_CURRENCY = "account_currency";
    }

    interface CurrencyColumns {
        String CURRENCY_ID = "currency_id";
        String CURRENCY_NAME = "currency_name";
    }

    interface RateColumns {
        String RATE_TIMESTAMP = "rate_timestamp";
        String RATE_CURRENCY = "rate_currency";
        String RATE_VALUE = "rate_value";
    }

    public static final class Accounts implements BaseColumns, AccountColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCOUNTS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_ACCOUNTS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_ACCOUNTS;

        public static Uri buildAccountUri(String accountId) {
            return CONTENT_URI.buildUpon().appendPath(accountId).build();
        }

        public static String getAccountId(Uri uri) {
            return uri.getLastPathSegment();
        }

    }

    public static final class Currencies implements BaseColumns, CurrencyColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CURRENCIES).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_CURRENCIES;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_CURRENCIES;

        public static Uri buildCurrencyUri(String currencyId) {
            return CONTENT_URI.buildUpon().appendPath(currencyId).build();
        }

        public static String getCurrencyId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    public static final class Rates implements BaseColumns, RateColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RATES).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_RATES;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_RATES;

        public static Uri buildRateUri(String rateId) {
            return CONTENT_URI.buildUpon().appendPath(rateId).build();
        }

        public static String getRateId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }



}
