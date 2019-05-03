package com.example.rcmatrix;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;

public class TextFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);

        ImageButton colorImageButton = view.findViewById(R.id.color_image_button);
        colorImageButton.setOnClickListener(v -> showColorPicker());

        return view;
    }

    private void showColorPicker() {
        ColorPicker colorPicker = new ColorPicker(Objects.requireNonNull(getActivity()));
        colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
            @Override
            public void setOnFastChooseColorListener(int position, int color) {
            }

            @Override
            public void onCancel(){
            }
        })
                .setTitle(getResources().getString(R.string.color_picker_title))
                .setRoundColorButton(true)
                .show();
    }

    public byte[] getMessage() {
        String message = "empty";
        return message.getBytes();
    }
}
