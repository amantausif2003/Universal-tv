package com.remote.control.allsmarttv.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputMethodManager;

public final class ImeUtil extends androidx.appcompat.widget.AppCompatEditText {

    private EditorInfo editorInfo;
    private Interceptor interceptor;

    public interface Interceptor extends InputConnection {
    }

    public ImeUtil(Context context) {
        this(context, null, 0);
    }

    public ImeUtil(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ImeUtil(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public ImeUtil setEditorInfo(EditorInfo editorInfo) {
        this.editorInfo = editorInfo;
        return this;
    }

    public void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
        if (onCreateInputConnection == null) {
            return null;
        }
        if (this.editorInfo != null) {
            editorInfo.inputType = this.editorInfo.inputType;
            editorInfo.imeOptions = this.editorInfo.imeOptions;
            editorInfo.initialSelEnd = this.editorInfo.initialSelEnd;
            editorInfo.initialSelStart = this.editorInfo.initialSelStart;
            editorInfo.initialCapsMode = this.editorInfo.initialCapsMode;
            editorInfo.actionLabel = this.editorInfo.actionLabel;
            editorInfo.hintText = this.editorInfo.hintText;
            editorInfo.label = this.editorInfo.label;
        } else {
            editorInfo.inputType = 32769;
            editorInfo.imeOptions = (editorInfo.imeOptions & 255) ^ editorInfo.imeOptions;
            editorInfo.imeOptions = 2;
            editorInfo.imeOptions |= 268435456;
        }
        return new InterceptConnection(onCreateInputConnection);
    }

    public boolean onCheckIsTextEditor() {
        return true;
    }

    public void onSelectionChanged(int i, int i2) {
        super.onSelectionChanged(i, i2);
        if (this.interceptor != null) {
            this.interceptor.setSelection(i, i2);
        }
    }

    private final class InterceptConnection extends InputConnectionWrapper {
        public InterceptConnection(InputConnection inputConnection) {
            super(inputConnection, false);
        }

        public boolean performContextMenuAction(int i) {
            ImeUtil.this.interceptor.performContextMenuAction(i);
            return super.performContextMenuAction(i);
        }

        public boolean performEditorAction(int i) {
            ImeUtil.this.interceptor.performEditorAction(i);
            return true;
        }

        public boolean setComposingText(CharSequence charSequence, int i) {
            ImeUtil.this.interceptor.setComposingText(charSequence, i);
            return super.setComposingText(charSequence, i);
        }

        public boolean sendKeyEvent(KeyEvent keyEvent) {
            ImeUtil.this.interceptor.sendKeyEvent(keyEvent);
            return super.sendKeyEvent(keyEvent);
        }

        public boolean commitText(CharSequence charSequence, int i) {
            ImeUtil.this.interceptor.commitText(charSequence, i);
            return super.commitText(charSequence, i);
        }

        public boolean deleteSurroundingText(int i, int i2) {
            ImeUtil.this.interceptor.deleteSurroundingText(i, i2);
            return super.deleteSurroundingText(i, i2);
        }

        public CharSequence getTextBeforeCursor(int i, int i2) {
            CharSequence textBeforeCursor = ImeUtil.this.interceptor.getTextBeforeCursor(i, i2);
            if (textBeforeCursor == null) {
                return super.getTextBeforeCursor(i, i2);
            }
            return textBeforeCursor;
        }

        public CharSequence getTextAfterCursor(int i, int i2) {
            CharSequence textAfterCursor = ImeUtil.this.interceptor.getTextAfterCursor(i, i2);
            if (textAfterCursor == null) {
                return super.getTextAfterCursor(i, i2);
            }
            return textAfterCursor;
        }

        public CharSequence getSelectedText(int i) {
            CharSequence selectedText = ImeUtil.this.interceptor.getSelectedText(i);
            if (selectedText == null) {
                return super.getSelectedText(i);
            }
            return selectedText;
        }

        public int getCursorCapsMode(int i) {
            int cursorCapsMode = ImeUtil.this.interceptor.getCursorCapsMode(i);
            if (-1 == cursorCapsMode) {
                return super.getCursorCapsMode(i);
            }
            return cursorCapsMode;
        }

        public ExtractedText getExtractedText(ExtractedTextRequest extractedTextRequest, int i) {
            ExtractedText extractedText = ImeUtil.this.interceptor.getExtractedText(extractedTextRequest, i);
            if (extractedText == null) {
                return super.getExtractedText(extractedTextRequest, i);
            }
            return extractedText;
        }

        public boolean setComposingRegion(int i, int i2) {
            ImeUtil.this.interceptor.setComposingRegion(i, i2);
            return super.setComposingRegion(i, i2);
        }

        public boolean finishComposingText() {
            ImeUtil.this.interceptor.finishComposingText();
            return super.finishComposingText();
        }

        public boolean commitCompletion(CompletionInfo completionInfo) {
            ImeUtil.this.interceptor.commitCompletion(completionInfo);
            return super.commitCompletion(completionInfo);
        }

        public boolean commitCorrection(CorrectionInfo correctionInfo) {
            ImeUtil.this.interceptor.commitCorrection(correctionInfo);
            return super.commitCorrection(correctionInfo);
        }

        public boolean setSelection(int i, int i2) {
            ImeUtil.this.interceptor.setSelection(i, i2);
            return super.setSelection(i, i2);
        }

        public boolean beginBatchEdit() {
            ImeUtil.this.interceptor.beginBatchEdit();
            return super.beginBatchEdit();
        }

        public boolean endBatchEdit() {
            ImeUtil.this.interceptor.endBatchEdit();
            return super.endBatchEdit();
        }

        public boolean clearMetaKeyStates(int i) {
            return super.clearMetaKeyStates(i);
        }

        public boolean reportFullscreenMode(boolean z) {
            return super.reportFullscreenMode(z);
        }

        public boolean performPrivateCommand(String str, Bundle bundle) {
            return super.performPrivateCommand(str, bundle);
        }

        @SuppressLint({"NewApi"})
        public boolean requestCursorUpdates(int i) {
            ImeUtil.this.interceptor.requestCursorUpdates(i);
            return super.requestCursorUpdates(i);
        }
    }

    public boolean onTextContextMenuItem(int i) {
        boolean onTextContextMenuItem = super.onTextContextMenuItem(i);
        this.interceptor.performContextMenuAction(i);
        return onTextContextMenuItem;
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (z) {
            showKeyboard();
        } else {
            hideKeyboard();
        }
    }

    public void hideKeyboard() {
        getInputManager().hideSoftInputFromWindow(getWindowToken(), 0);
    }

    public void showKeyboard() {
        requestFocus();
        getInputManager().showSoftInput(this, 0);
    }

    public void updateCompletionInfo(CompletionInfo[] completionInfoArr) {
        getInputManager().displayCompletions(this, completionInfoArr);
    }

    private InputMethodManager getInputManager() {
        return (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }
}
