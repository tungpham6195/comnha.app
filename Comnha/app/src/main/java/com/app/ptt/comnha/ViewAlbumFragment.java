package com.app.ptt.comnha;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ptt.comnha.Adapters.Viewalbum_rcyler_adapter;
import com.app.ptt.comnha.FireBase.Image;
import com.app.ptt.comnha.SingletonClasses.OpenAlbum;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewAlbumFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Image> images = new ArrayList<>();

    public ViewAlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_album, container, false);
        images = OpenAlbum.getInstance().getImage();
        anhxa(view);
        return view;
    }

    private void anhxa(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_viewalbum_rcylerAlbum);
        GridLayoutManager gridLayoutManager = new
                GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        layoutManager = gridLayoutManager;
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new Viewalbum_rcyler_adapter(images);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        OpenAlbum.getInstance().setImage(null);
        super.onDestroy();
    }
}
