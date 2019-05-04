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

class TextFragment extends Fragment {

    private ImageButton mColorImageButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);

        mColorImageButton = view.findViewById(R.id.color_image_button);
        mColorImageButton.setOnClickListener(v -> showColorPicker());

        // set color picker to default color
        mColorImageButton.setBackgroundColor(getResources().getColor(R.color.color_gray_50));


        return view;
    }

    private void showColorPicker() {
        ColorPicker colorPicker = new ColorPicker(Objects.requireNonNull(getActivity()));
        colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
            @Override
            public void setOnFastChooseColorListener(int position, int color) {
                mColorImageButton.setBackgroundColor(color);
            }

            @Override
            public void onCancel(){
            }
        })
                .setTitle(getResources().getString(R.string.color_picker_title))
                .setColors(getResources().getIntArray(R.array.color_picker_colors))
                .setRoundColorButton(true)
                .show();
    }

    /*
    public byte[] getMessage() {
        String message = "empty";
        return message.getBytes();
    }
    */
}
