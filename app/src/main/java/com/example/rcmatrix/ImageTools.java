package com.example.rcmatrix;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

class ImageTools {

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
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

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
        byte[] rgb = new byte[3];
        rgb[0] = (byte) (color >> 16 & 0xff);
        rgb[1] = (byte) (color >> 8 & 0xff);
        rgb[2] = (byte) (color & 0xff);
        return rgb;
    }
}