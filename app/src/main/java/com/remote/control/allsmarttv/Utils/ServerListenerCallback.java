package com.remote.control.allsmarttv.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.ExtractedTextRequest;

public interface ServerListenerCallback<T> {
    void badPacket(String str, T t);

    void badPacketVersion(byte b, T t);

    void beginBatchEdit(T t);

    void commitCompletion(CompletionInfo completionInfo, T t);

    void commitText(CharSequence charSequence, int i, T t);

    void configure(int i, int i2, byte b, byte b2, String str, T t);

    void deleteSurroundingText(int i, int i2, T t);

    void endBatchEdit(T t);

    void finishComposingText(T t);

    void getCursorCapsMode(long j, int i, T t);

    void getExtractedText(long j, ExtractedTextRequest extractedTextRequest, int i, T t);

    void getSelectedText(long j, int i, T t);

    void getTextAfterCursor(long j, int i, int i2, T t);

    void getTextBeforeCursor(long j, int i, int i2, T t);

    void intent(Intent intent, T t);

    void keyEvent(long j, int i, int i2, T t);

    void motionEvent(int i, int[] iArr, TouchRecordUtil[] touchRecordUtilArr, T t);

    void onCancelBugReport(T t);

    void onInteractive(boolean z, T t);

    void onPong(T t);

    void onReceivedBundle(int i, Bundle bundle, T t);

    void onTakeBugReport(T t);

    void performEditorAction(int i, T t);

    void requestCursorUpdates(int i, T t);

    void setComposingRegion(int i, int i2, T t);

    void setComposingText(CharSequence charSequence, int i, T t);

    void setSelection(int i, int i2, T t);

    void startVoice(T t);

    void stopVoice(T t);

    void string(String str, T t);

    void voiceConfig(int i, int i2, int i3, T t);

    void voicePacket(byte[] bArr, T t);
}
