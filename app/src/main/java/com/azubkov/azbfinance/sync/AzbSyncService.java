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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created on 09.11.14.
 * @author Andrey Zubkov
 */
public class AzbSyncService extends Service {

    private static String LOG_TAG = AzbSyncService.class.getSimpleName();
    private static final Object syncLock = new Object();
    private AzbSyncAdapter adapter = null;


    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "Sync service creation requested");
        synchronized (syncLock){
            if (adapter == null) {
                adapter = new AzbSyncAdapter(getApplicationContext(), true);
            }
        }
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return adapter.getSyncAdapterBinder();
    }
}
