package com.grelp.grelp.fragments;

import android.app.Dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class ErrorDialogFragment extends DialogFragment {
    private Dialog mDialog;

    public ErrorDialogFragment() {
        super();
    }

    public void setDialog(Dialog dialog) {
        mDialog = dialog;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return mDialog;
    }
}