package com.app.ptt.comnha;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Viewalbum_rcyler_adapter;
import com.app.ptt.comnha.FireBase.Image;
import com.app.ptt.comnha.SingletonClasses.OpenAlbum;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewAlbumFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Image> images;
    DatabaseReference dbRef;
    String fromFrag;
    StorageReference storageRef;

    public void setFromFrag(String fromFrag) {
        this.fromFrag = fromFrag;
    }


    public ViewAlbumFragment() {
        // Required empty public constructor
    }

    ChildEventListener imgChildEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_album, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_path));
        storageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
        imgChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
//                    Toast.makeText(getActivity(), dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("checkListenerFromImages", "have changed");
                    final Image image = dataSnapshot.getValue(Image.class);
                    image.setImageID(dataSnapshot.getKey());
                    storageRef.child(getString(R.string.images_CODE) + "/"
                            + image.getName())
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            image.setPath(uri);
                            images.add(image);
                            mAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (NullPointerException | IllegalStateException mess) {

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        if (fromFrag.equals(getString(R.string.frg_viewpost_CODE))) {
            dbRef.child(getString(R.string.images_CODE)
            ).orderByChild("postID").equalTo(OpenAlbum.getInstance().getPostID())
                    .addChildEventListener(imgChildEventListener);
        }
        if (fromFrag.equals(getString(R.string.frag_locadetail_CODE))) {
            dbRef.child(getString(R.string.images_CODE)
            ).orderByChild("locaID").equalTo(OpenAlbum.getInstance().getPostID())
                    .addChildEventListener(imgChildEventListener);
        }
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
        images = new ArrayList<>();
        mAdapter = new Viewalbum_rcyler_adapter(images);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        OpenAlbum.getInstance().setImage(null);
        super.onDestroy();
    }
}
