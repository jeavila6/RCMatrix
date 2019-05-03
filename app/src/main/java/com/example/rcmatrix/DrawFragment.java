package com.example.rcmatrix;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.util.DisplayMetrics;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

class DrawFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_draw, container, false);

        PaintView paintView = view.findViewById(R.id.paintView);
        Spinner strokeWidthSpinner = view.findViewById(R.id.stroke_width_spinner);
        Button clearButton = view.findViewById(R.id.clear_button);

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

        return view;

    }

}
