package com.remote.control.allsmarttv.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.ExtractedTextRequest;

import java.net.URISyntaxException;
import java.nio.ByteBuffer;

public class ServerPacketUtil<T> extends PacketParser {
    private static final boolean DEBUG = false;
    private static final String TAG = "AtvRemote.SrvrPcktPrsr";
    private ServerListenerCallback<T> mListener;

    public ServerPacketUtil() {
    }

    public ServerPacketUtil(ServerListenerCallback<T> serverListenerCallback) {
        this.mListener = serverListenerCallback;
    }

    private void parseConfigure(ByteBuffer byteBuffer, int i, T t) {
        int i2 = byteBuffer.getInt();
        int i3 = byteBuffer.getInt();
        byte b = byteBuffer.get();
        byte b2 = byteBuffer.get();
        byteBuffer.get();
        byteBuffer.get();
        String str = null;
        if (i > 12) {
            str = getString(byteBuffer);
        }
        this.mListener.configure(i2, i3, b, b2, str, t);
    }

    private boolean parseIme(ByteBuffer byteBuffer, T t) {
        switch (byteBuffer.get()) {
            case 1:
                this.mListener.performEditorAction(byteBuffer.getInt(), t);
                return true;
            case 2:
                this.mListener.setComposingText(getCharSequence(byteBuffer), byteBuffer.getInt(), t);
                return true;
            case 3:
                this.mListener.commitText(getCharSequence(byteBuffer), byteBuffer.getInt(), t);
                return true;
            case 4:
                this.mListener.deleteSurroundingText(byteBuffer.getInt(), byteBuffer.getInt(), t);
                return true;
            case 5:
                this.mListener.beginBatchEdit(t);
                return true;
            case 6:
                this.mListener.commitCompletion(new CompletionInfo(byteBuffer.getLong(), byteBuffer.getInt(), getCharSequence(byteBuffer), getCharSequence(byteBuffer)), t);
                return true;
            case 7:
                this.mListener.endBatchEdit(t);
                return true;
            case 8:
                this.mListener.finishComposingText(t);
                return true;
            case 9:
                this.mListener.requestCursorUpdates(byteBuffer.getInt(), t);
                return true;
            case 10:
                this.mListener.setComposingRegion(byteBuffer.getInt(), byteBuffer.getInt(), t);
                return true;
            case 11:
                this.mListener.setSelection(byteBuffer.getInt(), byteBuffer.getInt(), t);
                return true;
            case 12:
                this.mListener.getTextBeforeCursor(byteBuffer.getLong(), byteBuffer.getInt(), byteBuffer.getInt(), t);
                return true;
            case 13:
                this.mListener.getTextAfterCursor(byteBuffer.getLong(), byteBuffer.getInt(), byteBuffer.getInt(), t);
                return true;
            case 14:
                this.mListener.getCursorCapsMode(byteBuffer.getLong(), byteBuffer.getInt(), t);
                return true;
            case 15:
                long j = byteBuffer.getLong();
                ExtractedTextRequest extractedTextRequest = new ExtractedTextRequest();
                extractedTextRequest.token = byteBuffer.getInt();
                extractedTextRequest.flags = byteBuffer.getInt();
                extractedTextRequest.hintMaxLines = byteBuffer.getInt();
                extractedTextRequest.hintMaxChars = byteBuffer.getInt();
                this.mListener.getExtractedText(j, extractedTextRequest, byteBuffer.getInt(), t);
                return true;
            case 16:
                this.mListener.getSelectedText(byteBuffer.getLong(), byteBuffer.getInt(), t);
                return true;
            default:
                return false;
        }
    }

    private boolean parseIntent(ByteBuffer byteBuffer, int i, T t) {
        byte[] bArr = new byte[i];
        byteBuffer.get(bArr);
        try {
            this.mListener.intent(Intent.parseUri(new String(bArr), Intent.URI_INTENT_SCHEME), t);
            return true;
        } catch (URISyntaxException e) {
            this.mListener.badPacket("malformed intent", t);
            return false;
        }
    }

    private void parseKeyevent(ByteBuffer byteBuffer, T t) {
        this.mListener.keyEvent(byteBuffer.getLong(), byteBuffer.getInt(), byteBuffer.getInt(), t);
    }

