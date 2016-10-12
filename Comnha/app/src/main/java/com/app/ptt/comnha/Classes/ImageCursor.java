package com.app.ptt.comnha.Classes;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by PTT on 10/12/2016.
 */

public class ImageCursor {
    Context context;

    public ImageCursor(Context context) {
        this.context = context;
    }

    public Cursor allImage() {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String data = MediaStore.MediaColumns.DATA;
        String id = MediaStore.Images.Media._ID;
        String orderBy = MediaStore.Images.Media._ID;
        String[] cols = {data, id};
        cursor = resolver.query(uri, cols, null, null, orderBy);
        return cursor;

    }
}
