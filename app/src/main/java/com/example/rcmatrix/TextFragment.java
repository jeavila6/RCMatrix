package com.example.rcmatrix;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

        ColorPicker colorPicker = new ColorPicker(getActivity());
        colorPicker.show();

        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position, int color) {
                // ...
            }

            @Override
            public void onCancel(){
                // ...
            }
        });
    }

    public byte[] getMessage() {
        String message = "empty";
        return message.getBytes();
    }
}
