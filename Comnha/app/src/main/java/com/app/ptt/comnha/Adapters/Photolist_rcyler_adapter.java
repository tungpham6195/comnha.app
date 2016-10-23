package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.app.ptt.comnha.R;

/**
 * Created by PTT on 10/23/2016.
 */

public class Photolist_rcyler_adapter extends RecyclerView.Adapter<Photolist_rcyler_adapter.ViewHolder> {
    private Cursor mMediaStoreCursor;
    private final Activity mActivity;

    public Photolist_rcyler_adapter(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_rcyler_choosept, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap bitmap = getBitmapFromMediaStore(position);
        if (bitmap != null) {
            holder.getImageView().setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return (mMediaStoreCursor == null) ? 0 : mMediaStoreCursor.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_rcyler_chosept_imgV);
            checkBox = (CheckBox) itemView.findViewById(R.id.item_rcyler_chosept_chbox);
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    private Cursor swapCursor(Cursor cursor) {
        if (mMediaStoreCursor == cursor) {
            return null;
        }
        Cursor oldCursor = mMediaStoreCursor;
        this.mMediaStoreCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    public void changeCursor(Cursor cursor) {
        Cursor oldCursor = swapCursor(cursor);
        if (oldCursor != null) {
            oldCursor.close();
        }
    }

    private Bitmap getBitmapFromMediaStore(int position) {
        int idIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
        int mediaTypeIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
        mMediaStoreCursor.moveToPosition(position);
        Bitmap bitmap = null;
//        try {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        bitmap = BitmapFactory.decodeFile(
                mMediaStoreCursor.getString(mMediaStoreCursor.getColumnIndex(MediaStore.Images.Media.DATA)), options);

//                    bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(),
//                            Uri.parse(mMediaStoreCursor.getString(mMediaStoreCursor.getColumnIndex(MediaStore.Images.Media.DATA))));
//        } catch (IOException mess) {
//
//        }

        return bitmap;
//        switch (mMediaStoreCursor.getInt(mediaTypeIndex)) {
//            case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
//                return MediaStore.Images.Thumbnails.getThumbnail(mActivity.getContentResolver(),
//                        mMediaStoreCursor.getString());
//            default:
//                return null;
//        }
    }
}
