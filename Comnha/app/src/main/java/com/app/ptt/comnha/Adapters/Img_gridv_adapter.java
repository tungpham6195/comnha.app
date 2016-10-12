package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.app.ptt.comnha.R;

/**
 * Created by PTT on 10/12/2016.
 */

public class Img_gridv_adapter extends BaseAdapter {
    LayoutInflater mInflater;
    Cursor cursor;
    boolean thumbnailsselection[];
    Context context;
    int ids[];

    public Img_gridv_adapter(Context context, Cursor cursor, boolean thumbnailsselection[], int ids[]) {
        this.cursor = cursor;
        this.context = context;
        this.ids = ids;
        this.thumbnailsselection = thumbnailsselection;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_gridv_choosept, null);
            holder.imgThumb = (ImageView) view.findViewById(R.id.item_gridv_img);
            holder.checkBox = (CheckBox) view.findViewById(R.id.item_gridv_chbx);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.checkBox.setId(i);
        holder.imgThumb.setId(i);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox) view;
                int id = cb.getId();
                if (thumbnailsselection[id]) {
                    cb.setChecked(false);
                    thumbnailsselection[id] = false;
                } else {
                    cb.setChecked(true);
                    thumbnailsselection[id] = true;
                }
            }
        });
        holder.imgThumb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int id = holder.checkBox.getId();
                if (thumbnailsselection[id]) {
                    holder.checkBox.setChecked(false);
                    thumbnailsselection[id] = false;
                } else {
                    holder.checkBox.setChecked(true);
                    thumbnailsselection[id] = true;
                }
            }
        });
        holder.imgThumb.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))));
        Log.d("path",cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
//        try {
//            setBitmap(holder.imgThumb, ids[i]);
//        } catch (Throwable e) {
//        }
        return view;
    }

    class ViewHolder {
        ImageView imgThumb;
        CheckBox checkBox;
        int id;
    }

    private void setBitmap(final ImageView iv, final int id) {
        cursor.moveToPosition(id);
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                final int THUMBSIZE = 64;
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))),
                        THUMBSIZE, THUMBSIZE);
                return ThumbImage;
//                return MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
//                        id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                iv.setImageBitmap(result);
            }
        }.execute();
    }
}
