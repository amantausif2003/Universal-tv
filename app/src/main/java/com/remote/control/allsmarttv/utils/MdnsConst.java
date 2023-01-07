package com.remote.control.allsmarttv.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

public final class MdnsConst {
    public static final InetAddress MDNS_ADDRESS;
    public static final int MDNS_PORT = 5353;
    public static final Charset UTF8_CHARSET;

    static {
        Charset charset;
        InetAddress inetAddress = null;
        try {
            charset = Charset.forName("UTF-8");
        } catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
            charset = null;
        }
        UTF8_CHARSET = charset;
        try {
            inetAddress = InetAddress.getByName("224.0.0.251");
        } catch (UnknownHostException e2) {
        }
        MDNS_ADDRESS = inetAddress;
    }

    MdnsConst() {
    }
}
