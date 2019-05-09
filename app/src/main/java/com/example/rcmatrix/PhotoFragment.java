package com.example.rcmatrix;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

class PhotoFragment extends Fragment implements BluetoothFragmentInterface {

    // request codes
    private final int REQ_CODE_PICK = 1;
    private final int REQ_IMAGE_CAPTURE = 2;

    private ImageView mPhotoImageView;
    private Uri mCapturedImageUri;

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

        // start ACTION_IMAGE_CAPTURE intent on camera button click
        Button cameraButton = rootView.findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(view -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // for storing image capture
            mCapturedImageUri = Objects.requireNonNull(getActivity()).getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageUri);

            // check availability of camera
            boolean hasCameraFeature = Objects.requireNonNull(getActivity()).getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);

            if (hasCameraFeature)
                startActivityForResult(takePictureIntent, REQ_IMAGE_CAPTURE);
            else
                Toast.makeText(getContext(), R.string.no_camera, Toast.LENGTH_SHORT).show();
        });

        // set image view placeholder image
        mPhotoImageView.setImageDrawable(getResources()
                .getDrawable(R.drawable.image_view_placeholder));

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_PICK && data != null) {
            Uri selectedImage = data.getData();

            // display selected photo in image view
            mPhotoImageView.setImageURI(selectedImage);
        } else if (requestCode == REQ_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            // display captured image in image view
            mPhotoImageView.setImageURI(mCapturedImageUri);
        }
    }

    @Override
    public byte[] getMessage() {
        ByteArrayOutputStream tempStream = new ByteArrayOutputStream();

        // message type
        String messageType = "I";
        byte[] messageTypeBytes = messageType.getBytes();

        // crop and resize image
        Bitmap bitmap = ((BitmapDrawable) mPhotoImageView.getDrawable()).getBitmap();
        bitmap = ImageTools.cropSquareBitmap(bitmap);
        bitmap = ImageTools.resizeBitmap(bitmap);

        // get RGB values
        byte[] imageBytes = ImageTools.bitmapToRgb(bitmap);

        try {
            tempStream.write(messageTypeBytes);
            tempStream.write(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempStream.toByteArray();
    }
}