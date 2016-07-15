package com.hardwork.fg607.myipc.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import com.hardwork.fg607.myipc.binderpool.BinderPool;


public class BinderPoolService extends Service{

    private static final String TAG = "BinderPoolService";
    private Binder mBinderPool = new BinderPool.BinderPoolImpl();

    @Override
    public IBinder onBind(Intent intent) {
       return mBinderPool;
    }

}
