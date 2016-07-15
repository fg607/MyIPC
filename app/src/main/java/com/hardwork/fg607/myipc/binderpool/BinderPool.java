package com.hardwork.fg607.myipc.binderpool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.hardwork.fg607.myipc.service.BinderPoolService;

import java.util.concurrent.CountDownLatch;

/**
 * Created by fg607 on 16-7-14.
 */
public class BinderPool {

    private static final String TAG = "BinderPool";

    public static final int BINDER_NONE = -1;
    public static final int BINDER_COMPUTE = 0;
    public static final int BINDER_SECURITY_CENTER = 1;

    private Context mContext;
    private static IBinderPool mBinderPool;
    private static BinderPool mInstance;
    private CountDownLatch mCountDownLatch;

    private BinderPool(Context context) {
        mContext = context.getApplicationContext();
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context){
        if (mBinderPool == null){
            synchronized (BinderPool.class){
                if (mBinderPool == null){
                    mInstance = new BinderPool(context);
                }
            }
        }
        return mInstance;
    }

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient,0);
            mBinderPool = null;
            connectBinderPoolService();
        }
    };

    private synchronized void connectBinderPoolService(){
        mCountDownLatch = new CountDownLatch(1);
        Intent service = new Intent(mContext, BinderPoolService.class);
        mContext.bindService(service,mBinderPoolConnection,Context.BIND_AUTO_CREATE);
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public IBinder queryBinder(int binderCode){
        IBinder binder = null;
        if (mBinderPool != null){
            try {
                binder = mBinderPool.queryBinder(binderCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return binder;
    }

    public static class BinderPoolImpl extends IBinderPool.Stub {

        public BinderPoolImpl(){
            super();
        }
        @Override
        public IBinder queryBinder(int binderId) throws RemoteException {
            IBinder binder = null;
            switch (binderId){
                case BINDER_COMPUTE:
                    binder = new ComputeImpl();
                    break;
                case BINDER_SECURITY_CENTER:
                    binder = new SecurityCenterImpl();
                    break;
                default:
                    break;
            }
            return binder;
        }
    }
}
