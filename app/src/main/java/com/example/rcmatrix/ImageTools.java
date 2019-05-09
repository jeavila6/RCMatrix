package com.example.rcmatrix;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

class ImageTools {

    /**
     * Return center cropped Bitmap from Bitmap.
     * @param bitmap source Bitmap
     * @return center cropped Bitmap
     */
    static Bitmap cropSquareBitmap(Bitmap bitmap) {
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();

        int newWidth = Math.min(width, height);
        int newHeight = (height > width) ? height - (height - width) : height;

        int cropWidth = (width - height) / 2;
        cropWidth = (cropWidth < 0) ? 0: cropWidth;
        int cropHeight = (height - width) / 2;
        cropHeight = (cropHeight < 0) ? 0: cropHeight;

        return Bitmap.createBitmap(bitmap, cropWidth, cropHeight, newWidth, newHeight);
    }

    /**
     * Return resized 64x64 Bitmap from Bitmap.
     * @param bitmap source Bitmap
     * @return resized Bitmap
     */
    static Bitmap resizeBitmap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, 64, 64, true);
    }

    /**
     * Return byte array of RGB values from Bitmap.
     * @param bitmap source Bitmap
     * @return RGB byte array
     */
    static byte[] bitmapToRgb(Bitmap bitmap) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int pixel : pixels) {
            try {
                outputStream.write(colorToRgb(pixel));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return outputStream.toByteArray();
    }

    /**
     * Return byte array of RGB values from color.
     * @param color source color
     * @return RGB byte array
     */
    static byte[] colorToRgb(int color) {
        return new byte[]{
                (byte) (color >> 16 & 0xff),
                (byte) (color >> 8 & 0xff),
                (byte) (color & 0xff)
        };
    }
}