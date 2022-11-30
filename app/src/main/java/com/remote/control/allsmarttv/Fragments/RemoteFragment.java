package com.remote.control.allsmarttv.Fragments;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputContentInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.remote.control.allsmarttv.Activities.RemoteActivity;
import com.remote.control.allsmarttv.Utils.ImeListenerCall;
import com.remote.control.allsmarttv.Utils.OnRemoteListener;
import com.remote.control.allsmarttv.R;
import com.remote.control.allsmarttv.Utils.ImeUtil;
import com.remote.control.allsmarttv.Utils.SpeechUtil;
import com.remote.control.allsmarttv.Utils.android_touchpad;

public class RemoteFragment extends Fragment {

    private boolean keyboardVisible = false;
    private int lastSeenTextLength;
    private Listener listener;
    private int nextPlayPauseKeyCode = 126;
    private OnRemoteListener onRemoteListener = null;
    private SpeechUtil speechOrb;
    private ImeUtil textCapture;
    private View textCaptureRoot;

    private GoogleApiClient client;
    RemoteActivity remoteActivity;
    RelativeLayout cursor_lay, channel_lay, volume_lay;
    LinearLayout num_lay;
    private static final float HINT_TEXT_ALPHA_UNFOCUSED = 0.5f;
    private static final String KEY_NEXT_PLAY_PAUSE_KEYCODE = "next_play_pause_keycode";
    private static final int END_OF_LINE = 99999;
    int toogle = 0, mute_toogle = 0, pad_toogle = 0, pl_tgl = 0;
    private TextView txt_hint;
    private ImeListenerCall imeListenerCall = null;
    private final ImeUtil.Interceptor interceptor = new ImeUtil.Interceptor() {

        public boolean performEditorAction(int i) {
            if (RemoteFragment.this.onRemoteListener == null) {
                return false;
            }
            RemoteFragment.this.onRemoteListener.performEditorAction(i);
            return true;
        }

        public boolean setComposingText(CharSequence charSequence, int i) {
            if (RemoteFragment.this.onRemoteListener == null) {
                return false;
            }
            RemoteFragment.this.onRemoteListener.setComposingText(charSequence, i);
            return true;
        }

        public boolean commitText(CharSequence charSequence, int i) {
            if (RemoteFragment.this.onRemoteListener == null) {
                return false;
            }
            RemoteFragment.this.onRemoteListener.commitText(charSequence, i);
            return true;
        }

        public boolean deleteSurroundingText(int i, int i2) {
            if (RemoteFragment.this.onRemoteListener == null) {
                return false;
            }
            RemoteFragment.this.onRemoteListener.deleteSurroundingText(i, i2);
            return true;
        }

        public boolean deleteSurroundingTextInCodePoints(int i, int i2) {
            Log.w("TAG", "not implemented: deleteSurroundingTextInCodePoints  " + i + " " + i2);
            return false;
        }

        public boolean sendKeyEvent(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == 4 || RemoteFragment.this.onRemoteListener == null) {
                return false;
            }
            RemoteFragment.this.onRemoteListener.sendKeyEvent(keyEvent.getKeyCode(), keyEvent.getAction());
            return true;
        }

        public boolean performContextMenuAction(int i) {
            if (RemoteFragment.this.textCapture == null) {
                return false;
            }
            switch (i) {
                case 16908320:
                case 16908322:
                    setComposingRegion(0, RemoteFragment.END_OF_LINE);
                    setComposingText(RemoteFragment.this.textCapture.getText(), RemoteFragment.END_OF_LINE);
                    finishComposingText();
                    RemoteFragment.this.textCapture.setSelection(RemoteFragment.this.textCapture.getText().length());
                    return true;
                case 16908321:
                default:
                    return false;
            }
        }

        public boolean beginBatchEdit() {
            if (RemoteFragment.this.onRemoteListener == null) {
                return false;
            }
            RemoteFragment.this.onRemoteListener.beginBatchEdit();
            return true;
        }

        public CharSequence getTextBeforeCursor(int i, int i2) {
            if (RemoteFragment.this.onRemoteListener != null) {
                return RemoteFragment.this.onRemoteListener.getTextBeforeCursor(i, i2);
            }
            return null;
        }

        public CharSequence getTextAfterCursor(int i, int i2) {
            if (RemoteFragment.this.onRemoteListener != null) {
                return RemoteFragment.this.onRemoteListener.getTextAfterCursor(i, i2);
            }
            return null;
        }

