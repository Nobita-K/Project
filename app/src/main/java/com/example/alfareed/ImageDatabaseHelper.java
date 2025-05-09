package com.example.alfareed;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class ImageDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "images.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_IMAGES = "images";

    private static final String COLUMN_ID = "image_id";
    private static final String COLUMN_IMAGE = "image_data";

    // Size limits
    private static final int MAX_IMAGE_SIZE_BYTES = 1024 * 512; // 512 KB

    public ImageDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_IMAGES + "("
                + COLUMN_ID + " TEXT PRIMARY KEY, "
                + COLUMN_IMAGE + " BLOB"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        onCreate(db);
    }

    // Use this to insert a Bitmap safely, will compress and downscale if needed
    public boolean insertImage(String imageId, Bitmap bitmap) {
        byte[] imageData = compressBitmapForDatabase(bitmap);
        if (imageData == null) return false; // Too big or failed

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, imageId);
        values.put(COLUMN_IMAGE, imageData);
        long result = db.insert(TABLE_IMAGES, null, values);
        db.close();
        return result != -1;
    }

    // If you must keep the old insertImage(byte[]), check the size!
    public boolean insertImage(String imageId, byte[] imageData) {
        if (imageData == null || imageData.length > MAX_IMAGE_SIZE_BYTES) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, imageId);
        values.put(COLUMN_IMAGE, imageData);
        long result = db.insert(TABLE_IMAGES, null, values);
        db.close();
        return result != -1;
    }

    // AdminItems Screen
    public byte[] getImage(String imageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        byte[] imageData = null;
        Cursor cursor = db.query(
                TABLE_IMAGES,
                new String[]{COLUMN_IMAGE},
                COLUMN_ID + "=?",
                new String[]{imageId},
                null, null, null
        );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                imageData = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));
            }
            cursor.close();
        }
        db.close();
        return imageData;
    }
    // Menu Screen

    public Bitmap getImageById(String imageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        byte[] imageData = null;
        Cursor cursor = db.query(
                TABLE_IMAGES,
                new String[]{COLUMN_IMAGE},
                COLUMN_ID + "=?",
                new String[]{imageId},
                null, null, null
        );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                imageData = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));
            }
            cursor.close();
        }
        db.close();

        if (imageData != null) {
            return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        } else {
            return null;
        }
    }

    // Helper: compress and resize bitmap so it fits inside MAX_IMAGE_SIZE_BYTES
    private byte[] compressBitmapForDatabase(Bitmap bitmap) {
        if (bitmap == null) return null;

        int quality = 85; // start with high quality
        int maxDimension = 800; // max width or height in px
        Bitmap scaled = bitmap;

        // Scale down if needed
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > maxDimension || height > maxDimension) {
            float scale = Math.min((float)maxDimension / width, (float)maxDimension / height);
            int newWidth = Math.round(width * scale);
            int newHeight = Math.round(height * scale);
            scaled = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        }

        // Try compressing with decreasing quality if needed
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        byte[] data = stream.toByteArray();

        while (data.length > MAX_IMAGE_SIZE_BYTES && quality > 40) {
            stream.reset();
            quality -= 10;
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, stream);
            data = stream.toByteArray();
        }

        if (data.length > MAX_IMAGE_SIZE_BYTES) {
            // Still too big, fail
            return null;
        }

        return data;
    }
}