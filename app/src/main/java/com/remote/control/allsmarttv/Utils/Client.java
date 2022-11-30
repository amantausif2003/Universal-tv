package com.remote.control.allsmarttv.Utils;

import android.os.Bundle;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;

import java.util.Map;

public interface Client {

    public interface Listener {
        void onAsset(String str, Map<String, String> map, byte[] bArr);

        void onBadMessage(int i);

        void onBugReportStatus(int i);

        void onCapabilities(CapabilitiesClass capabilitiesClass);

        void onConnectFailed(Exception exc);

        void onConnected();

        void onConnecting();

        void onDeveloperStatus(boolean z);

        void onDisconnected();

        void onException(Exception exc);

        void onReceiveCompletionInfo(CompletionInfo[] completionInfoArr);

        void onReceiveConfigureFailure(int i);

        void onReceiveConfigureSuccess(int i, String str, BuildInfo buildInfo);

        void onReceiveHideIme();

        void onReceivePacketVersionTooHigh(byte b);

        void onReceivePacketVersionTooLow(byte b);

        void onReceiveShowIme(EditorInfo editorInfo, boolean z, ExtractedText extractedText, boolean z2);

        void onReceiveStartVoice();

        void onReceiveStopVoice();

        void onResponse(long j, Object obj);

        void onSslHandshakeCompleted();

        void onSslHandshakeFailed(Exception exc);

        void receivedBundle(int i, Bundle bundle);
    }
}
