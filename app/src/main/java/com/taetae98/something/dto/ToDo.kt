package com.taetae98.something.dto

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.taetae98.something.utility.Time

@Entity(
        foreignKeys = [
            ForeignKey(
                    entity = Drawer::class,
                    parentColumns = ["id"],
                    childColumns = ["drawerId"],
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE
            )
        ]
)
data class ToDo(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0L,
        var title: String = "",
        var description: String = "",
        var drawerId: Long = 0L,
        var isFinished: Boolean = false,
        var isOnTop: Boolean = false,
        var isSticky: Boolean = false,
        var hasTerm: Boolean = false,
        var beginTime: Time = Time(),
        var endTime: Time = Time()
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readLong(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            Time(parcel.readLong()),
            Time(parcel.readLong())
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.run {
            writeLong(id)
            writeString(title)
            writeString(description)
            writeLong(drawerId)
            writeByte(if (isFinished) 1 else 0)
            writeByte(if (isOnTop) 1 else 0)
            writeByte(if (isSticky) 1 else 0)
            writeByte(if (hasTerm) 1 else 0)
            writeLong(beginTime.timeInMillis)
            writeLong(endTime.timeInMillis)
        }
    }

    companion object CREATOR : Parcelable.Creator<ToDo> {
        override fun createFromParcel(parcel: Parcel): ToDo {
            return ToDo(parcel)
        }

        override fun newArray(size: Int): Array<ToDo?> {
            return arrayOfNulls(size)
        }
    }
}