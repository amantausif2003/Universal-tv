package com.remote.control.allsmarttv.Utils;

import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;

public interface ImeListenerCall {
    EditorInfo getImeEditor();

    ExtractedText getImeExtracted();
}
