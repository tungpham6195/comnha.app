package com.app.ptt.comnha;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.app.ptt.comnha.Classes.CreatePost;
import com.firebase.client.Firebase;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {
    Button btn_save, btn_upImage, btn_tag, btn_vote, btn_location;
    EditText edt_tittle, edt_content;
    CreatePost createPost;
    String userID;

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public PostFragment() {
        // Required empty public constructor
    }

    Firebase dbRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        anhXa(view);
        Firebase.setAndroidContext(getContext());
//        dbRef=new Firebase("https://com-nha.firebaseio.com/");
//        dbRef.child("Posts/-KS-ffaqVs7mfzsCmIyt/LoaiPost").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                String groupKey=dataSnapshot.getKey();
//                Toast.makeText(getContext(),groupKey,Toast.LENGTH_SHORT).show();
//                dbRef.child("LoaiPost/"+groupKey+"/name").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Toast.makeText(getContext(),dataSnapshot.getValue().toString(),Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onCancelled(FirebaseError firebaseError) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });

        return view;

    }

    void anhXa(View view) {
        btn_save = (Button) view.findViewById(R.id.btn_save);
        btn_upImage = (Button) view.findViewById(R.id.btn_upImage);
        btn_tag = (Button) view.findViewById(R.id.btn_hashtag);
        btn_vote = (Button) view.findViewById(R.id.btn_vote);
        btn_location = (Button) view.findViewById(R.id.btn_choselocat);
        edt_tittle = (EditText) view.findViewById(R.id.edt_tittle);
        edt_content = (EditText) view.findViewById(R.id.edt_content);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPost = new CreatePost();
                createPost.setTittle(edt_tittle.getText().toString());
                createPost.setContent(edt_content.getText().toString());
                createPost.setUserID(userID );
                createPost.setContext(getActivity().getApplicationContext());
                createPost.createNew();
            }
        });
    }
}
