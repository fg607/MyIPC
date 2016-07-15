package com.hardwork.fg607.myipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hardwork.fg607.myipc.aidl.Book;
import com.hardwork.fg607.myipc.aidl.IBookManager;
import com.hardwork.fg607.myipc.aidl.IOnNewBookArrivedListener;
import com.hardwork.fg607.myipc.service.BookManagerService;

import java.util.ArrayList;
import java.util.List;

public class BookManagerActivity extends AppCompatActivity {

    private static  final String TAG = "BookManagerActivity";

    private static final int ARRIVED_NEW_BOOK = 1;

    private IBinder.DeathRecipient mDeathRecipent = new IBinder.DeathRecipient() {

       //binderDied()运行在Bidner线程池中，所以不能在此方法对UI进行操作。
        @Override
        public void binderDied() {

            if(mBookManager==null){

                return;
            }

            //解除Binder死亡代理
            mBookManager.asBinder().unlinkToDeath(mDeathRecipent,0);

            mBookManager = null;

            //重新绑定

            Intent intent = new Intent(BookManagerActivity.this, BookManagerService.class);

            bindService(intent,mServiceConnection, Context.BIND_AUTO_CREATE);


        }
    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

                case ARRIVED_NEW_BOOK:
                    Book book = (Book) msg.obj;
                    Toast.makeText(BookManagerActivity.this,"新书上市了，快去看看\r\n"+book.toString(),Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    };

    private IOnNewBookArrivedListener mNewBookListener = new IOnNewBookArrivedListener.Stub() {

        //由于远程调用listener的onNewBookArrived(Book book)方法时会运行在客户端的Binder线程池中，
        //所以不能在此方法中进行UI操作，应该使用Handle切换到UI线程中操作。
        @Override
        public void onNewBookArrived(Book book) throws RemoteException {

            mHandler.obtainMessage(ARRIVED_NEW_BOOK,book).sendToTarget();
        }
    };

    private IBookManager mBookManager;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mBookManager = IBookManager.Stub.asInterface(service);

            //设置死亡代理，方便在Binder死亡后重新绑定
            try {
                mBookManager.asBinder().linkToDeath(mDeathRecipent,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            //由于调用远程服务端的方法会运行在服务端的Binder线程池中，同时客户端会被挂起，
            //所以避免服务端方法好耗时长导客户端界面的ANR,应该另开线程执行远程调用。

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {

                        mBookManager.addBook(new Book(3,"国富论"));

                        List<Book> list = mBookManager.getBookList();

                        Log.i(TAG, list.toString());

                        mBookManager.registerListener(mNewBookListener);


                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }

            }).start();




        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, BookManagerService.class);

        bindService(intent,mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {

        unbindService(mServiceConnection);

        try {
            mBookManager.unregisterListener(mNewBookListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }
}
