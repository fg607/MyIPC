package com.hardwork.fg607.myipc.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.hardwork.fg607.myipc.aidl.Book;
import com.hardwork.fg607.myipc.aidl.IBookManager;
import com.hardwork.fg607.myipc.aidl.IOnNewBookArrivedListener;

import java.security.Permission;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookManagerService extends Service {

    private static final String TAG = "BookManagerService";

    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();

    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<IOnNewBookArrivedListener>();



    private Binder mBinder= new IBookManager.Stub(){


        @Override
        public List<Book> getBookList() throws RemoteException {

            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {

            mBookList.add(book);

        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {

            mListenerList.register(listener);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {

            mListenerList.unregister(listener);

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mBookList.add(new Book(1,"魔鬼经济学"));
        mBookList.add(new Book(2,"稀缺"));

        new Thread(new ServiceWorker()).start();
    }

    @Override
    public IBinder onBind(Intent intent) {

        //对绑定服务进行权限验证(如果客户端和服务端不在一个工程需要在onTransact()方法中验证即可)
        int check = checkCallingOrSelfPermission("com.hardwork.fg607.myipc.ACCESS_BOOK_SERVICE");

        if(check== PackageManager.PERMISSION_DENIED){

            return  null;
        }

       return mBinder;
    }

    private void onNewBookArrived(Book book)throws RemoteException{

        mBookList.add(book);

       final int N = mListenerList.beginBroadcast();

        for(int i =0;i<N;i++){

            IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);

            if(listener!=null){

                //由于调用远程客户端端的方法会运行在客户端的Binder线程池中，同时服务端会被挂起，
                //所以避免客户端方法耗时过长导服务端界面的ANR,应该另开线程执行远程调用。
                try {
                    listener.onNewBookArrived(book);
                }catch (RemoteException e){

                    e.printStackTrace();
                }

            }
        }

        mListenerList.finishBroadcast();


    }

    private class ServiceWorker implements Runnable{


        @Override
        public void run() {

            while (!mIsServiceDestoryed.get()){

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int bookId = mBookList.size()+1;
                Book book = new Book(bookId,"NewBook#"+bookId);

                try {

                    onNewBookArrived(book);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
