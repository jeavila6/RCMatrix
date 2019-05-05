package com.example.rcmatrix;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

class AboutDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // inflate layout for dialog, passing null as parent view since its used in dialog layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView =
                inflater.inflate(R.layout.fragment_dialog_about, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        // set constructed layout for dialog
        builder.setView(dialogView);

        // set positive button for dialog
        builder.setPositiveButton(R.string.about_dialog_positive_button, (dialog, id) -> {});

        return builder.create();
    }
}