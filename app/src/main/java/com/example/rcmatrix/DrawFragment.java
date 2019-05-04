package com.example.rcmatrix;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;

class DrawFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_draw, container, false);

        PaintView paintView = view.findViewById(R.id.paint_view);
        Spinner strokeWidthSpinner = view.findViewById(R.id.stroke_width_spinner);
        Button clearButton = view.findViewById(R.id.clear_button);
        ImageButton fgColorImageButton = view.findViewById(R.id.fg_color_image_button);
        ImageButton bgColorImageButton = view.findViewById(R.id.bg_color_image_button);

        // initialize paint view canvas
        DisplayMetrics metrics = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        paintView.initCanvas(metrics.widthPixels, metrics.heightPixels);

        // set stroke width spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.stroke_width_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        strokeWidthSpinner.setAdapter(adapter);

        // set stroke width on stroke width spinner item selection, taking item position as width
        strokeWidthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                paintView.setStrokeWidth(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        // clear canvas on clear button click
        clearButton.setOnClickListener(v -> paintView.clear());

        // set foreground color and and background color pickers to match
        fgColorImageButton.setBackgroundColor(paintView.getFgColor());
        bgColorImageButton.setBackgroundColor(paintView.getBgColor());

        // set foreground color on color picker selection
        fgColorImageButton.setOnClickListener(v -> {
            ColorPicker colorPicker = new ColorPicker(Objects.requireNonNull(getActivity()));
            colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                @Override
                public void setOnFastChooseColorListener(int position, int color) {
                    fgColorImageButton.setBackgroundColor(color);
                    paintView.setFgColor(color);
                }

                @Override
                public void onCancel(){
                }
            })
                    .setTitle(getResources().getString(R.string.color_picker_title))
                    .setRoundColorButton(true)
                    .show();
        });

        // set background color on color picker selection
        bgColorImageButton.setOnClickListener(v -> {
            ColorPicker colorPicker = new ColorPicker(Objects.requireNonNull(getActivity()));
            colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                @Override
                public void setOnFastChooseColorListener(int position, int color) {
                    bgColorImageButton.setBackgroundColor(color);
                    paintView.setBgColor(color);
                }

                @Override
                public void onCancel(){
                }
            })
                    .setTitle(getResources().getString(R.string.color_picker_title))
                    .setRoundColorButton(true)
                    .show();
        });
        return view;
    }
}