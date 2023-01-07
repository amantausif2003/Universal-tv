package com.remote.control.allsmarttv.utils;

import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;

import androidx.collection.ArrayMap;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;


public class ClientPackParser extends PacketParser {
    private static final boolean DEBUG = false;
    private static final String TAG = "AtvRemote.ClntPcktPrsr";
    private ClientListener mListener;

    public ClientPackParser() {
    }

    public ClientPackParser(ClientListener clientListener) {
        this.mListener = clientListener;
    }

    @Override
    public int parse(byte[] bArr) {
        boolean z = true;
        byte[] bArr2 = null;
        bArr2 = null;
        ExtractedText extractedText = null;
        BuildInfo buildInfo = null;
        boolean z2 = false;
        if (bArr == null) {
            this.mListener.badPacket("packet was null");
            return -1;
        } else if (bArr.length >= 4) {
            ByteBuffer wrap = ByteBuffer.wrap(bArr);
            byte b = wrap.get();
            byte b2 = wrap.get();
            short s = wrap.getShort();
            if (b != 1) {
                this.mListener.badPacketVersion(b);
                return -1;
            } else if (b2 < 0 || b2 > 36) {
                this.mListener.badPacket("malformed messageType: " + ((int) b2));
                return -1;
            } else if (wrap.remaining() < s) {
                this.mListener.badPacket("record too short, expected " + ((int) s) + " bytes after header but " + "there's only " + wrap.remaining());
                return -3;
            } else if (s >= PackConstants.MIN_MESSAGE_LENGTH[b2]) {
                switch (b2) {
                    case 5:
                        if (bArr.length <= 4) {
                            this.mListener.showIme(null, false, null, false);
                            break;
                        } else {
                            EditorInfo editorInfo = new EditorInfo();
                            editorInfo.inputType = wrap.getInt();
                            editorInfo.imeOptions = wrap.getInt();
                            editorInfo.privateImeOptions = getString(wrap);
                            editorInfo.actionLabel = getCharSequence(wrap);
                            editorInfo.actionId = wrap.getInt();
                            editorInfo.initialSelStart = wrap.getInt();
                            editorInfo.initialSelEnd = wrap.getInt();
                            editorInfo.initialCapsMode = wrap.getInt();
                            editorInfo.hintText = getCharSequence(wrap);
                            editorInfo.label = getCharSequence(wrap);
                            editorInfo.packageName = getString(wrap);
                            editorInfo.fieldId = wrap.getInt();
                            editorInfo.fieldName = getString(wrap);
                            boolean z3 = wrap.get() == 1;
                            if (bArr.length <= wrap.position()) {
                                z = false;
                            } else if (wrap.get() == Byte.MAX_VALUE) {
                                extractedText = getExtracted(wrap);
                            }
                            this.mListener.showIme(editorInfo, z3, extractedText, z);
                            break;
                        }
                    case 6:
                        this.mListener.hideIme();
                        break;
                    case 7:
                        if (bArr.length <= 4) {
                            this.mListener.configureSuccess(0, null, null);
                            break;
                        } else {
                            int i = wrap.getInt();
                            String string = getString(wrap);
                            if (bArr.length > wrap.position()) {
                                buildInfo = new BuildInfo();
                                buildInfo.fingerprint = getString(wrap);
                                buildInfo.id = getString(wrap);
                                buildInfo.manufacturer = getString(wrap);
                                buildInfo.model = getString(wrap);
                                buildInfo.sdk = wrap.getInt();
                            }
                            this.mListener.configureSuccess(i, string, buildInfo);
                            break;
                        }
                    case 8:
                        this.mListener.configureFailure(wrap.getInt());
                        break;
                    case 9:
                        this.mListener.packetVersionTooHigh(wrap.get());
                        break;
                    case 10:
                        this.mListener.packetVersionTooLow(wrap.get());
                        break;
                    case 11:
                        this.mListener.startVoice();
                        break;
                    case 12:
                        this.mListener.stopVoice();
                        break;
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 18:
                    case 19:
                    case 21:
                    case 28:
                    case 29:
                    case 31:
                    default:
                        return -4;
                    case 17:
                        int i2 = wrap.getInt();
                        CompletionInfo[] completionInfoArr = new CompletionInfo[i2];
                        for (int i3 = 0; i3 < i2; i3++) {
                            completionInfoArr[i3] = new CompletionInfo(wrap.getLong(), wrap.getInt(), getCharSequence(wrap), getCharSequence(wrap));
                        }
                        this.mListener.onCompletionInfo(completionInfoArr);
                        break;
                    case 20:
                        this.mListener.onPing();
                        break;
                    case 22:
                        this.mListener.onReplyGetTextBeforeCursor(wrap.getLong(), getCharSequence(wrap));
                        break;
                    case 23:
                        this.mListener.onReplyGetTextAfterCursor(wrap.getLong(), getCharSequence(wrap));
                        break;
                    case 24:
                        this.mListener.onReplyGetCursorCapsMode(wrap.getLong(), wrap.getInt());
                        break;
                    case 25:
                        this.mListener.onReplyGetExtractedText(wrap.getLong(), getExtracted(wrap));
                        break;
                    case 26:
                        this.mListener.onReplyGetSelectedText(wrap.getLong(), getCharSequence(wrap));
                        break;
                    case 27:
                        if (wrap.get() == 1) {
                            z2 = true;
                        }
                        this.mListener.onDeveloperStatus(z2);
                        break;
                    case 30:
                        this.mListener.onBugReportStatus(wrap.getInt());
                        break;
                    case 32:
                        String string2 = getString(wrap);
                        String string3 = getString(wrap);
                        int i4 = wrap.getInt();
                        int i5 = wrap.getInt();
                        int i6 = wrap.getInt();
                        ArrayMap arrayMap = new ArrayMap();
                        for (int i7 = 0; i7 < i6; i7++) {
                            arrayMap.put(getString(wrap), getString(wrap));
                        }
                        this.mListener.onAssetHeader(string2, string3, i4, i5, arrayMap);
                        break;
                    case 33:
                        this.mListener.onAssetFooter(getString(wrap), wrap.getInt());
                        break;
                    case 34:
                        String string4 = getString(wrap);
                        int i8 = wrap.getInt();
                        int i9 = wrap.getInt();
                        int i10 = wrap.getInt();
                        byte[] bArr3 = new byte[i10];
                        wrap.get(bArr3, 0, i10);
                        this.mListener.onAssetChunk(string4, i8, i9, bArr3);
                        break;
                    case 35:
                        Bundle bundle = new Bundle();
                        int parseBundleFromBuffer = parseBundleFromBuffer(wrap, bundle);
                        if (parseBundleFromBuffer > 0 && parseBundleFromBuffer <= 4) {
                            this.mListener.onReceivedBundle(parseBundleFromBuffer, bundle);
                            break;
                        } else {
                            Log.e(TAG, "Unrecognized bundle message type.");
                            return -1;
                        }
                    case 36:
                        int i11 = wrap.getInt();
                        if (i11 != 0) {
                            bArr2 = new byte[i11];
                            try {
                                wrap.get(bArr2);
                            } catch (BufferUnderflowException e) {
                                Log.e(TAG, "failed to parse MT_CAPABILITIES");
                                return -1;
                            }
                        }
                        if (bArr2 != null) {
                            this.mListener.onCapabilities(new CapabilitiesClass.Builder(ByteBuffer.wrap(bArr2)).build());
                            break;
                        }
                        break;
                }
                return s;
            } else {
                this.mListener.badPacket("record too short, expected " + ((int) PackConstants.MIN_MESSAGE_LENGTH[b2]) + " bytes for message type " + ((int) b2) + ", but only received " + ((int) s));
                return -3;
            }
        } else {
            this.mListener.badPacket("packet too short to contain header");
            return -3;
        }
    }

    public void setListener(ClientListener clientListener) {
        this.mListener = clientListener;
    }
}
