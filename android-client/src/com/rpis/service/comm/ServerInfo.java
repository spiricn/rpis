
package com.rpis.service.comm;

import android.os.Parcel;
import android.os.Parcelable;

public class ServerInfo implements Parcelable {
    public ServerInfo(String name, String version, String address, String rest) {
        mName = name;
        mVersion = version;
        mAddress = address;
        mRest = rest;
    }

    public String getName() {
        return mName;
    }

    public String getVersion() {
        return mVersion;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getRest() {
        return mRest;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeString(mName);
        dest.writeString(mVersion);
        dest.writeString(mAddress);
        dest.writeString(mRest);
    }

    public static final Creator<ServerInfo> CREATOR = new Creator<ServerInfo>() {
        @Override
        public ServerInfo createFromParcel(final Parcel source) {
            return new ServerInfo(source.readString(), source.readString(), source.readString(),
                    source.readString());
        }

        @Override
        public ServerInfo[] newArray(final int size) {
            return new ServerInfo[size];
        }
    };

    @Override
    public String toString() {
        return "{" + mName + "," + mVersion + "," + mAddress + "," + mRest + "}";
    }

    private String mName;
    private String mVersion;
    private String mAddress;
    private String mRest;
}
