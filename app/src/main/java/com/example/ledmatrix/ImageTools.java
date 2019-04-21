package com.example.ledmatrix;

import android.graphics.Bitmap;

class ImageTools {

    /**
     * Return resized Bitmap from Bitmap.
     * @param bitmap source Bitmap
     * @param width pixel width
     * @param height pixel height
     * @return resized Bitmap
     */
    static Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    /**
     * Return String of RGB values from Bitmap.
     * @param bitmap source Bitmap
     * @return RGB String
     */
    static byte[] bitmapToRgb(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        // RGB values for each pixel, in order
        byte[] bytes = new byte[width * height * 3];

        int index = 0;
        for (int pixel : pixels) {
            bytes[index++] = (byte) (pixel >> 16 & 0xff);
            bytes[index++] = (byte) (pixel >> 8 & 0xff);
            bytes[index++] = (byte) (pixel & 0xff);
        }

        return bytes;
    }

}
