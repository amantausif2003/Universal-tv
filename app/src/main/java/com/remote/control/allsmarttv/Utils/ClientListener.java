package com.remote.control.allsmarttv.Utils;

import android.os.Bundle;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;

import java.util.Map;

public interface ClientListener {
    void badPacket(String str);

    void badPacketVersion(byte b);

    void configureFailure(int i);

    void configureSuccess(int i, String str, BuildInfo buildInfo);

    void hideIme();

    void onAssetChunk(String str, int i, int i2, byte[] bArr);

    void onAssetFooter(String str, int i);

    void onAssetHeader(String str, String str2, int i, int i2, Map<String, String> map);

    void onBugReportStatus(int i);

    void onCapabilities(CapabilitiesClass capabilitiesClass);

    void onCompletionInfo(CompletionInfo[] completionInfoArr);

    void onDeveloperStatus(boolean z);

    void onPing();

    void onReceivedBundle(int i, Bundle bundle);

    void onReplyGetCursorCapsMode(long j, int i);

    void onReplyGetExtractedText(long j, ExtractedText extractedText);

    void onReplyGetSelectedText(long j, CharSequence charSequence);

    void onReplyGetTextAfterCursor(long j, CharSequence charSequence);

    void onReplyGetTextBeforeCursor(long j, CharSequence charSequence);

    void packetVersionTooHigh(byte b);

    void packetVersionTooLow(byte b);

    void showIme(EditorInfo editorInfo, boolean z, ExtractedText extractedText, boolean z2);

    void startVoice();

    void stopVoice();
}
