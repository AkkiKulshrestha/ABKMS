package com.ascend.www.abkms.application;

import android.app.Application;


import com.ascend.www.abkms.utils.ConnectivityReceiver;

/**
 * Created by Ravi Tamada on 15/06/16.
 * www.androidhive.info
 */

public class MyApplication extends Application {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        //MultiDex.install(this);
        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
