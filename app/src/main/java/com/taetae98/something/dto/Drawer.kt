package com.taetae98.something.dto

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Drawer(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0L,      // ID(PK) 자동으로 증가한다.
        var name: String = "",  // 서랍의 이름
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readLong(), parcel.readString() ?: "")

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(name)
    }

    companion object CREATOR : Parcelable.Creator<Drawer> {
        override fun createFromParcel(parcel: Parcel): Drawer {
            return Drawer(parcel)
        }

        override fun newArray(size: Int): Array<Drawer?> {
            return arrayOfNulls(size)
        }
    }
}