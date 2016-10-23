package com.app.ptt.comnha;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Photolist_rcyler_adapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChoosePhotoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public ChoosePhotoFragment() {
        // Required empty public constructor
    }

    private final static int MEDIASTORE_LOADED_ID = 0;
    RecyclerView mRecyclerView;
    Photolist_rcyler_adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_photo, container, false);
        getLoaderManager().initLoader(MEDIASTORE_LOADED_ID, null, this);
        anhxa(view);

        return view;
    }

    private void anhxa(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_choosept_recyclerV);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new Photolist_rcyler_adapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE
        };
        String selection = MediaStore.Images.Media.MIME_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
        return new CursorLoader(
                getActivity().getApplicationContext(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, MediaStore.Images.Media.MIME_TYPE + "="+"\'image/jpeg\'", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Toast.makeText(getActivity(), data.getCount() + "", Toast.LENGTH_SHORT).show();
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

}
