package com.example.soundscape

import android.os.Parcel
import android.os.Parcelable

data class Recommendation(val song: String, val artist: String, var isFavorited: Boolean ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(song)
        parcel.writeString(artist)
        parcel.writeByte(if (isFavorited) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recommendation> {
        override fun createFromParcel(parcel: Parcel): Recommendation {
            return Recommendation(parcel)
        }

        override fun newArray(size: Int): Array<Recommendation?> {
            return arrayOfNulls(size)
        }
    }
}