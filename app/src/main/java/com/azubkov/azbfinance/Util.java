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

import android.content.Context;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;

/**
 * Created by oxymoron on 02.10.2014.
 */
public final class Util {

    private static final double RUBKRW = 26.8567;
    private static final double USDKRW = 1071.7500;
    private static final double USDRUB = 39.8960;

    private static NumberFormat amountFormat;

    private static final double[][] convTable = new double[][]{
        // RUB
        new double[]{1,         1/USDRUB, RUBKRW    },
        // USD
        new double[]{USDRUB,  1,      USDKRW    },
        // KRW
        new double[]{1/RUBKRW,    1/USDKRW, 1           }
    };
    public static final char THOUSANDS_SEPARATOR = ' ';

    public static String toCurr(Context context, double amount, Curr curr) {
        int format;
        switch (curr){
            case RUB: format = R.string.rub_format;
                break;
            case USD: format = R.string.usd_format;
                break;
            case KRW: format = R.string.krw_format;
                break;
            default:
                throw new UnsupportedOperationException("Wrong currency: " + curr);
        }
//        String value = String.format("%,.0f", amount);
        String value = getAmountFormat().format(amount);
        return context.getString(format, value);
    }

    private static NumberFormat getAmountFormat() {
        if (amountFormat == null){
            DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();
            DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
            symbols.setGroupingSeparator(THOUSANDS_SEPARATOR);
            format.setDecimalFormatSymbols(symbols);
            format.setMaximumFractionDigits(0);
            amountFormat = format;
        }
        return amountFormat;
    }

    public static double convert(double amount, Curr fromCurr, Curr toCurr) {
        int fromIndex = currToIndex(fromCurr);
        int toIndex = currToIndex(toCurr);
        double rate = convTable[fromIndex][toIndex];
        return amount * rate;
    }

    public static double convert(double amount, String fromCurr, String toCurr) {
        return convert(amount, Curr.valueOf(fromCurr), Curr.valueOf(toCurr));
    }

    private static int currToIndex(Curr curr) {
        int index;
        switch (curr){
            case RUB: index = 0;
                break;
            case USD: index = 1;
                break;
            case KRW: index = 2;
                break;
            default:
                throw new UnsupportedOperationException("Wrong currency: " + curr);
        }
        return index;
    }
}
