package com.remote.control.allsmarttv.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

class PacketEDTestClass {
    ClientPackParser clientParser = new ClientPackParser();
    PackEncoderClass encoder = new PackEncoderClass();
    private boolean fail;
    private boolean response;
    ServerPacketUtil<Object> serverParser = new ServerPacketUtil<>();

    public static class ClientAdapter implements ClientListener {
        @Override
        public void badPacket(String str) {
        }

        @Override
        public void badPacketVersion(byte b) {
        }

        @Override
        public void configureFailure(int i) {
        }

        @Override
        public void configureSuccess(int i, String str, BuildInfo buildInfo) {
        }

        @Override
        public void hideIme() {
        }

        @Override
        public void onAssetChunk(String str, int i, int i2, byte[] bArr) {
        }

        @Override
        public void onAssetFooter(String str, int i) {
        }

        @Override
        public void onAssetHeader(String str, String str2, int i, int i2, Map<String, String> map) {
        }

        @Override
        public void onBugReportStatus(int i) {
        }

        @Override
        public void onCapabilities(CapabilitiesClass capabilitiesClass) {
        }

        @Override
        public void onCompletionInfo(CompletionInfo[] completionInfoArr) {
        }

        @Override
        public void onDeveloperStatus(boolean z) {
        }

        @Override
        public void onPing() {
        }

        @Override
        public void onReceivedBundle(int i, Bundle bundle) {
        }

        @Override
        public void onReplyGetCursorCapsMode(long j, int i) {
        }

        @Override
        public void onReplyGetExtractedText(long j, ExtractedText extractedText) {
        }

        @Override
        public void onReplyGetSelectedText(long j, CharSequence charSequence) {
        }

        @Override
        public void onReplyGetTextAfterCursor(long j, CharSequence charSequence) {
        }

        @Override
        public void onReplyGetTextBeforeCursor(long j, CharSequence charSequence) {
        }

        @Override
        public void packetVersionTooHigh(byte b) {
        }

        @Override
        public void packetVersionTooLow(byte b) {
        }

        @Override
        public void showIme(EditorInfo editorInfo, boolean z, ExtractedText extractedText, boolean z2) {
        }

        @Override
        public void startVoice() {
        }

        @Override
        public void stopVoice() {
        }
    }

    public static class ServerAdapterCallback implements ServerListenerCallback {
        @Override
        public void badPacket(String str, Object obj) {
        }

        @Override
        public void badPacketVersion(byte b, Object obj) {
        }

        @Override
        public void beginBatchEdit(Object obj) {
        }

        @Override
        public void commitCompletion(CompletionInfo completionInfo, Object obj) {
        }

        @Override
        public void commitText(CharSequence charSequence, int i, Object obj) {
        }

        @Override
        public void configure(int i, int i2, byte b, byte b2, String str, Object obj) {
        }

        @Override
        public void deleteSurroundingText(int i, int i2, Object obj) {
        }

        @Override
        public void endBatchEdit(Object obj) {
        }

        @Override
        public void finishComposingText(Object obj) {
        }

        @Override
        public void getCursorCapsMode(long j, int i, Object obj) {
        }

        @Override
        public void getExtractedText(long j, ExtractedTextRequest extractedTextRequest, int i, Object obj) {
        }

        @Override
        public void getSelectedText(long j, int i, Object obj) {
        }

        @Override
        public void getTextAfterCursor(long j, int i, int i2, Object obj) {
        }

        @Override
        public void getTextBeforeCursor(long j, int i, int i2, Object obj) {
        }

        @Override
        public void intent(Intent intent, Object obj) {
        }

        @Override
        public void keyEvent(long j, int i, int i2, Object obj) {
        }

        @Override
        public void motionEvent(int i, int[] iArr, TouchRecordUtil[] touchRecordUtilArr, Object obj) {
        }

        @Override
        public void onCancelBugReport(Object obj) {
        }

        @Override
        public void onInteractive(boolean z, Object obj) {
        }

        @Override
        public void onPong(Object obj) {
        }

        @Override
        public void onReceivedBundle(int i, Bundle bundle, Object obj) {
        }

        @Override
        public void onTakeBugReport(Object obj) {
        }

        @Override
        public void performEditorAction(int i, Object obj) {
        }

        @Override
        public void requestCursorUpdates(int i, Object obj) {
        }

        @Override
        public void setComposingRegion(int i, int i2, Object obj) {
        }

