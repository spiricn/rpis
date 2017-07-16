
package com.rpis.service.rest;

import org.json.JSONException;

public class RestAPI {
    public RestAPI(String address) {
        mAddress = address;
    }

    public HTTPResponse parsedGet(String query) {
        HTTPResponse res = HTTPRequest.execute(mAddress + "/" + query);
        if (res == null) {
            return null;
        }

        if (res.getResponse() == null) {
            return res;
        } else {
            try {
                if (res.getResponse().getBoolean("success") == false) {
                    return null;
                } else {
                    return res;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private String mAddress;
}
