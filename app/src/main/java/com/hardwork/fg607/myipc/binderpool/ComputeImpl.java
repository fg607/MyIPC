package com.hardwork.fg607.myipc.binderpool;

import android.os.IBinder;
import android.os.RemoteException;

/**
 * Created by fg607 on 16-7-14.
 */
public class ComputeImpl extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {

        return a+b;
    }
}