        @Override
        public void setComposingText(CharSequence charSequence, int i, Object obj) {
        }

        @Override
        public void setSelection(int i, int i2, Object obj) {
        }

        @Override
        public void startVoice(Object obj) {
        }

        @Override
        public void stopVoice(Object obj) {
        }

        @Override
        public void string(String str, Object obj) {
        }

        @Override
        public void voiceConfig(int i, int i2, int i3, Object obj) {
        }

        @Override
        public void voicePacket(byte[] bArr, Object obj) {
        }
    }

    PacketEDTestClass() {
    }

    static void assertEquals(byte b, byte b2) {
        if (b != b2) {
            throw new RuntimeException("Got " + ((int) b) + ", expected " + ((int) b2));
        }
    }

    static void assertEquals(float f, float f2) {
        if (f != f2) {
            throw new RuntimeException("Got " + f + ", expected " + f2);
        }
    }

    static void assertEquals(int i, int i2) {
        if (i != i2) {
            throw new RuntimeException("Got " + i + ", expected " + i2);
        }
    }

    static void assertEquals(long j, long j2) {
        if (j != j2) {
            throw new RuntimeException("Got " + j + ", expected " + j2);
        }
    }

    static void assertEquals(Object obj, Object obj2) {
        if ((obj != null && !obj.equals(obj2)) || (obj == null && obj2 != null)) {
            throw new RuntimeException("Got " + obj + ", expected " + obj2);
        }
    }

    public static void main(String[] strArr) {
        PacketEDTestClass packetEDTestClass = new PacketEDTestClass();
        packetEDTestClass.testConfigure();
        packetEDTestClass.testKeyevent();
        packetEDTestClass.testMotionevent();
        packetEDTestClass.testShowime();
        packetEDTestClass.testHideime();
        packetEDTestClass.testConfigureSuccess();
        packetEDTestClass.testConfigureFailure();
        packetEDTestClass.testPacketVersionTooHigh();
        packetEDTestClass.testPacketVersionTooLow();
        packetEDTestClass.testBadPackets();
        packetEDTestClass.testBadPacketVersions();
        packetEDTestClass.testStreamReading();
    }

    private void testBadPacketVersions() {
        this.clientParser.setListener(new ClientAdapter() {

            @Override
            public void badPacketVersion(byte b) {
                PacketEDTestClass.this.response = true;
                PacketEDTestClass.assertEquals((int) b, 32);
            }

            @Override
            public void showIme(EditorInfo editorInfo, boolean z, ExtractedText extractedText, boolean z2) {
                PacketEDTestClass.this.fail = true;
            }
        });
        byte[] encodeShowIme = this.encoder.encodeShowIme();
        encodeShowIme[0] = 32;
        this.fail = false;
        this.response = false;
        this.clientParser.parse(encodeShowIme);
        if (!this.response) {
            throw new RuntimeException("bad packet version was not called");
        } else if (!this.fail) {
            System.out.println("bad packet version test passed");
        } else {
            throw new RuntimeException("parser called showIme but it should have failed");
        }
    }

    private void testBadPackets() {
        this.serverParser.setListener(new ServerAdapterCallback() {

            @Override
            public void badPacket(String str, Object obj) {
            }
        });
        this.serverParser.parse(null);
        for (int i = 0; i < 20; i++) {
            try {
                byte[] bArr = new byte[i];
                if (i > 0) {
                    bArr[0] = 1;
                }
                this.serverParser.parse(bArr);
            } catch (RuntimeException e) {
                System.out.println("Failed on buffer size " + i);
                throw e;
            }
        }
        this.serverParser.parse(new byte[100]);
        this.serverParser.parse(new byte[10000]);
        Random random = new Random();
        for (int i2 = 0; i2 < 10000; i2++) {
            int nextInt = random.nextInt(2048);
            byte[] bArr2 = new byte[nextInt];
            random.nextBytes(bArr2);
            if (nextInt > 0) {
                bArr2[0] = 1;
            }
            this.serverParser.parse(bArr2);
        }
        this.serverParser.parse(new byte[]{1, 8, 0, 8});
        this.serverParser.parse(new byte[]{1, 0, 0, 4});
        System.out.println("bad packet test passed");
    }

