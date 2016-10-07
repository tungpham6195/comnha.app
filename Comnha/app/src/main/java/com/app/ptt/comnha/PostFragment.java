package com.app.ptt.comnha;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.app.ptt.comnha.Classes.Posts;
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.firebase.client.Firebase;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {
    Button btn_save, btn_upImage, btn_tag, btn_vote, btn_location;
    EditText edt_title, edt_content;
    Posts posts;
    static final int PICK_LOCATION_REQUEST = 1;

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
        edt_title = (EditText) view.findViewById(R.id.edt_title);
        edt_content = (EditText) view.findViewById(R.id.edt_content);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posts = new Posts();
                posts.setTitle(edt_title.getText().toString());
                posts.setLocaID(ChooseLoca.getInstance().getLocaID());
                posts.setContent(edt_content.getText().toString());
                posts.setUserID(LoginSession.getInstance().getUserID());
                posts.setContext(getActivity().getApplicationContext());
                posts.createNew();
            }
        });
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AdapterActivity.class);
                intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frag_chooseloca_CODE));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText(getActivity().getApplicationContext(), "resume post Frag with key: " + locaID, Toast.LENGTH_SHORT).show();
    }


}
