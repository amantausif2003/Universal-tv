package com.remote.control.allsmarttv.utils;

public class TouchRecordUtil {
    public final Location[] locations;
    public final long time;

    public static class Location {
        public final float xDip;
        public final float yDip;

        public Location(float f, float f2) {
            this.xDip = f;
            this.yDip = f2;
        }

        public Location(float f, float f2, float f3) {
            this.xDip = f / f3;
            this.yDip = f2 / f3;
        }
    }

    public TouchRecordUtil(long j, Location[] locationArr) {
        this.time = j;
        this.locations = locationArr;
    }
}