        public CharSequence getSelectedText(int i) {
            if (RemoteFragment.this.onRemoteListener != null) {
                return RemoteFragment.this.onRemoteListener.getSelectedText(i);
            }
            return null;
        }

        public int getCursorCapsMode(int i) {
            if (RemoteFragment.this.onRemoteListener != null) {
                return RemoteFragment.this.onRemoteListener.getCursorCapsMode(i);
            }
            return 0;
        }

        public ExtractedText getExtractedText(ExtractedTextRequest extractedTextRequest, int i) {
            if (RemoteFragment.this.onRemoteListener != null) {
                return RemoteFragment.this.onRemoteListener.getExtractedText(extractedTextRequest, i);
            }
            return null;
        }

        public boolean setComposingRegion(int i, int i2) {
            if (RemoteFragment.this.onRemoteListener == null) {
                return false;
            }
            RemoteFragment.this.onRemoteListener.setComposingRegion(i, i2);
            return true;
        }

        public boolean finishComposingText() {
            if (RemoteFragment.this.onRemoteListener == null) {
                return false;
            }
            RemoteFragment.this.onRemoteListener.finishComposingText();
            return true;
        }

        public boolean commitCompletion(CompletionInfo completionInfo) {
            if (RemoteFragment.this.onRemoteListener == null) {
                return false;
            }
            RemoteFragment.this.onRemoteListener.commitCompletion(completionInfo);
            return true;
        }

        public boolean commitCorrection(CorrectionInfo correctionInfo) {
            return false;
        }

        public boolean setSelection(int i, int i2) {
            if (RemoteFragment.this.onRemoteListener == null) {
                return false;
            }
            RemoteFragment.this.onRemoteListener.setSelection(i, i2);
            return true;
        }

        public boolean endBatchEdit() {
            if (RemoteFragment.this.onRemoteListener == null) {
                return false;
            }
            RemoteFragment.this.onRemoteListener.endBatchEdit();
            return true;
        }

        public boolean clearMetaKeyStates(int i) {
            return false;
        }

        public boolean reportFullscreenMode(boolean z) {
            return false;
        }

        public boolean performPrivateCommand(String str, Bundle bundle) {
            return false;
        }

        public boolean requestCursorUpdates(int i) {
            if (RemoteFragment.this.onRemoteListener == null) {
                return false;
            }
            RemoteFragment.this.onRemoteListener.requestCursorUpdates(i);
            return true;
        }

        public Handler getHandler() {
            return null;
        }

        public void closeConnection() {
        }

