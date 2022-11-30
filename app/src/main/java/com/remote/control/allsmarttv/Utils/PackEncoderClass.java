package com.remote.control.allsmarttv.Utils;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;

import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class PackEncoderClass {
    private static final boolean DEBUG = false;
    public static final byte[] ENCODED_CANCEL_BUGREPORT = {1, 29, 0, 0};
    private static final byte[] ENCODED_CONFIGURE_SUCCESS = {1, 7, 0, 0};
    private static final byte[] ENCODED_HIDE_IME = {1, 6, 0, 0};
    private static final byte[] ENCODED_PACKET_PING = {1, 20, 0, 0};
    public static final byte[] ENCODED_PACKET_PONG = {1, 21, 0, 0};
    private static final byte[] ENCODED_PACKET_START_VOICE = {1, 11, 0, 0};
    private static final byte[] ENCODED_PACKET_STOP_VOICE = {1, 12, 0, 0};
    private static final byte[] ENCODED_PACKET_VERSION_TOO_HIGH = {1, 9, 0, 4, 1, 0, 0, 0};
    private static final byte[] ENCODED_PACKET_VERSION_TOO_LOW = {1, 10, 0, 4, 1, 0, 0, 0};
    private static final byte[] ENCODED_SHOW_IME = {1, 5, 0, 0};
    public static final byte[] ENCODED_TAKE_BUGREPORT = {1, 28, 0, 0};
    private static final ByteBuffer mBuffer = ByteBuffer.allocate(65539);
    private static final ReentrantLock mLock = new ReentrantLock();
    long mIndex = 0;


    public class PacketBuilder {
        public PacketBuilder(byte b) {
            if (!PackEncoderClass.mLock.isHeldByCurrentThread()) {
                PackEncoderClass.mLock.lock();
                PackEncoderClass.mBuffer.clear();
                addHeader(b);
                return;
            }
            PackEncoderClass.mLock.unlock();
            throw new RuntimeException(String.format("Thread %s already building packet", Thread.currentThread().toString()));
        }

        private void addHeader(byte b) {
            put((byte) 1).put(b).putShort((short) 0);
        }

        private void setPayloadSize() {
            PackEncoderClass.mBuffer.putShort(2, (short) (PackEncoderClass.mBuffer.position() - 4));
        }

        public byte[] build() {
            setPayloadSize();
            byte[] bArr = new byte[PackEncoderClass.mBuffer.position()];
            System.arraycopy(PackEncoderClass.mBuffer.array(), PackEncoderClass.mBuffer.arrayOffset(), bArr, 0, PackEncoderClass.mBuffer.position());
            PackEncoderClass.mLock.unlock();
            return bArr;
        }

        public void destroy() {
            PackEncoderClass.mBuffer.clear();
            PackEncoderClass.mLock.unlock();
        }

        public PacketBuilder put(byte b) {
            PackEncoderClass.mBuffer.put(b);
            return this;
        }

        public PacketBuilder put(byte[] bArr) {
            PackEncoderClass.mBuffer.put(bArr);
            return this;
        }

        public PacketBuilder putBoolean(boolean z) {
            byte b = 0;
            ByteBuffer byteBuffer = PackEncoderClass.mBuffer;
            if (z) {
                b = 1;
            }
            byteBuffer.put(b);
            return this;
        }

        public PacketBuilder putCharSequence(CharSequence charSequence) {
            if (charSequence != null) {
                byte[] bytes = charSequence.toString().getBytes();
                PackEncoderClass.mBuffer.put((byte) 0);
                PackEncoderClass.mBuffer.putInt(bytes.length);
                PackEncoderClass.mBuffer.put(bytes);
            } else {
                PackEncoderClass.mBuffer.put((byte) 1);
            }
            return this;
        }

        public PacketBuilder putExtractedText(ExtractedText extractedText) {
            putBoolean(extractedText == null);
            if (extractedText != null) {
                putCharSequence(extractedText.text);
                putInt(extractedText.startOffset);
                putInt(extractedText.partialStartOffset);
                putInt(extractedText.partialEndOffset);
                putInt(extractedText.selectionStart);
                putInt(extractedText.selectionEnd);
                putInt(extractedText.flags);
            }
            return this;
        }

        public PacketBuilder putFloat(float f) {
            PackEncoderClass.mBuffer.putFloat(f);
            return this;
        }

        public PacketBuilder putInt(int i) {
            PackEncoderClass.mBuffer.putInt(i);
            return this;
        }

        public PacketBuilder putLong(long j) {
            PackEncoderClass.mBuffer.putLong(j);
            return this;
        }

        public PacketBuilder putShort(short s) {
            PackEncoderClass.mBuffer.putShort(s);
            return this;
        }
    }

    public byte[] encodeAssetDataChunk(String str, int i, int i2, byte[] bArr, int i3) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 34);
        packetBuilder.putCharSequence(str);
        packetBuilder.putInt(i);
        packetBuilder.putInt(i2);
        packetBuilder.putInt(i3);
        packetBuilder.put(bArr);
        return packetBuilder.build();
    }

    public byte[] encodeAssetFooter(String str, int i) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 33);
        packetBuilder.putCharSequence(str);
        packetBuilder.putInt(i);
        return packetBuilder.build();
    }

    public byte[] encodeAssetHeader(String str, String str2, int i, int i2, Map<String, String> map) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 32);
        packetBuilder.putCharSequence(str);
        packetBuilder.putCharSequence(str2);
        packetBuilder.putInt(i);
        packetBuilder.putInt(i2);
        if (map != null) {
            packetBuilder.putInt(map.size());
            for (String str3 : map.keySet()) {
                packetBuilder.putCharSequence(str3);
                packetBuilder.putCharSequence(map.get(str3));
            }
        } else {
            packetBuilder.putInt(0);
        }
        return packetBuilder.build();
    }

    public byte[] encodeBeginBatchEdit() {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 18);
        packetBuilder.put((byte) 5);
        return packetBuilder.build();
    }

    public byte[] encodeBugreportStatus(int i) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 30);
        packetBuilder.putInt(i);
        return packetBuilder.build();
    }

    public byte[] encodeCancelBugReport() {
        return ENCODED_CANCEL_BUGREPORT;
    }

    public byte[] encodeCapabilities(CapabilitiesClass capabilitiesClass) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 36);
        int length = capabilitiesClass.toByteBuffer().array().length;
        packetBuilder.putInt(length);
        if (length != 0) {
            packetBuilder.put(capabilitiesClass.toByteBuffer().array());
        }
        return packetBuilder.build();
    }

    public byte[] encodeCommitCompletion(CompletionInfo completionInfo) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 18);
        packetBuilder.put((byte) 6).putLong(completionInfo.getId()).putInt(completionInfo.getPosition()).putCharSequence(completionInfo.getText()).putCharSequence(completionInfo.getLabel());
        return packetBuilder.build();
    }

    public byte[] encodeCommitText(CharSequence charSequence, int i) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 18);
        packetBuilder.put((byte) 3).putCharSequence(charSequence).putInt(i);
        return packetBuilder.build();
    }

    public byte[] encodeCompletionInfo(CompletionInfo[] completionInfoArr) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 17);
        packetBuilder.putInt(completionInfoArr.length);
        for (CompletionInfo completionInfo : completionInfoArr) {
            packetBuilder.putLong(completionInfo.getId()).putInt(completionInfo.getPosition()).putCharSequence(completionInfo.getText()).putCharSequence(completionInfo.getLabel());
        }
        return packetBuilder.build();
    }

    public byte[] encodeConfigure(int i, int i2, byte b, byte b2, String str) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 0);
        packetBuilder.putInt(i).putInt(i2).put(b).put(b2).put((byte) 0).put((byte) 0).putCharSequence(str);
        return packetBuilder.build();
    }

    public byte[] encodeConfigureFailure(int i) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 8);
        packetBuilder.putInt(i);
        return packetBuilder.build();
    }

    public byte[] encodeConfigureSuccess(int i, String str) {
        return new PacketBuilder((byte) 7).putInt(i).putCharSequence(str).putCharSequence(Build.FINGERPRINT).putCharSequence(Build.ID).putCharSequence(Build.MANUFACTURER).putCharSequence(Build.MODEL).putInt(Build.VERSION.SDK_INT).build();
    }

    public byte[] encodeDeleteSurroundingText(int i, int i2) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 18);
        packetBuilder.put((byte) 4).putInt(i).putInt(i2);
        return packetBuilder.build();
    }

    public byte[] encodeDeveloperStatus(boolean z) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 27);
        packetBuilder.putBoolean(z);
        return packetBuilder.build();
    }

    public byte[] encodeEndBatchEdit() {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 18);
        packetBuilder.put((byte) 7);
        return packetBuilder.build();
    }

    public byte[] encodeFinishComposingText() {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 18);
        packetBuilder.put((byte) 8);
        return packetBuilder.build();
    }

    public byte[] encodeGetCursorCapsMode(long j, int i) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 18);
        packetBuilder.put((byte) 14).putLong(j).putInt(i);
        return packetBuilder.build();
    }

    public byte[] encodeGetExtractedText(long j, ExtractedTextRequest extractedTextRequest, int i) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 18);
        packetBuilder.put((byte) 15).putLong(j).putInt(extractedTextRequest.token).putInt(extractedTextRequest.flags).putInt(extractedTextRequest.hintMaxLines).putInt(extractedTextRequest.hintMaxChars).putInt(i);
        return packetBuilder.build();
    }

    public byte[] encodeGetSelectedText(long j, int i) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 18);
        packetBuilder.put((byte) 16).putLong(j).putInt(i);
        return packetBuilder.build();
    }

    public byte[] encodeGetTextAfterCursor(long j, int i, int i2) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 18);
        packetBuilder.put((byte) 13).putLong(j).putInt(i).putInt(i2);
        return packetBuilder.build();
    }

    public byte[] encodeGetTextBeforeCursor(long j, int i, int i2) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 18);
        packetBuilder.put((byte) 12).putLong(j).putInt(i).putInt(i2);
        return packetBuilder.build();
    }

    public byte[] encodeHideIme() {
        return ENCODED_HIDE_IME;
    }

    public byte[] encodeIntent(Intent intent) {
        byte[] bytes = intent.toUri(Intent.URI_ANDROID_APP_SCHEME).getBytes();
        if (bytes.length <= 2048) {
            PacketBuilder packetBuilder = new PacketBuilder((byte) 16);
            packetBuilder.put(bytes);
            return packetBuilder.build();
        }
        throw new IllegalArgumentException("Intent is too long");
    }

    public byte[] encodeInteractive(boolean z) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 19);
        packetBuilder.putBoolean(z);
        return packetBuilder.build();
    }

    public byte[] encodeKeyEvent(int i, int i2) {
        long j = this.mIndex;
        this.mIndex = 1 + j;
        return encodeKeyEvent(j, i, i2);
    }

    public byte[] encodeKeyEvent(long j, int i, int i2) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 2);
        packetBuilder.putLong(j).putInt(i).putInt(i2);
        return packetBuilder.build();
    }

    public byte[] encodeMotionEvent(int i, int[] iArr, TouchRecordUtil[] touchRecordUtilArr) {
        if (iArr.length > 255) {
            throw new IllegalArgumentException("can't have more than 255 pointers");
        } else if (touchRecordUtilArr.length <= 255) {
            byte length = (byte) iArr.length;
            byte length2 = (byte) touchRecordUtilArr.length;
            PacketBuilder packetBuilder = new PacketBuilder((byte) 3);
            packetBuilder.putInt(i).put(length);
            for (byte b = 0; b < length; b = (byte) (b + 1)) {
                packetBuilder.putInt(iArr[b]);
            }
            for (byte b2 = 0; b2 < length2; b2 = (byte) (b2 + 1)) {
                TouchRecordUtil touchRecordUtil = touchRecordUtilArr[b2];
                packetBuilder.putLong(touchRecordUtil.time);
                if (touchRecordUtil.locations.length == length) {
                    for (byte b3 = 0; b3 < length; b3 = (byte) (b3 + 1)) {
                        packetBuilder.putFloat(touchRecordUtil.locations[b3].xDip);
                        packetBuilder.putFloat(touchRecordUtil.locations[b3].yDip);
                    }
                } else {
                    packetBuilder.destroy();
                    throw new IllegalArgumentException("every pointer must be in every touch record");
                }
            }
            return packetBuilder.build();
        } else {
            throw new IllegalArgumentException("can't have more than 255 touches");
        }
    }

    public byte[] encodePacketVersionTooHigh() {
        return ENCODED_PACKET_VERSION_TOO_HIGH;
    }

    public byte[] encodePacketVersionTooLow() {
        return ENCODED_PACKET_VERSION_TOO_LOW;
    }

    public byte[] encodePerformEditorAction(int i) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 18);
        packetBuilder.put((byte) 1);
        packetBuilder.putInt(i);
        return packetBuilder.build();
    }

    public byte[] encodePing() {
        return ENCODED_PACKET_PING;
    }

    public byte[] encodePong() {
        return ENCODED_PACKET_PONG;
    }

    public byte[] encodeReplyGetCursorCapsMode(long j, int i) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 24);
        packetBuilder.putLong(j).putInt(i);
        return packetBuilder.build();
    }

    public byte[] encodeReplyGetExtractedText(long j, ExtractedText extractedText) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 25);
        packetBuilder.putLong(j).putExtractedText(extractedText);
        return packetBuilder.build();
    }

    public byte[] encodeReplyGetSelectedText(long j, CharSequence charSequence) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 26);
        packetBuilder.putLong(j).putCharSequence(charSequence);
        return packetBuilder.build();
    }

    public byte[] encodeReplyGetTextAfterCursor(long j, CharSequence charSequence) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 23);
        packetBuilder.putLong(j).putCharSequence(charSequence);
        return packetBuilder.build();
    }

    public byte[] encodeReplyGetTextBeforeCursor(long j, CharSequence charSequence) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 22);
        packetBuilder.putLong(j).putCharSequence(charSequence);
        return packetBuilder.build();
    }

    public byte[] encodeRequestCursorUpdates(int i) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 18);
        packetBuilder.put((byte) 9).putInt(i);
        return packetBuilder.build();
    }

    public byte[] encodeSetComposingRegion(int i, int i2) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 18);
        packetBuilder.put((byte) 10).putInt(i).putInt(i2);
        return packetBuilder.build();
    }

    public byte[] encodeSetComposingText(CharSequence charSequence, int i) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 18);
        packetBuilder.put((byte) 2).putCharSequence(charSequence).putInt(i);
        return packetBuilder.build();
    }

    public byte[] encodeSetSelection(int i, int i2) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 18);
        packetBuilder.put((byte) 11).putInt(i).putInt(i2);
        return packetBuilder.build();
    }

    @Deprecated
    public byte[] encodeShowIme() {
        return ENCODED_SHOW_IME;
    }

    public byte[] encodeShowIme(EditorInfo editorInfo, boolean z, ExtractedText extractedText) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 5);
        packetBuilder.putInt(editorInfo.inputType).putInt(editorInfo.imeOptions).putCharSequence(editorInfo.privateImeOptions).putCharSequence(editorInfo.actionLabel).putInt(editorInfo.actionId).putInt(editorInfo.initialSelStart).putInt(editorInfo.initialSelEnd).putInt(editorInfo.initialCapsMode).putCharSequence(editorInfo.hintText).putCharSequence(editorInfo.label).putCharSequence(editorInfo.packageName).putInt(editorInfo.fieldId).putCharSequence(editorInfo.fieldName).putBoolean(z).put(Byte.MAX_VALUE).putExtractedText(extractedText);
        return packetBuilder.build();
    }

    public byte[] encodeStartVoice() {
        return ENCODED_PACKET_START_VOICE;
    }

    public byte[] encodeStopVoice() {
        return ENCODED_PACKET_STOP_VOICE;
    }

    public byte[] encodeString(String str) {
        byte[] bytes = str.getBytes();
        if (bytes.length <= 1024) {
            PacketBuilder packetBuilder = new PacketBuilder((byte) 15);
            packetBuilder.put(bytes);
            return packetBuilder.build();
        }
        throw new IllegalArgumentException("String is too long");
    }

    public byte[] encodeTakeBugReport() {
        return ENCODED_TAKE_BUGREPORT;
    }

    public byte[] encodeTransmitBundle(int i, Bundle bundle) throws InvalidParameterException {
        boolean z;
        int i2;
        boolean z2;
        PacketBuilder packetBuilder = new PacketBuilder((byte) 35);
        if (i > 0 && i <= 4) {
            packetBuilder.putInt(16449536 | i);
            Set<String> keySet = bundle.keySet();
            int size = (short) keySet.size();
            packetBuilder.putShort((short) size);
            byte[] bArr = new byte[size];
            int i3 = 0;
            for (String str : keySet) {
                Object obj = bundle.get(str);
                if (!(obj instanceof Byte)) {
                    z = false;
                } else {
                    bArr[i3] = 0;
                    i3++;
                    z = true;
                }
                if (obj instanceof Boolean) {
                    bArr[i3] = 5;
                    i3++;
                    z = true;
                }
                if (obj instanceof Float) {
                    bArr[i3] = 4;
                    i3++;
                    z = true;
                }
                if (obj instanceof Integer) {
                    bArr[i3] = 2;
                    i3++;
                    z = true;
                }
                if (obj instanceof Long) {
                    bArr[i3] = 3;
                    i3++;
                    z = true;
                }
                if (obj instanceof Short) {
                    bArr[i3] = 1;
                    i3++;
                    z = true;
                }
                if (!(obj instanceof String)) {
                    i2 = i3;
                    z2 = z;
                } else {
                    i2 = i3 + 1;
                    bArr[i3] = 6;
                    z2 = true;
                }
                if (z2) {
                    i3 = i2;
                } else {
                    packetBuilder.build();
                    throw new InvalidParameterException("Bundle contains unsupported variable types");
                }
            }
            packetBuilder.put(bArr);
            for (String str2 : keySet) {
                packetBuilder.putCharSequence(str2);
                Object obj2 = bundle.get(str2);
                if (obj2 instanceof Byte) {
                    packetBuilder.put(((Byte) obj2).byteValue());
                }
                if (obj2 instanceof Boolean) {
                    packetBuilder.putBoolean(((Boolean) obj2).booleanValue());
                }
                if (obj2 instanceof Float) {
                    packetBuilder.putFloat(((Float) obj2).floatValue());
                }
                if (obj2 instanceof Integer) {
                    packetBuilder.putInt(((Integer) obj2).intValue());
                }
                if (obj2 instanceof Long) {
                    packetBuilder.putLong(((Long) obj2).longValue());
                }
                if (obj2 instanceof Short) {
                    packetBuilder.putShort(((Short) obj2).shortValue());
                }
                if (obj2 instanceof String) {
                    packetBuilder.putCharSequence((String) obj2);
                }
            }
            return packetBuilder.build();
        }
        packetBuilder.build();
        throw new InvalidParameterException("Message type not recognized.");
    }

    public byte[] encodeVoiceConfig(int i, int i2, int i3) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 13);
        packetBuilder.putInt(i).putInt(i2).putInt(i3);
        return packetBuilder.build();
    }

    public byte[] encodeVoicePacket(byte[] bArr) {
        PacketBuilder packetBuilder = new PacketBuilder((byte) 14);
        packetBuilder.put(bArr);
        return packetBuilder.build();
    }
}
