package io.apisense.network.ping;

/**
 * Definition of a ping RTT.
 */
public class Rtt {

    public final float min;

    public final float avg;

    public final float max;

    public final float mdev;

    Rtt(float min, float avg, float max, float mdev) {
        this.min = min;
        this.avg = avg;
        this.max = max;
        this.mdev = mdev;
    }

    @Override
    public String toString() {
        return "Rtt{" +
                "min=" + min +
                ", avg=" + avg +
                ", max=" + max +
                ", mdev=" + mdev +
                "}";
    }
}