        @Override
        public boolean commitContent(@NonNull InputContentInfo inputContentInfo, int flags, @Nullable Bundle opts) {
            return false;
        }
    };

    private android_touchpad touchpad;
    private android_touchpad.Listener touchpadListener = new android_touchpad.Listener() {

        @Override
        public void onTrackpadEvent(int i) {
            if (RemoteFragment.this.onRemoteListener != null) {
                RemoteFragment.this.onRemoteListener.sendKeyEvent(i, 0);
                RemoteFragment.this.onRemoteListener.sendKeyEvent(i, 1);
            }
        }

        @Override
        public void onLongPressCenterDown() {
            if (RemoteFragment.this.onRemoteListener != null) {
                RemoteFragment.this.onRemoteListener.sendKeyEvent(23, 0);
            }
        }

        @Override
        public void onLongPressCenterUp() {
            if (RemoteFragment.this.onRemoteListener != null) {
                RemoteFragment.this.onRemoteListener.sendKeyEvent(23, 1);
            }
        }
    };

    public interface Listener {
        void onMicPermissionDenied();

        void onStoreExtractedText(ExtractedText extractedText);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.remoteActivity = (RemoteActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment can only be added to CoreRemoteActivity");
        }

        if (!(activity instanceof Listener)) {
            throw new ClassCastException(activity.toString() + " must implement " + "onStoreExtractedText().");
        } else if (!(activity instanceof ImeListenerCall)) {
            throw new ClassCastException(activity.toString() + " must implement ImeInfoInterface.");
        } else {
            this.listener = (Listener) activity;
            this.imeListenerCall = (ImeListenerCall) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.remote_frag, viewGroup, false);

        this.touchpad = (android_touchpad) view.findViewById(R.id.android_touchpad);
        this.touchpad.setListener(this.touchpadListener);

        client = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();

        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button0, button_enter;

        ImageButton button_down, button_mute, button_chup, button_chdown, button_disconnect,
                button_volup, button_voldown, button_up, button_right, button_left;

        ImageView button_prev, button_rew, button_play, button_for, button_next, button_home, button_num, button_touch, button_cast, button_back, button_turnOff;

        cursor_lay = view.findViewById(R.id.cursor_lay);
        num_lay = view.findViewById(R.id.number_lay);
        channel_lay = view.findViewById(R.id.channel_lay);
        volume_lay = view.findViewById(R.id.volume_lay);

        button_disconnect = view.findViewById(R.id.button_disconnect);
        button_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                remoteActivity.generateConnectionFragment(false);

            }
        });

        button_num = view.findViewById(R.id.button_numpad);
        button_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (toogle == 0) {
                    vibrator.vibrate(10);

                    volume_lay.setVisibility(View.VISIBLE);
                    channel_lay.setVisibility(View.VISIBLE);
                    pad_toogle = 0;
                    toogle = 1;
                    cursor_lay.setVisibility(View.INVISIBLE);
                    num_lay.setVisibility(View.VISIBLE);
                    touchpad.setVisibility(View.INVISIBLE);
                    touchpad.reset();
                } else {
                    vibrator.vibrate(10);

                    volume_lay.setVisibility(View.VISIBLE);
                    channel_lay.setVisibility(View.VISIBLE);
                    cursor_lay.setVisibility(View.VISIBLE);
                    num_lay.setVisibility(View.INVISIBLE);
                    touchpad.setVisibility(View.INVISIBLE);
                    touchpad.reset();
                    pad_toogle = 0;
                    toogle = 0;
                }
            }
        });


        button_back = view.findViewById(R.id.btn_back);
        button_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 4);

                return false;
            }
        });

        button_turnOff = view.findViewById(R.id.button_turnOff);
        button_turnOff.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 26);

                return false;
            }
        });


        button_mute = view.findViewById(R.id.button_mute);
        button_mute.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 164);

                return false;
            }
        });

        button_chup = view.findViewById(R.id.button_chup);
        button_chup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 166);

                return false;
            }
        });

        button_chdown = view.findViewById(R.id.button_chdown);
        button_chdown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 167);

                return false;
            }
        });

        button_volup = view.findViewById(R.id.button_volup);
        button_volup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 24);


                return false;
            }
        });


        button_voldown = view.findViewById(R.id.button_voldown);
        button_voldown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 25);

                return false;
            }
        });

        button_up = view.findViewById(R.id.button_cursorup);
        button_up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 19);

                return false;
            }
        });

        button_left = view.findViewById(R.id.button_cursorleft);
        button_left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 21);

                return false;
            }
        });


        button_right = view.findViewById(R.id.button_cursorright);
        button_right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 22);


                return false;
            }
        });

        button_cast = view.findViewById(R.id.button_cast);
        button_cast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    startActivity(new Intent("android.settings.CAST_SETTINGS"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        button_touch = view.findViewById(R.id.button_touchpad);
        button_touch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pad_toogle == 0) {

                    touchpad.setVisibility(View.VISIBLE);
                    touchpad.reset();
                    channel_lay.setVisibility(View.INVISIBLE);
                    cursor_lay.setVisibility(View.INVISIBLE);
                    num_lay.setVisibility(View.INVISIBLE);
                    pad_toogle = 1;
                    volume_lay.setVisibility(View.INVISIBLE);
                } else {
                    volume_lay.setVisibility(View.VISIBLE);
                    channel_lay.setVisibility(View.VISIBLE);
                    cursor_lay.setVisibility(View.VISIBLE);
                    num_lay.setVisibility(View.INVISIBLE);
                    pad_toogle = 0;
                    touchpad.setVisibility(View.INVISIBLE);
                    touchpad.reset();
                }
            }
        });


        button0 = view.findViewById(R.id.button0);
        button0.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 7);

                return false;
            }
        });

        button1 = view.findViewById(R.id.button1);
        button1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 8);

                return false;
            }
        });

        button2 = view.findViewById(R.id.button2);
        button2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 9);

                return false;
            }
        });

        button3 = view.findViewById(R.id.button3);
        button3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 10);

                return false;
            }
        });

        button4 = view.findViewById(R.id.button4);
        button4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 11);

                return false;
            }
        });


        button5 = view.findViewById(R.id.button5);
        button5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 12);

                return false;
            }
        });


        button6 = view.findViewById(R.id.button6);
        button6.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 13);

                return false;
            }
        });


        button7 = view.findViewById(R.id.button7);
        button7.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 14);

                return false;
            }
        });


        button8 = view.findViewById(R.id.button8);
        button8.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 15);

                return false;
            }
        });


        button9 = view.findViewById(R.id.button9);
        button9.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 16);

                return false;
            }
        });

        button_down = view.findViewById(R.id.button_cursordown);
        button_down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 20);

                return false;
            }
        });


        button_enter = view.findViewById(R.id.button_cursorok);
        button_enter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 23);

                return false;
            }
        });

        button_home = view.findViewById(R.id.button_home);
        button_home.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 3);

                return false;
            }
        });

        button_prev = view.findViewById(R.id.button_previous);
        button_prev.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 88);

                return false;
            }
        });

        button_rew = view.findViewById(R.id.button_rewind);
        button_rew.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 89);

                return false;
            }
        });


        button_for = view.findViewById(R.id.button_forward);
        button_for.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 90);

                return false;
            }
        });

        button_next = view.findViewById(R.id.button_next);
        button_next.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                vibrator.vibrate(10);
                doFunc(v, event, 87);

                return false;
            }
        });


        button_play = view.findViewById(R.id.button_play);
        button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pl_tgl == 0) {
                    button_play.setImageResource(R.drawable.pause);
                    button_play.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            vibrator.vibrate(10);
                            doFunc(v, event, 126);

                            return false;
                        }
                    });
                    pl_tgl = 1;
                } else {
                    button_play.setImageResource(R.drawable.play);
                    button_play.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            vibrator.vibrate(10);
                            doFunc(v, event, 127);

                            return false;
                        }
                    });
                    pl_tgl = 0;
                }
            }
        });

        this.speechOrb = (SpeechUtil) view.findViewById(R.id.button_mic);
        if (isRecording()) {
            showVoiceAnimation();
        }

        speechOrb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RemoteFragment.this.toggleSpeech();

            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.nextPlayPauseKeyCode = bundle.getInt(KEY_NEXT_PLAY_PAUSE_KEYCODE, 126);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        toogle = 0;
        pad_toogle = 0;
        touchpad.setVisibility(View.INVISIBLE);
        touchpad.reset();
        channel_lay.setVisibility(View.VISIBLE);
        volume_lay.setVisibility(View.VISIBLE);
        cursor_lay.setVisibility(View.VISIBLE);
        num_lay.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onPause() {
        super.onPause();
        this.touchpad.reset();
    }

    @Override
    public void onDetach() {
        hideIme();
        this.listener = null;
        this.imeListenerCall = null;
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(KEY_NEXT_PLAY_PAUSE_KEYCODE, this.nextPlayPauseKeyCode);
    }

    public void attachRemoteInterface(OnRemoteListener onRemoteListener) {
        this.onRemoteListener = onRemoteListener;
    }

    public void toggleKeyboard() {
        setKeyboardVisible(!this.keyboardVisible);
    }

    public void showIme() {
        if (getActivity() != null && isVisible()) {
            setKeyboardVisible(true);
        }
    }

    public void hideIme() {
        setKeyboardVisible(false);
    }

    @SuppressLint("ResourceType")
    private void setKeyboardVisible(boolean z) {
        this.keyboardVisible = z;
        if (this.keyboardVisible) {
            this.textCapture = getInterceptor();
            ExtractedText imeExtractedText = this.imeListenerCall.getImeExtracted();
            EditorInfo imeEditorInfo = this.imeListenerCall.getImeEditor();
            this.textCapture.setEditorInfo(imeEditorInfo);
            if (imeExtractedText != null) {
                this.textCapture.setText(imeExtractedText.text);
                this.textCapture.setSelection(imeExtractedText.selectionStart, imeExtractedText.selectionEnd);
            }
            this.textCapture.showKeyboard();
            if (imeEditorInfo != null) {
                this.txt_hint.setText(imeEditorInfo.hintText);
                if (TextUtils.isEmpty(imeEditorInfo.hintText)) {
                    this.txt_hint.setVisibility(View.GONE);
                } else {
                    this.txt_hint.setVisibility(View.VISIBLE);
                }
            } else {
                this.txt_hint.setText("");
                this.txt_hint.setVisibility(View.GONE);
            }
        } else if (this.textCapture != null) {
            this.textCapture.hideKeyboard();
            this.textCapture.setEditorInfo(null);
            ExtractedText extractedText = new ExtractedText();
            extractedText.text = this.textCapture.getText();
            extractedText.selectionStart = this.textCapture.getSelectionStart();
            extractedText.selectionEnd = this.textCapture.getSelectionEnd();
            if (this.listener != null) {
                this.listener.onStoreExtractedText(extractedText);
            }
            final View view = this.textCaptureRoot;
            new Handler().postDelayed(new Runnable() {

                public void run() {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
            }, (long) view.getContext().getResources().getInteger(17694720));
            this.textCaptureRoot.setAlpha(0.0f);
            this.textCapture = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    public boolean isRecording() {
        if (this.onRemoteListener != null) {
            return this.onRemoteListener.isRecording();
        }
        return false;
    }

    private void startVoice() {
        if (!isMicAllowed()) {
            micPermission();
        } else if (this.onRemoteListener != null) {
            this.onRemoteListener.startVoice();
        }
    }

    public void showVoiceAnimation() {
        this.speechOrb.showListening();
    }

    public void stopVoice() {
        if (this.onRemoteListener != null) {
            this.onRemoteListener.stopVoice();
        }
    }

    public void stopVoiceSearchAnimation() {
        this.speechOrb.showNotListening();
    }

    public void setSoundLevel(int i) {
        this.speechOrb.setSoundLevel(i);
    }

    public void updateCompletionInfo(CompletionInfo[] completionInfoArr) {
        if (this.textCapture != null) {
            this.textCapture.updateCompletionInfo(completionInfoArr);
        }
    }

    @TargetApi(19)
    private void addTextColorAnimation(ViewPropertyAnimator viewPropertyAnimator, TextView textView, int i) {
        if (Build.VERSION.SDK_INT >= 19) {
            final int i2 = (i & 16711680) >> 16;
            final int i3 = (i & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
            final int i4 = i & 255;
            final int currentTextColor = (textView.getCurrentTextColor() & 16711680) >> 16;
            final int currentTextColor2 = (textView.getCurrentTextColor() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
            final int currentTextColor3 = textView.getCurrentTextColor() & 255;
            viewPropertyAnimator.setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float animatedFraction = valueAnimator.getAnimatedFraction();
                    RemoteFragment.this.txt_hint.setTextColor((((int) (((1.0f - animatedFraction) * ((float) currentTextColor3)) + (((float) i4) * animatedFraction))) & 255) + ((((int) ((((float) i2) * animatedFraction) + (((float) currentTextColor) * (1.0f - animatedFraction)))) & 255) << 16) + ViewCompat.MEASURED_STATE_MASK + ((((int) ((((float) i3) * animatedFraction) + (((float) currentTextColor2) * (1.0f - animatedFraction)))) & 255) << 8));
                }
            });
            return;
        }
        textView.setTextColor(i);
    }

    @SuppressLint("ResourceType")
    @TargetApi(21)
    private Interpolator getInterpolator() {
        if (Build.VERSION.SDK_INT >= 21) {
            return AnimationUtils.loadInterpolator(getActivity(), 17563663);
        }
        return null;
    }


    public void sendKeyPress(int i) {
        if (this.onRemoteListener != null) {
            this.onRemoteListener.sendKeyEvent(i, 0);
            this.onRemoteListener.sendKeyEvent(i, 1);
        }
    }

    public void toggleSpeech() {
        if (this.onRemoteListener == null) {
            return;
        }
        if (this.onRemoteListener.isRecording()) {
            stopVoice();
        } else {
            startVoice();
        }
    }


    public int getPlayPauseKeyCode() {
        int i = this.nextPlayPauseKeyCode;
        if (this.nextPlayPauseKeyCode == 126) {
            this.nextPlayPauseKeyCode = 127;
        } else {
            this.nextPlayPauseKeyCode = 126;
        }
        return i;
    }

    private int getPlayPauseKeyCode(int i) {
        int i2 = this.nextPlayPauseKeyCode;
        if (i == 1) {
            if (this.nextPlayPauseKeyCode == 126) {
                this.nextPlayPauseKeyCode = 127;
            } else {
                this.nextPlayPauseKeyCode = 126;
            }
        }
        return i2;
    }

    private boolean isMicAllowed() {
        return ContextCompat.checkSelfPermission(getActivity(), "android.permission.RECORD_AUDIO") == 0;
    }

    private void micPermission() {
        requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i != 1) {
            return;
        }
        if (iArr.length <= 0 || iArr[0] != 0) {
            Log.w("TAG", "Microphone permission denied.");
            if (this.listener != null) {
                this.listener.onMicPermissionDenied();
                return;
            }
            return;
        }
        startVoice();
    }

    public void doFunc(View view, MotionEvent motionEvent, int key) {

        if (motionEvent.getAction() == 1) {
        }
        if (motionEvent.getAction() == 0) {
            if (RemoteFragment.this.onRemoteListener != null) {
                RemoteFragment.this.onRemoteListener.sendKeyEvent(key, 0);
            }
            view.performHapticFeedback(1);
        } else if (motionEvent.getAction() == 1 && RemoteFragment.this.onRemoteListener != null) {
            RemoteFragment.this.onRemoteListener.sendKeyEvent(key, 1);
        }
    }


    private ImeUtil getInterceptor() {
        if (this.textCapture == null) {
            LayoutInflater from = LayoutInflater.from(getActivity());
            ViewGroup viewGroup = (ViewGroup) getActivity().getWindow().getDecorView().findViewById(R.id.content_parent);
            this.textCaptureRoot = from.inflate(R.layout.ime_lay, viewGroup, false);
            this.textCaptureRoot.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    RemoteFragment.this.hideIme();
                }
            });
            this.textCapture = (ImeUtil) this.textCaptureRoot.findViewById(R.id.ime);
            this.textCapture.setInterceptor(this.interceptor);
            int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.ime_editor_hint_text_size);
            int dimensionPixelSize2 = getResources().getDimensionPixelSize(R.dimen.ime_editor_text_size);
            int dimensionPixelSize3 = getResources().getDimensionPixelSize(R.dimen.ime_editor_text_vertical_margin);
            @SuppressLint("ResourceType") final int integer = getResources().getInteger(17694720);
            final int i = dimensionPixelSize3 + dimensionPixelSize2;
            final float f = ((float) dimensionPixelSize2) / ((float) dimensionPixelSize);
            final int color = getResources().getColor(R.color.ime_unfocused);
            final int color2 = getResources().getColor(R.color.ime_color);
            final Interpolator interpolator = getInterpolator();
            this.txt_hint = (TextView) this.textCaptureRoot.findViewById(R.id.text_hint);
            this.txt_hint.setPivotX(0.0f);
            this.txt_hint.setPivotY(0.0f);
            this.txt_hint.setAlpha(HINT_TEXT_ALPHA_UNFOCUSED);
            this.txt_hint.setScaleX(f);
            this.txt_hint.setScaleY(f);
            this.txt_hint.setTranslationY((float) i);
            this.lastSeenTextLength = 0;
            this.textCapture.addTextChangedListener(new TextWatcher() {

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    if (editable.length() == 0 && RemoteFragment.this.lastSeenTextLength != 0) {
                        RemoteFragment.this.txt_hint.clearAnimation();
                        ViewPropertyAnimator duration = RemoteFragment.this.txt_hint.animate().translationY((float) i).scaleX(f).scaleY(f).alpha(RemoteFragment.HINT_TEXT_ALPHA_UNFOCUSED).setDuration((long) integer);
                        if (interpolator != null) {
                            duration.setInterpolator(interpolator);
                        }
                        RemoteFragment.this.addTextColorAnimation(duration, RemoteFragment.this.txt_hint, color);
                    } else if (editable.length() != 0 && RemoteFragment.this.lastSeenTextLength == 0) {
                        RemoteFragment.this.txt_hint.clearAnimation();
                        ViewPropertyAnimator duration2 = RemoteFragment.this.txt_hint.animate().translationY(0.0f).scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration((long) integer);
                        if (interpolator != null) {
                            duration2.setInterpolator(interpolator);
                        }
                        RemoteFragment.this.addTextColorAnimation(duration2, RemoteFragment.this.txt_hint, color2);
                    }
                    RemoteFragment.this.lastSeenTextLength = editable.length();
                }
            });
            viewGroup.addView(this.textCaptureRoot);
        }
        return this.textCapture;
    }
}
