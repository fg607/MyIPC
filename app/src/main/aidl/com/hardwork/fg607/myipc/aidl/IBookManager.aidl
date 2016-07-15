// IBookManager.aidl
package com.hardwork.fg607.myipc.aidl;

import com.hardwork.fg607.myipc.aidl.Book;
import com.hardwork.fg607.myipc.aidl.IOnNewBookArrivedListener;

interface IBookManager {

   List<Book> getBookList();
   void addBook(in Book book);
   void registerListener(IOnNewBookArrivedListener listener);
   void unregisterListener(IOnNewBookArrivedListener listener);
}
