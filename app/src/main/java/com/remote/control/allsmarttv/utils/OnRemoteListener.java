package com.remote.control.allsmarttv.utils;

import android.os.Bundle;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;

public interface OnRemoteListener {
    boolean beginBatchEdit();

    boolean commitCompletion(CompletionInfo completionInfo);

    boolean commitText(CharSequence charSequence, int i);

    boolean deleteSurroundingText(int i, int i2);

    boolean endBatchEdit();

    boolean finishComposingText();

    int getCursorCapsMode(int i);

    ExtractedText getExtractedText(ExtractedTextRequest extractedTextRequest, int i);

    CharSequence getSelectedText(int i);

    CharSequence getTextAfterCursor(int i, int i2);

    CharSequence getTextBeforeCursor(int i, int i2);

    boolean isRecording();

    boolean performEditorAction(int i);

    boolean requestCursorUpdates(int i);

    void sendBundle(int i, Bundle bundle);

    void sendKeyEvent(int i, int i2);

    boolean setComposingRegion(int i, int i2);

    boolean setComposingText(CharSequence charSequence, int i);

    boolean setSelection(int i, int i2);

    void startVoice();

    void stopVoice();

    void takeBugReport();
}