    private void testConfigure() {
        this.serverParser.setListener(new ServerAdapterCallback() {

            @Override
            public void configure(int i, int i2, byte b, byte b2, String str, Object obj) {
                PacketEDTestClass.this.response = true;
                PacketEDTestClass.assertEquals(i, 800);
                PacketEDTestClass.assertEquals(i2, 600);
                PacketEDTestClass.assertEquals((int) b, 32);
                PacketEDTestClass.assertEquals((int) b2, 1);
            }
        });
        byte[] encodeConfigure = this.encoder.encodeConfigure(800, 600, (byte) 32, (byte) 1, null);
        assertEquals(encodeConfigure.length, 16);
        assertResponse("configure", encodeConfigure, this.serverParser);
    }

    private void testConfigureFailure() {
        this.clientParser.setListener(new ClientAdapter() {

            @Override
            public void configureFailure(int i) {
                PacketEDTestClass.this.response = true;
                PacketEDTestClass.assertEquals(i, 572657937);
            }
        });
        byte[] encodeConfigureFailure = this.encoder.encodeConfigureFailure(572657937);
        assertEquals(encodeConfigureFailure.length, 8);
        assertResponse("configureFailure", encodeConfigureFailure, this.clientParser);
    }

    private void testConfigureSuccess() {
        this.clientParser.setListener(new ClientAdapter() {

            @Override
            public void configureSuccess(int i, String str, BuildInfo buildInfo) {
                PacketEDTestClass.this.response = true;
            }
        });
        byte[] encodeConfigureSuccess = this.encoder.encodeConfigureSuccess(0, null);
        assertEquals(encodeConfigureSuccess.length, 4);
        assertResponse("configureSuccess", encodeConfigureSuccess, this.clientParser);
    }

    private void testHideime() {
        this.clientParser.setListener(new ClientAdapter() {
            /* class com.remote.tv.remote.Utils.PacketEncodeDecodeTest.AnonymousClass5 */

            @Override // com.remote.tv.remote.Utils.PacketEncodeDecodeTest.ClientAdapter, com.remote.tv.remote.Interfaces.OnClientCommandListener
            public void hideIme() {
                PacketEDTestClass.this.response = true;
            }
        });
        byte[] encodeHideIme = this.encoder.encodeHideIme();
        assertEquals(encodeHideIme.length, 4);
        assertResponse("hideIme", encodeHideIme, this.clientParser);
    }

    private void testKeyevent() {
        this.serverParser.setListener(new ServerAdapterCallback() {

            @Override
            public void keyEvent(long j, int i, int i2, Object obj) {
                PacketEDTestClass.this.response = true;
                PacketEDTestClass.assertEquals(j, 4294967296L);
                PacketEDTestClass.assertEquals(i, -559038737);
                PacketEDTestClass.assertEquals(i2, 267235309);
            }
        });
        byte[] encodeKeyEvent = this.encoder.encodeKeyEvent(4294967296L, -559038737, 267235309);
        assertEquals(encodeKeyEvent.length, 20);
        assertResponse("keyEvent", encodeKeyEvent, this.serverParser);
    }

    private void testMotionevent() {
        final int[] iArr = {0, 4096, 1048576};
        final TouchRecordUtil[] touchRecordUtilArr = {new TouchRecordUtil(4294967296L, new TouchRecordUtil.Location[]{new TouchRecordUtil.Location(0.5f, 0.2f), new TouchRecordUtil.Location(100.0f, 0.2f), new TouchRecordUtil.Location(30.0f, 10101.01f)}), new TouchRecordUtil(4294967303L, new TouchRecordUtil.Location[]{new TouchRecordUtil.Location(1.5f, 0.2f), new TouchRecordUtil.Location(700.0f, 0.2f), new TouchRecordUtil.Location(20.0f, 10101.01f)})};
        this.serverParser.setListener(new ServerAdapterCallback() {

            @Override
            public void motionEvent(int i, int[] iArr, TouchRecordUtil[] touchRecordUtilArr, Object obj) {
                PacketEDTestClass.this.response = true;
                PacketEDTestClass.assertEquals(i, 2);
                PacketEDTestClass.assertEquals(iArr.length, iArr.length);
                for (int i2 = 0; i2 < iArr.length; i2++) {
                    try {
                        PacketEDTestClass.assertEquals(iArr[i2], iArr[i2]);
                    } catch (RuntimeException e) {
                        System.out.println("failed on pointer " + i2);
                        throw e;
                    }
                }
                PacketEDTestClass.assertEquals(touchRecordUtilArr.length, touchRecordUtilArr.length);
                for (int i3 = 0; i3 < touchRecordUtilArr.length; i3++) {
                    TouchRecordUtil touchRecordUtil = touchRecordUtilArr[i3];
                    TouchRecordUtil touchRecordUtil2 = touchRecordUtilArr[i3];
                    try {
                        assertEquals(touchRecordUtil.time, touchRecordUtil2.time);
                        assertEquals(touchRecordUtil.locations.length, touchRecordUtil2.locations.length);
                        for (int i4 = 0; i4 < touchRecordUtil.locations.length; i4++) {
                            TouchRecordUtil.Location location = touchRecordUtil.locations[i4];
                            TouchRecordUtil.Location location2 = touchRecordUtil2.locations[i4];
                            try {
                                assertEquals(location.xDip, location2.xDip);
                                assertEquals(location.yDip, location2.yDip);
                            } catch (RuntimeException e2) {
                                System.out.println("failed on location " + i4);
                                throw e2;
                            }
                        }
                    } catch (RuntimeException e3) {
                        System.out.println("failed on touch " + i3);
                        throw e3;
                    }
                }
            }
        });
        assertResponse("motionEvent", this.encoder.encodeMotionEvent(2, iArr, touchRecordUtilArr), this.serverParser);
    }

