package com.remote.control.allsmarttv.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.remote.control.allsmarttv.R;

public class PairingFragment extends Fragment {
    private Button btn_cancel;
    private Listener listener;
    private Button btn_pair;
    private boolean pairingCompleted;
    private View pair_view;
    private EditText edt_pin;

    public interface Listener {
        void onPairingCancelled();

        void onPairingCompleted(String str);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Listener) {
            this.listener = (Listener) activity;
            return;
        }
        throw new ClassCastException(activity.toString() + " must implement OnPairingListener");
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {

        this.pair_view = layoutInflater.inflate(R.layout.pairing_fragment, viewGroup, false);
        this.edt_pin = (EditText) this.pair_view.findViewById(R.id.txt_pair);
        this.edt_pin.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View view, boolean z) {
                if (!z) {
                    PairingFragment.this.cancel();
                }
            }
        });

        this.btn_cancel = (Button) this.pair_view.findViewById(R.id.btn_pCancel);
        this.btn_cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                        PairingFragment.this.hide();
                        PairingFragment.this.listener.onPairingCancelled();
            }
        });

        this.btn_pair = (Button) this.pair_view.findViewById(R.id.pair_view);
        this.btn_pair.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                    PairingFragment.this.pairingCompleted = true;
                    PairingFragment.this.hide();
                    PairingFragment.this.btn_pair.setEnabled(false);
                    PairingFragment.this.edt_pin.setEnabled(false);
                    PairingFragment.this.listener.onPairingCompleted(PairingFragment.this.edt_pin.getText().toString());

            }
        });
        return this.pair_view;
    }

    private void cancel() {
        if (!this.pairingCompleted) {
            this.listener.onPairingCancelled();
        }
        hide();
    }


    private void hide() {
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.edt_pin.getWindowToken(), 0);
    }

    private void show() {
        this.edt_pin.requestFocus();
        this.edt_pin.setSelection(this.edt_pin.getText().length());
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(this.edt_pin, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.pairingCompleted = false;
        this.edt_pin.setText("");
        show();
    }
}
