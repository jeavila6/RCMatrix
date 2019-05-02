package com.example.rcmatrix;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class PhotoFragment extends Fragment {

    // request codes
    private final int REQ_CODE_PICK = 1;

    ImageView mPhotoImageView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo, container, false);

        mPhotoImageView = rootView.findViewById(R.id.photo_image_view);

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
        if (requestCode == REQ_CODE_PICK) {
            Uri selectedImage = data.getData();

            // display selected photo in image view
            mPhotoImageView.setImageURI(selectedImage);
        }
    }

    public byte[] getMessage() {

        // get image from image view
        Bitmap bitmap = ((BitmapDrawable) mPhotoImageView.getDrawable()).getBitmap();

        // resize and return RGB values for pixels
        bitmap = ImageTools.resizeBitmap(bitmap, 64, 64);
        return ImageTools.bitmapToRgb(bitmap);
    }
}