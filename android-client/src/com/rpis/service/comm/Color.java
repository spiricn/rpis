
package com.rpis.service.comm;

import android.os.Parcel;
import android.os.Parcelable;

public class Color implements Parcelable {
    public Color(float r, float g, float b) {
        mRed = r;
        mGreen = g;
        mBlue = b;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(mRed);
        dest.writeFloat(mGreen);
        dest.writeFloat(mBlue);
    }

    public static final Creator<Color> CREATOR = new Creator<Color>() {
        @Override
        public Color createFromParcel(final Parcel source) {
            return new Color(source.readFloat(), source.readFloat(), source.readFloat());
        }

        @Override
        public Color[] newArray(final int size) {
            return new Color[size];
        }
    };

    private float mRed;
    private float mGreen;
    private float mBlue;
}
