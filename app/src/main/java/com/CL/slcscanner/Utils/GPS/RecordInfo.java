package com.CL.slcscanner.Utils.GPS;

import java.util.Date;

/**
 *  Copyright 2017 Alberto González Pérez
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     <p>http://www.apache.org/licenses/LICENSE-2.0</p>
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

public class RecordInfo {
    private final float accuracy;
    private final long timestamp;

    public RecordInfo() {
        this(0.0f);
    }

    public RecordInfo(float accuracy) {
        this(accuracy, new Date().getTime());
    }

    public RecordInfo(long timestamp) {
        this(0.0f, timestamp);
    }

    public RecordInfo(float accuracy, long timestamp) {
        this.accuracy = accuracy;
        this.timestamp = timestamp;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public long getTimestamp() {
        return timestamp;
    }
}