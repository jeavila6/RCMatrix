package com.example.ledmatrix;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import static android.app.Activity.RESULT_OK;

public class PhotoFragment extends Fragment {

    // request codes for starting activities
    private final int REQ_CODE_PICK = 0;

    ImageView photoImageView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo, container, false);

        photoImageView = rootView.findViewById(R.id.photo_image_view);

        // start ACTION_PICK intent on pick button click
        Button browseButton = rootView.findViewById(R.id.browse_button);
        browseButton.setOnClickListener(view -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK);

            // filter jpeg, png, gif, etc.
            pickIntent.setType("image/*");

            startActivityForResult(pickIntent, REQ_CODE_PICK);
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        switch(requestCode) {
            case REQ_CODE_PICK:
                Uri selectedImage = data.getData();

                // display selected photo in image view
                photoImageView.setImageURI(selectedImage);
        }
    }
}