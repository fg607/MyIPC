// IOnNewBookArrivedListener.aidl
package com.hardwork.fg607.myipc.aidl;

import com.hardwork.fg607.myipc.aidl.Book;

interface IOnNewBookArrivedListener {

   void onNewBookArrived(in Book book);
}
