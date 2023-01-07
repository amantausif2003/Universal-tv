package com.remote.control.allsmarttv.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;

class MdnsWriter {
    private ByteArrayOutputStream mByteStream = new ByteArrayOutputStream(16384);
    private DataOutputStream mDataStream = new DataOutputStream(this.mByteStream);

    public DatagramPacket getPacket() {
        try {
            this.mDataStream.flush();
        } catch (IOException e) {
        }
        byte[] byteArray = this.mByteStream.toByteArray();
        return new DatagramPacket(byteArray, byteArray.length, MdnsConst.MDNS_ADDRESS, (int) MdnsConst.MDNS_PORT);
    }

    public void writeBytes(byte[] bArr) {
        try {
            this.mDataStream.write(bArr, 0, bArr.length);
        } catch (IOException e) {
        }
    }

    public void writeLabels(String str) {
        for (String str2 : str.split("\\.")) {
            byte[] bytes = str2.getBytes(MdnsConst.UTF8_CHARSET);
            writeUInt8(bytes.length);
            writeBytes(bytes);
        }
    }

    public void writeUInt16(int i) {
        try {
            this.mDataStream.writeShort(1048575 & i);
        } catch (IOException e) {
        }
    }

    public void writeUInt8(int i) {
        try {
            this.mDataStream.writeByte(i & 255);
        } catch (IOException e) {
        }
    }
}
