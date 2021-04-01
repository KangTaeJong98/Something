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
                    onDelete = ForeignKey.SET_NULL,
                    onUpdate = ForeignKey.CASCADE
            )
        ]
)
data class ToDo(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0L,                  // ID(PK) 자동으로 증가한다.
        var title: String = "",             // 할일의 제목
        var description: String = "",       // 할일의 설명
        var drawerId: Long? = null,         // 서랍의 ID(FK)
        var isFinished: Boolean = false,    // 할일의 완료 여부
        var isOnTop: Boolean = false,       // 할일을 최상단에 표시할지 여부
        var isNotification: Boolean = false,// 알림창에 표시
        var hasTerm: Boolean = false,       // 기간이 있는지 여부
        var beginTime: Time = Time(),       // 시작시간
        var endTime: Time = Time()          // 종료시간
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readLong().run { if (this == -1L) { null } else { this } },
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
            writeLong(drawerId ?: -1)
            writeByte(if (isFinished) 1 else 0)
            writeByte(if (isOnTop) 1 else 0)
            writeByte(if (isNotification) 1 else 0)
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