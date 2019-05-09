package com.example.rcmatrix;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

class AboutDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // inflate layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView =
                inflater.inflate(R.layout.fragment_dialog_about, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        builder.setView(dialogView);

        // set positive button; do nothing on click
        builder.setPositiveButton(R.string.about_dialog_positive_button, (dialog, id) -> {});

        // make links clickable
        TextView aboutTextView = dialogView.findViewById(R.id.about_text_view);
        aboutTextView.setMovementMethod(LinkMovementMethod.getInstance());

        return builder.create();
    }
}