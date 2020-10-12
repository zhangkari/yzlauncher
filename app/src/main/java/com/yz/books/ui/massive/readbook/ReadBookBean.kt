package com.yz.books.ui.massive.readbook

import android.os.Parcel
import android.os.Parcelable

/**
 * @author lilin
 * @time on 2020-01-13 08:55
 */

data class ReadBookBean(var bookPath: String?,
                        var bookName: String?,
                        var readingChapter: Int,
                        var readingCurrentPage: Int,
                        var readingTotalPage: Int): Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bookPath)
        parcel.writeString(bookName)
        parcel.writeInt(readingChapter)
        parcel.writeInt(readingCurrentPage)
        parcel.writeInt(readingTotalPage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReadBookBean> {
        override fun createFromParcel(parcel: Parcel): ReadBookBean {
            return ReadBookBean(parcel.readString(), parcel.readString(),
                parcel.readInt(),parcel.readInt(),parcel.readInt())
        }

        override fun newArray(size: Int): Array<ReadBookBean?> {
            return arrayOfNulls(size)
        }
    }

}