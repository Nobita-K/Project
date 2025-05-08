package com.example.alfareed;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

public class ImageDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "images.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_IMAGES = "images";

    private static final String COLUMN_ID = "image_id";
    private static final String COLUMN_IMAGE = "image_data";

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

    public boolean insertImage(String imageId, byte[] imageData) {
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
            return android.graphics.BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        } else {
            return null;
        }
    }

}