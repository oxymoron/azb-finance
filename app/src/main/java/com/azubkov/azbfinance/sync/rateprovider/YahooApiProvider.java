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

package com.azubkov.azbfinance.sync.rateprovider;

import android.support.annotation.Nullable;
import android.util.Log;

import com.azubkov.azbfinance.Curr;
import com.azubkov.azbfinance.model.Rate;
import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created on 09.11.14.
 *
 * @author Andrey Zubkov
 */
public class YahooApiProvider implements RateProvider {

    public static final String YAHOO_FINANCE_URL =
            "http://finance.yahoo.com/d/quotes.csv?s=RUB=X+KRW=X+EUR=X&f=sl1d1t1";
    private static String LOG_TAG = YahooApiProvider.class.getSimpleName();

    @Override
    @Nullable
    public List<Rate> request() throws IOException {
        BasicHttpClient httpClient = new BasicHttpClient();
        HttpResponse response = httpClient.get(YAHOO_FINANCE_URL, null);
        if (response == null) {
            throw new IOException("Currency response is null");
        }
        int status = response.getStatus();
        switch (status) {
            case HttpURLConnection.HTTP_OK:
                String body = response.getBodyAsString();
                return processBody(body);
            case HttpURLConnection.HTTP_NOT_MODIFIED:
                Log.d(LOG_TAG, "Not modified");
                return null;
            default:
                throw new IOException("Error fetching currencies. HTTP Status: " + status);
        }
    }

    private List<Rate> processBody(String body) throws IOException {
        Log.d(LOG_TAG, body);
        List<Rate> rates = new ArrayList<Rate>();
        try {
            Scanner scanner = new Scanner(body);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] values = line.split(",");

                String currString = values[0].substring(1, 4);
                Curr curr = Curr.valueOf(currString);

                String valString = values[1];
                Double value = Double.valueOf(valString);

                String d = values[2];
                String t = values[3];
                String dt = unwrap(d) + " " + normalizeTimeString(unwrap(t));
                // To upper case to convert am/pm to AM/PM
                Date date = new SimpleDateFormat("M/d/yyyy h:m a").parse(dt.toUpperCase());
                Rate rate = new Rate(curr, value, date);
                rates.add(rate);
            }
        } catch (ParseException e) {
            throw new IOException(e);
        }
        return rates;
    }

    private String normalizeTimeString(String str) {
        int len = str.length();
        int cut = len - 2;
        return str.substring(0, cut) + " " + str.substring(cut, len);
    }

    private String unwrap(String str) {
        return str.substring(1, str.length() - 1);
    }
}
