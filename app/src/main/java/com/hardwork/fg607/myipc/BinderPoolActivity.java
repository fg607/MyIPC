package com.hardwork.fg607.myipc;

import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hardwork.fg607.myipc.binderpool.BinderPool;
import com.hardwork.fg607.myipc.binderpool.ComputeImpl;
import com.hardwork.fg607.myipc.binderpool.ICompute;
import com.hardwork.fg607.myipc.binderpool.SecurityCenterImpl;

public class BinderPoolActivity extends AppCompatActivity {

    private static final String TAG = "BinderPoolActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    doWork();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void doWork() throws RemoteException {

        BinderPool binderPool = BinderPool.getInstance(BinderPoolActivity.this);


        IBinder binder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);

        ComputeImpl compute = (ComputeImpl) binder;

        int c = compute.add(1,2);

        Log.i(TAG,"compute C="+c);

        binder = binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);

        SecurityCenterImpl securityCenter = (SecurityCenterImpl) binder;

        String encrypted = securityCenter.encrypt("今晚十点小树林见！");

        Log.i(TAG,"加密内容:"+encrypted);

        String decrypted = securityCenter.decrypt(encrypted);

        Log.i(TAG,"解密内容:"+decrypted);



    }
}
