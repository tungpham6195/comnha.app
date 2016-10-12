package com.app.ptt.comnha;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.app.ptt.comnha.Adapters.Img_gridv_adapter;
import com.app.ptt.comnha.Classes.ImageCursor;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChoosePhotoFragment extends Fragment {
    GridView gridView;
    Button btn_select;
    String[] arrPath;
    boolean[] thumbselecttion;
    int ids[];
    int count;
    Img_gridv_adapter img_adapter;

    public ChoosePhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_photo, container, false);
        anhxa(view);
        ImageCursor imageCursor = new ImageCursor(getActivity().getApplicationContext());
        Cursor imgCursor = imageCursor.allImage();
        int image_column_index = imgCursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imgCursor.getCount();
        Log.d("howmany",count+"");
        this.arrPath = new String[this.count];
        ids = new int[count];
        this.thumbselecttion = new boolean[this.count];
        for (int i = 0; i < this.count; i++) {
            imgCursor.moveToPosition(i);
            ids[i] = imgCursor.getInt(image_column_index);
            int dataColumnIndex = imgCursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            arrPath[i] = imgCursor.getString(dataColumnIndex);
        }
        img_adapter = new Img_gridv_adapter(getActivity().getApplicationContext(), imgCursor, thumbselecttion, ids);
        gridView.setAdapter(img_adapter);
        return view;
    }

    private void anhxa(View view) {
        gridView = (GridView) view.findViewById(R.id.frg_choosept_gridV);
        btn_select = (Button) view.findViewById(R.id.frg_choosept_btn_select);
    }


}
