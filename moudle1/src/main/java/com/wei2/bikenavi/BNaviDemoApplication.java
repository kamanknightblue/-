/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.wei2.bikenavi;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;


public class BNaviDemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
