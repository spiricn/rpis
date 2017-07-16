
package com.rpis.service.comm;

import android.os.Parcel;
import android.os.Parcelable;

public enum RpisResult implements Parcelable {
    OK(1), ERROR(2);

    RpisResult(int value) {
        mValue = value;
    }

    static RpisResult fromValue(int value) {
        for (RpisResult i : values()) {
            if (i.mValue == value) {
                return i;
            }
        }

        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mValue);
    }

    public static final Creator<RpisResult> CREATOR = new Creator<RpisResult>() {
        @Override
        public RpisResult createFromParcel(final Parcel source) {
            return RpisResult.fromValue(source.readInt());
        }

        @Override
        public RpisResult[] newArray(final int size) {
            return new RpisResult[size];
        }
    };

    private int mValue;
}
