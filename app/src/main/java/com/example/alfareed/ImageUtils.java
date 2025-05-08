package com.example.alfareed;

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;

public class ImageUtils {
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); // Changed to JPEG format
        return stream.toByteArray();
    }
}