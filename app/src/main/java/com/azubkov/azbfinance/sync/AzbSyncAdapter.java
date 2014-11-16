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

package com.azubkov.azbfinance.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.azubkov.azbfinance.R;
import com.azubkov.azbfinance.data.FinContract.Rates;
import com.azubkov.azbfinance.model.Rate;
import com.azubkov.azbfinance.sync.rateprovider.RateProvider;
import com.azubkov.azbfinance.sync.rateprovider.YahooApiProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created on 09.11.14.
 *
 * @author Andrey Zubkov
 */
public class AzbSyncAdapter extends AbstractThreadedSyncAdapter {

    private static String LOG_TAG = AzbSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 10000;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private final RateProvider rateProvider;


    public AzbSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        rateProvider = new YahooApiProvider();
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              SyncResult syncResult) {
        Log.d(LOG_TAG, "Syncing...");
        try {
            List<Rate> rateList = rateProvider.request();
            List<ContentValues> values = new ArrayList<ContentValues>();
            for (Rate r : rateList) {
                ContentValues v = new ContentValues();
                v.put(Rates.RATE_TIMESTAMP, r.getDate().getTime());
                v.put(Rates.RATE_VALUE, r.getValue());
                v.put(Rates.RATE_CURRENCY, r.getCurr().name());
                values.add(v);
            }
            ContentResolver resolver = getContext().getContentResolver();
            ContentValues[] valuesArray = values.toArray(new ContentValues[rateList.size()]);
            resolver.bulkInsert(Rates.CONTENT_URI, valuesArray);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Rate provider e");
        }

    }


    public static void initSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(
                context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type)
        );
        if (accountManager.getPassword(newAccount) == null) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account account, Context context) {
        AzbSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(
                account, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    private static void configurePeriodicSync(Context context, int syncInterval, int syncFlextime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, syncFlextime).
                    setSyncAdapter(account, authority).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }
}
