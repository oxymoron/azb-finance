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

package com.azubkov.azbfinance.model;

import com.azubkov.azbfinance.Curr;

import java.util.Date;

/**
 * Created on 09.11.14.
 *
 * @author Andrey Zubkov
 */
public class Rate {

    private final Curr curr;

    private final Double value;

    private final Date date;

    public Rate(Curr curr, Double value, Date date) {
        this.curr = curr;
        this.value = value;
        this.date = date;
    }

    public Curr getCurr() {
        return curr;
    }

    public Double getValue() {
        return value;
    }

    public Date getDate() {
        return date;
    }
}
