package com.example.rcmatrix;

import android.graphics.Bitmap;

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
