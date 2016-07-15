package com.hardwork.fg607.myipc.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fg607 on 16-7-11.
 */
public class Book implements Parcelable {

    public int bookId;
    public String bookName;

    public Book(int bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
    }

    protected Book(Parcel in) {

        bookId = in.readInt();
        bookName = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {

        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(bookId);
        dest.writeString(bookName);
    }

    @Override
    public String toString() {

        return "ID："+bookId+"，书名："+bookName;
    }
}
