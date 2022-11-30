package com.remote.control.allsmarttv.Utils;

public class PackConstants {

    static final short[] MIN_MESSAGE_LENGTH = new short[37];

    static {
        MIN_MESSAGE_LENGTH[0] = 12;
        MIN_MESSAGE_LENGTH[2] = 16;
        MIN_MESSAGE_LENGTH[3] = 5;
        MIN_MESSAGE_LENGTH[5] = 0;
        MIN_MESSAGE_LENGTH[6] = 0;
        MIN_MESSAGE_LENGTH[7] = 0;
        MIN_MESSAGE_LENGTH[8] = 4;
        MIN_MESSAGE_LENGTH[9] = 1;
        MIN_MESSAGE_LENGTH[10] = 1;
    }
}
