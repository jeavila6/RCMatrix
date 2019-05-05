package com.example.rcmatrix;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;

class TextFragment extends Fragment implements BluetoothFragmentInterface {

    private ImageButton mColorImageButton;
    private EditText mInputEditText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_text, container, false);

        mColorImageButton = rootView.findViewById(R.id.color_image_button);
        mInputEditText = rootView.findViewById(R.id.input_edit_text);

        // show color picker on color image button click
        mColorImageButton.setOnClickListener(v -> showColorPicker());

        // set default color for color image button
        mColorImageButton.setBackgroundColor(getResources().getColor(R.color.color_gray_50));

        return rootView;
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

    @Override
    public byte[] getMessage() {
        ByteArrayOutputStream tempStream = new ByteArrayOutputStream();

        // message type
        String messageType = "T";
        byte[] messageTypeBytes = messageType.getBytes();

        // text color
        int textColor = ((ColorDrawable)mColorImageButton.getBackground()).getColor();
        byte[] textColorBytes = ImageTools.colorToRgb(textColor);

        // text
        String text = mInputEditText.getText().toString();
        byte[] textBytes = text.getBytes();

        try {
            tempStream.write(messageTypeBytes);
            tempStream.write(textColorBytes);
            tempStream.write(textBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempStream.toByteArray();
    }
}
