package com.app.ptt.comnha;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Photos_rcyler_adapter;
import com.app.ptt.comnha.FireBase.Image;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
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
 * Created by PTT on 10/25/2016.
 */

public class PhotoFragment extends Fragment {
    FirebaseDatabase database;
    DatabaseReference dbRef;
    StorageReference storageRef;
    FirebaseStorage storage;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    GridLayoutManager gridLayoutManager;
    ArrayList<Image> photoList;
    ChildEventListener imgageValueEventListener;

    public PhotoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
        anhxa(view);

        imgageValueEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
//                    Toast.makeText(getActivity(), dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("checkListenerFromImages", "have changed");
                    final Image image = dataSnapshot.getValue(Image.class);
                    image.setImageID(dataSnapshot.getKey());
                    storageRef.child(getResources().getString(R.string.images_CODE)
                            + image.getName())
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            image.setPath(uri);
                            photoList.add(image);
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
        dbRef.child(getResources().getString(R.string.images_CODE))
                .orderByChild("userID")
                .equalTo(LoginSession.getInstance().getUserID())
                .addChildEventListener(imgageValueEventListener);
        return view;
    }

    void anhxa(View view) {
        photoList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_photo_recyclerV);
        gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new Photos_rcyler_adapter(photoList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        dbRef.removeEventListener(imgageValueEventListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
