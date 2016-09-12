
package rest;

public class RPISApi {
    public RPISApi(String address) {
        mStripApi = new RestStrip(address);
    }

    public RestStrip getStripAPI() {
        return mStripApi;
    }

    private RestStrip mStripApi;
}