    private void testPacketVersionTooHigh() {
        this.clientParser.setListener(new ClientAdapter() {
            /* class com.remote.tv.remote.Utils.PacketEncodeDecodeTest.AnonymousClass8 */

            @Override // com.remote.tv.remote.Utils.PacketEncodeDecodeTest.ClientAdapter, com.remote.tv.remote.Interfaces.OnClientCommandListener
            public void packetVersionTooHigh(byte b) {
                PacketEDTestClass.this.response = true;
                PacketEDTestClass.assertEquals(b, (byte) 1);
            }
        });
        byte[] encodePacketVersionTooHigh = this.encoder.encodePacketVersionTooHigh();
        assertEquals(encodePacketVersionTooHigh.length, 8);
        assertResponse("packetVersionTooHigh", encodePacketVersionTooHigh, this.clientParser);
    }

    private void testPacketVersionTooLow() {
        this.clientParser.setListener(new ClientAdapter() {
            /* class com.remote.tv.remote.Utils.PacketEncodeDecodeTest.AnonymousClass9 */

            @Override // com.remote.tv.remote.Utils.PacketEncodeDecodeTest.ClientAdapter, com.remote.tv.remote.Interfaces.OnClientCommandListener
            public void packetVersionTooLow(byte b) {
                PacketEDTestClass.this.response = true;
                PacketEDTestClass.assertEquals(b, (byte) 1);
            }
        });
        byte[] encodePacketVersionTooLow = this.encoder.encodePacketVersionTooLow();
        assertEquals(encodePacketVersionTooLow.length, 8);
        assertResponse("packetVersionTooLow", encodePacketVersionTooLow, this.clientParser);
    }

    private void testShowime() {
        this.clientParser.setListener(new ClientAdapter() {
            /* class com.remote.tv.remote.Utils.PacketEncodeDecodeTest.AnonymousClass4 */

            @Override // com.remote.tv.remote.Utils.PacketEncodeDecodeTest.ClientAdapter, com.remote.tv.remote.Interfaces.OnClientCommandListener
            public void showIme(EditorInfo editorInfo, boolean z, ExtractedText extractedText, boolean z2) {
                PacketEDTestClass.this.response = true;
            }
        });
        byte[] encodeShowIme = this.encoder.encodeShowIme();
        assertEquals(encodeShowIme.length, 4);
        assertResponse("showIme", encodeShowIme, this.clientParser);
    }

    private void testStreamReading() {
        byte[] bArr = new byte[260];
        bArr[0] = 1;
        bArr[1] = 5;
        bArr[2] = 1;
        bArr[3] = 0;
        byte[] bArr2 = new byte[bArr.length];
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
        try {
            ClientPackParser clientPackParser = this.clientParser;
            assertEquals(ClientPackParser.readPacket(byteArrayInputStream, bArr2), bArr.length);
            System.out.println("stream reading test passed");
        } catch (IOException e) {
            throw new RuntimeException("failure reading stream", e);
        }
    }

    public void assertResponse(String str, byte[] bArr, PacketParser packetParser) {
        this.response = false;
        packetParser.parse(bArr);
        if (this.response) {
            System.out.println(str + " test passed");
            return;
        }
        throw new RuntimeException(str + " was not called");
    }
}