    private boolean parseMotionevent(ByteBuffer byteBuffer, int i, T t) {
        int i2 = byteBuffer.getInt();
        int i3 = byteBuffer.get();
        if (i3 >= 0 && i3 < 32) {
            int[] iArr = new int[i3];
            for (int i4 = 0; i4 < i3; i4++) {
                iArr[i4] = byteBuffer.getInt();
                if (iArr[i4] < 0 || iArr[i4] >= 32) {
                    this.mListener.badPacket(String.format("invalid pointer id %d", Integer.valueOf(iArr[i4])), t);
                    return false;
                }
            }
            int i5 = (i - 5) - (i3 * 4);
            int i6 = (i3 * 8) + 8;
            int i7 = i5 / i6;
            if (i6 * i7 != i5) {
                this.mListener.badPacket("malformed motionEvent: not the right size", t);
                return false;
            } else if (i7 <= 240) {
                TouchRecordUtil[] touchRecordUtilArr = new TouchRecordUtil[i7];
                for (int i8 = 0; i8 < i7; i8++) {
                    long j = byteBuffer.getLong();
                    TouchRecordUtil.Location[] locationArr = new TouchRecordUtil.Location[i3];
                    for (int i9 = 0; i9 < i3; i9++) {
                        locationArr[i9] = new TouchRecordUtil.Location(byteBuffer.getFloat(), byteBuffer.getFloat());
                    }
                    touchRecordUtilArr[i8] = new TouchRecordUtil(j, locationArr);
                }
                this.mListener.motionEvent(i2, iArr, touchRecordUtilArr, t);
                return true;
            } else {
                this.mListener.badPacket("malformed motionEvent: unsupported number of touches", t);
                return false;
            }
        } else {
            this.mListener.badPacket("unsupported number of pointers", t);
            return false;
        }
    }

    private void parseString(ByteBuffer byteBuffer, int i, T t) {
        byte[] bArr = new byte[i];
        byteBuffer.get(bArr);
        this.mListener.string(new String(bArr), t);
    }

    private void parseVoiceConfig(ByteBuffer byteBuffer, T t) {
        this.mListener.voiceConfig(byteBuffer.getInt(), byteBuffer.getInt(), byteBuffer.getInt(), t);
    }

    private void parseVoicePacket(ByteBuffer byteBuffer, int i, T t) {
        byte[] bArr = new byte[i];
        byteBuffer.get(bArr);
        this.mListener.voicePacket(bArr, t);
    }

    @Override // com.remote.tv.remote.Utils.PacketParser
    public int parse(byte[] bArr) {
        return parse(bArr, null);
    }

    public int parse(byte[] bArr, T t) {
        if (bArr == null) {
            this.mListener.badPacket("packet was null", t);
            return -1;
        } else if (bArr.length >= 4) {
            ByteBuffer wrap = ByteBuffer.wrap(bArr);
            byte b = wrap.get();
            byte b2 = wrap.get();
            short s = wrap.getShort();
            if (b != 1) {
                this.mListener.badPacketVersion(b, t);
                return -1;
            } else if (b2 < 0 || b2 > 36) {
                this.mListener.badPacket("malformed messageType: " + ((int) b2), t);
                return -1;
            } else if (wrap.remaining() < s) {
                this.mListener.badPacket("record too short, expected " + ((int) s) + " bytes after header but " + "there's only " + wrap.remaining(), t);
                return -3;
            } else if (s >= PackConstants.MIN_MESSAGE_LENGTH[b2]) {
                switch (b2) {
                    case 0:
                        parseConfigure(wrap, s, t);
                        break;
                    case 1:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 17:
                    case 20:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 30:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    default:
                        return -4;
                    case 2:
                        parseKeyevent(wrap, t);
                        break;
                    case 3:
                        if (!parseMotionevent(wrap, s, t)) {
                            return -1;
                        }
                        break;
                    case 11:
                        this.mListener.startVoice(t);
                        break;
                    case 12:
                        this.mListener.stopVoice(t);
                        break;
                    case 13:
                        parseVoiceConfig(wrap, t);
                        break;
                    case 14:
                        parseVoicePacket(wrap, s, t);
                        break;
                    case 15:
                        parseString(wrap, s, t);
                        break;
                    case 16:
                        if (!parseIntent(wrap, s, t)) {
                            return -1;
                        }
                        break;
                    case 18:
                        if (!parseIme(wrap, t)) {
                            return -1;
                        }
                        break;
                    case 19:
                        parseInteractive(wrap, t);
                        break;
                    case 21:
                        this.mListener.onPong(t);
                        break;
                    case 28:
                        this.mListener.onTakeBugReport(t);
                        break;
                    case 29:
                        this.mListener.onCancelBugReport(t);
                        break;
                    case 35:
                        Bundle bundle = new Bundle();
                        int parseBundleFromBuffer = parseBundleFromBuffer(wrap, bundle);
                        if (parseBundleFromBuffer > 0 && parseBundleFromBuffer <= 4) {
                            this.mListener.onReceivedBundle(parseBundleFromBuffer, bundle, t);
                            break;
                        } else {
                            Log.e(TAG, "Unrecognized bundle message type.");
                            return -1;
                        }
                }
                return s;
            } else {
                this.mListener.badPacket("record too short, expected " + ((int) PackConstants.MIN_MESSAGE_LENGTH[b2]) + " bytes for message type " + ((int) b2) + ", but only received " + ((int) s), t);
                return -3;
            }
        } else {
            this.mListener.badPacket("packet too short to contain header", t);
            return -3;
        }
    }

    public void parseInteractive(ByteBuffer byteBuffer, T t) {
        byte b = byteBuffer.get();
        if (b == 0) {
            this.mListener.onInteractive(false, t);
        } else if (1 != b) {
            this.mListener.badPacket("malformed interactive message", t);
        } else {
            this.mListener.onInteractive(true, t);
        }
    }

    public void setListener(ServerListenerCallback<T> serverListenerCallback) {
        this.mListener = serverListenerCallback;
    }
}
