
package com.rpis.service.comm;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Color implements Parcelable {
    public Color(float h, float s, float v) {
        mHue = h;
        mSaturation = s;
        mValue = v;
    }

    public Color(JSONObject json) throws JSONException {
        mHue = (float) json.getDouble("hue");
        mSaturation = (float) json.getDouble("saturation");
        mValue = (float) json.getDouble("value");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public float getHue() {
        return mHue;
    }

    public void setHue(float mHue) {
        this.mHue = mHue;
    }

    public float getSaturation() {
        return mSaturation;
    }

    public void setSaturation(float mSaturation) {
        this.mSaturation = mSaturation;
    }

    public float getValue() {
        return mValue;
    }

    public void setValue(float mValue) {
        this.mValue = mValue;
    }

    public int getRGB() {
        float[] hsv = {
                mHue * 360, mSaturation, mValue
        };

        return android.graphics.Color.HSVToColor(hsv);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(mHue);
        dest.writeFloat(mSaturation);
        dest.writeFloat(mValue);
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

    @Override
    public String toString() {
        return "{" + mHue + "," + mSaturation + "," + mValue + "}";
    }

    private float mHue;
    private float mSaturation;
    private float mValue;
}
