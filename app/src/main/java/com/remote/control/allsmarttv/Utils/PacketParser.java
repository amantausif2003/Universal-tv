package com.remote.control.allsmarttv.Utils;

import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.ExtractedText;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public abstract class PacketParser {
    private static final boolean DEBUG = false;
    private static final String TAG = "AtvRemote.PacketParser";

    static CharSequence getCharSequence(ByteBuffer byteBuffer) {
        if (byteBuffer.get() != 0) {
            return null;
        }
        int i = byteBuffer.getInt();
        byte[] bArr = new byte[i];
        byteBuffer.get(bArr, 0, i);
        return new String(bArr);
    }

    static ExtractedText getExtracted(ByteBuffer byteBuffer) {
        if (byteBuffer.get() != 0) {
            return null;
        }
        ExtractedText extractedText = new ExtractedText();
        extractedText.text = getCharSequence(byteBuffer);
        extractedText.startOffset = byteBuffer.getInt();
        extractedText.partialStartOffset = byteBuffer.getInt();
        extractedText.partialEndOffset = byteBuffer.getInt();
        extractedText.selectionStart = byteBuffer.getInt();
        extractedText.selectionEnd = byteBuffer.getInt();
        extractedText.flags = byteBuffer.getInt();
        return extractedText;
    }

    static String getString(ByteBuffer byteBuffer) {
        CharSequence charSequence = getCharSequence(byteBuffer);
        if (charSequence != null) {
            return charSequence.toString();
        }
        return null;
    }

    public static void logPacket(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bArr.length; i++) {
            if (i % 32 == 0) {
                if (i > 0) {
                    Log.d(TAG, sb.toString());
                    sb.setLength(0);
                }
                sb.append(String.format("%02x: ", Integer.valueOf(i)));
            }
            sb.append(String.format("%02x ", Byte.valueOf(bArr[i])));
        }
        if (sb.length() > 0) {
            Log.d(TAG, sb.toString());
        }
    }

    static int parseBundleFromBuffer(ByteBuffer byteBuffer, Bundle bundle) {
        int i;
        int i2 = byteBuffer.getInt();
        int i3 = -65536 & i2;
        if (!(i3 == 16449536 || i3 == 65601536) || (i = i2 & 255) <= 0 || i > 4) {
            return -10;
        }
        int i4 = byteBuffer.getShort();
        byte[] bArr = new byte[i4];
        byteBuffer.get(bArr);
        for (int i5 = 0; i5 < i4; i5++) {
            String charSequence = getCharSequence(byteBuffer).toString();
            switch (bArr[i5]) {
                case 0:
                    bundle.putByte(charSequence, byteBuffer.get());
                    break;
                case 1:
                    bundle.putShort(charSequence, byteBuffer.getShort());
                    break;
                case 2:
                    bundle.putInt(charSequence, byteBuffer.getInt());
                    break;
                case 3:
                    bundle.putLong(charSequence, byteBuffer.getLong());
                    break;
                case 4:
                    bundle.putFloat(charSequence, byteBuffer.getFloat());
                    break;
                case 5:
                    bundle.putBoolean(charSequence, byteBuffer.get() == 1);
                    break;
                case 6:
                    bundle.putString(charSequence, getCharSequence(byteBuffer).toString());
                    break;
                default:
                    return -10;
            }
        }
        return i;
    }

    private static int readExactly(InputStream inputStream, byte[] bArr, int i, int i2) throws IOException {
        int i3 = 0;
        int i4 = i2;
        while (i3 < i2) {
            int read = inputStream.read(bArr, i + i3, i4);
            if (read < 0) {
                return -5;
            }
            i3 += read;
            i4 -= read;
        }
        return i3;
    }

    public static int readPacket(InputStream inputStream, byte[] bArr) throws IOException {
        boolean z = false;
        if (bArr.length < 4) {
            return -2;
        }
        if (-5 == readExactly(inputStream, bArr, 0, 4)) {
            return -5;
        }
        short s = (short) (((bArr[2] & 255) << 8) | (bArr[3] & 255));
        if (bArr.length < s + 4) {
            z = true;
            bArr = new byte[(s + 4)];
        }
        if (-5 == readExactly(inputStream, bArr, 4, s)) {
            return -5;
        }
        if (!z) {
            return s + 4;
        }
        return -2;
    }

    public abstract int parse(byte[] bArr);
}
