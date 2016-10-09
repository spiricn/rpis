
package com.rpis.service.comm;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Prefab implements Parcelable {
    public Prefab(String name, int id) {
        mName = name;
        mId = id;
    }

    public Prefab(JSONObject json) throws JSONException {
        mName = json.getString("name");
        mId = json.getInt("id");
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeInt(mId);
    }

    public static final Creator<Prefab> CREATOR = new Creator<Prefab>() {
        @Override
        public Prefab createFromParcel(final Parcel source) {
            return new Prefab(source.readString(), source.readInt());
        }

        @Override
        public Prefab[] newArray(final int size) {
            return new Prefab[size];
        }
    };

    public String getName() {
        return mName;
    }

    public int getId(){
        return mId;
    }

    @Override
    public String toString() {
        return "{" + mName + "," + mId + "}";
    }

    private String mName;
    private int mId;
}
