package com.example.ledmatrix;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

class ImageTools {

    /**
     * Return Bitmap from URI.
     * @param context application environment
     * @param uri source URI
     * @return result Bitmap
     */
    static Bitmap uriToBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

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
    static String bitmapToRgb(Bitmap bitmap) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        StringBuilder builder = new StringBuilder();
        for (int pixel : pixels) {
            int r = (pixel) >> 16 & 0xff;
            int g = (pixel) >> 8 & 0xff;
            int b = (pixel) & 0xff;
            builder.append("[").append(r).append(",").append(g).append(",").append(b).append("]");
        }
        return builder.toString();
    }

}
