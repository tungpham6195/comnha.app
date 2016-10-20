package com.app.ptt.comnha;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Comment_rcyler_adapter;
import com.app.ptt.comnha.Classes.Times;
import com.app.ptt.comnha.FireBase.Comment;
import com.app.ptt.comnha.FireBase.Post;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewpostFragment extends Fragment {
    private String postID;
    TextView txt_un, txt_date, txt_title, txt_content, txt_likenumb, btn_like, btn_comment;
    Button btn_sendcomment;
    EditText edt_comment;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mlayoutManager;
    RecyclerView.Adapter mAdapter;
    ArrayList<Comment> comment_List;
    Firebase ref;

    public ViewpostFragment() {
        // Required empty public constructor
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_viewpost, container, false);
        anhxa(view);
        comment_List = new ArrayList<Comment>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_viewrev_rcyler_comments);
        mRecyclerView.setHasFixedSize(true);
        mlayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mlayoutManager);
        mAdapter = new Comment_rcyler_adapter(comment_List);
        mRecyclerView.setAdapter(mAdapter);
        Firebase.setAndroidContext(getActivity().getApplicationContext());
        ref = new Firebase(getString(R.string.firebase_path));
        Log.d("postID", postID);
        ref.child(getString(R.string.posts_CODE) + "/" + postID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                txt_title.setText(post.getTitle());
                txt_date.setText(post.getDate());
                txt_content.setText(post.getContent());
                txt_un.setText(post.getUsername());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        ref.child(getString(R.string.postcomment_CODE) + "/" + postID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                comment_List.add(comment);
                mAdapter.notifyDataSetChanged();
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
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return view;
    }

    private void anhxa(View view) {
        txt_un = (TextView) view.findViewById(R.id.frg_viewrev_txtv_username);
        txt_date = (TextView) view.findViewById(R.id.frg_viewrev_txtv_postdate);
        txt_title = (TextView) view.findViewById(R.id.frg_viewrev_txtv_tittle);
        txt_content = (TextView) view.findViewById(R.id.frg_viewrev_txtv_content);
        txt_likenumb = (TextView) view.findViewById(R.id.frg_viewrev_txtv_likenumb);
        btn_like = (TextView) view.findViewById(R.id.frg_viewrev_txtv_like);
        btn_comment = (TextView) view.findViewById(R.id.frg_viewrev_txtv_comment);
        btn_sendcomment = (Button) view.findViewById(R.id.frg_viewrev_btn_sendcomment);
        edt_comment = (EditText) view.findViewById(R.id.frg_viewrev_edt_comment);
//        edt_comment.setFocusable(false);
        btn_sendcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edt_comment.getText().toString().equals("")) {
                    Comment newComment = new Comment();
                    newComment.setContent(edt_comment.getText().toString());
                    newComment.setUsername(LoginSession.getInstance().getUsername());
                    newComment.setUserID(LoginSession.getInstance().getUserID());
                    newComment.setDate(new Times().getDate());
                    newComment.setTime(new Times().getTime());
                    edt_comment.setText(null);
                    String key = ref.child(getResources().getString(R.string.postcomment_CODE) + "/" + postID).push().getKey();
                    Map<String, Object> commentValues = newComment.toMap();
                    Map<String, Object> childUpdates = new HashMap<String, Object>();
                    childUpdates.put("/" + getResources().getString(R.string.postcomment_CODE) + "/" + postID + "/" + key, commentValues);
                    ref.updateChildren(childUpdates, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                Toast.makeText(getActivity().getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_comment.requestFocusFromTouch();
                Log.d("coomnent", "clicked");
            }
        });
    }

}
