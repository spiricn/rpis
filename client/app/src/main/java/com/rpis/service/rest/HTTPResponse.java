
package com.rpis.service.rest;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HTTPResponse {
    private static final String TAG = HTTPResponse.class.getSimpleName();

    HTTPResponse(String response, int code) {
        mResponse = response;
        mCode = code;
    }

    public JSONObject getResponse() {
        if (mJson == null) {
            try {
                Log.d(TAG, "Parsing: '" + mResponse + "'");
                mJson = new JSONObject(mResponse);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        return mJson;
    }

    public int getCode() {
        return mCode;
    }

    private String mResponse;
    private int mCode;
    private JSONObject mJson;
}
