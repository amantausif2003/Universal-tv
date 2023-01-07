package com.remote.control.allsmarttv.utils;

import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;

public interface ImeListenerCall {
    EditorInfo getImeEditor();

    ExtractedText getImeExtracted();
}
