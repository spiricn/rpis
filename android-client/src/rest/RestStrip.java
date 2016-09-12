
package rest;

public class RestStrip extends ARestAPI {
    public RestStrip(String address) {
        super(address);
    }

    public void powerOn() {
        get("/strip/powerOn", null);
    }

    public void powerOff() {
        get("/strip/powerOff", null);
    }

    public void setColor(float h, float s, float v) {
        get("/strip/color/set?h=" + h + "&s=" + s + "&v=" + v, null);
    }
}
