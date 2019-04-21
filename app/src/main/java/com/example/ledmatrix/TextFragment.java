package com.example.ledmatrix;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class TextFragment extends Fragment {

    EditText mInputEditText;
    EditText mColorEditText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        mInputEditText = view.findViewById(R.id.input_edit_text);
        mColorEditText = view.findViewById(R.id.color_edit_text);
        return view;
    }

    public byte[] getMessage() {
        String message = mColorEditText.getText().toString(); // text color
        message += mInputEditText.getText().toString(); // text
        return message.getBytes();
    }

}
