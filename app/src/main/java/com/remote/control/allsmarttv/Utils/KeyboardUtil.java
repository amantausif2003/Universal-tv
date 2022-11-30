package com.remote.control.allsmarttv.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.jaku.core.JakuRequest;
import com.jaku.core.KeypressKeyValues;
import com.jaku.request.KeypressRequest;
import com.remote.control.allsmarttv.R;

import java.util.ArrayDeque;
import java.util.Deque;

public class KeyboardUtil extends DialogFragment {

    private String oldText = "";
    private EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.key_layout, null);
        editText = (EditText) view.findViewById(R.id.edit_text);

        setdata();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        showKey(editText);

    }

    private void showKey(final View view) {
        if (view.requestFocus()) {
            final InputMethodManager imm = (InputMethodManager)
                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.requestFocus();
                    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);
        }
    }

    private void setdata() {
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (event.getKeyCode() == 67 &&
                            event.getAction() == KeyEvent.ACTION_DOWN) {
                        sendBackspace();
                        return true;
                    }
                }
                return false;
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 67 &&
                        event.getAction() == KeyEvent.ACTION_DOWN) {
                    sendBackspace();
                    return true;
                }

                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();

                int difference = newText.length() - oldText.length();

                // clear button was pressed
                if (newText.equals("")) {
                    int diff = oldText.length() - newText.length();

                    for (int i = 0; i < diff; i++) {
                        sendBackspace();
                    }

                    oldText = newText;

                    return;
                }

                if (difference > 1) {
                    newText.replace(oldText, "");

                    oldText = newText;

                    sendString(newText);

                    return;
                }

                String key = null;

                if (newText.length() > 0) {
                    key = newText.substring(newText.length() - 1);
                }

                if (oldText.length() > newText.length()) {
                    key = "BACKSPACE";
                }

                oldText = newText;

                if (key != null) {
                    if (key.equals("BACKSPACE")) {
                        sendBackspace();
                    } else {
                        if (key.equals(" ")) {
                            key = "%20";
                        }

                        sendString(key);
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void sendBackspace() {
        String url = RokuCmds.getTv();

        KeypressRequest keypressRequest = new KeypressRequest(url, KeypressKeyValues.BACKSPACE.getValue());
        JakuRequest request = new JakuRequest(keypressRequest, null);

        new RequestTask(request, new RokuRequest() {
            @Override
            public void requestResult(RokureqType rokureqType, RequestTask.Result result) {

            }

            @Override
            public void onErrorResponse(RequestTask.Result result) {

            }
        }).execute(RokureqType.keypress);
    }

    private void sendString(String stringLiteral) {
        String url = RokuCmds.getTv();

        KeypressRequest keypressRequest = new KeypressRequest(url, KeypressKeyValues.LIT_.getValue() + stringLiteral);
        JakuRequest request = new JakuRequest(keypressRequest, null);

        new RequestTask(request, new RokuRequest() {
            @Override
            public void requestResult(RokureqType rokureqType, RequestTask.Result result) {
                Log.d("sasas", "OK");
            }

            @Override
            public void onErrorResponse(RequestTask.Result result) {
                Log.d("sasas", "error");
            }
        }).execute(RokureqType.keypress);
        Deque<Integer> stack = new ArrayDeque<Integer>();
    }
}
