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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewpostFragment extends Fragment implements View.OnClickListener {
    private String postID;
    TextView txt_un, txt_date, txt_title, txt_content, txt_likenumb, btn_like, btn_comment;
    Button btn_sendcomment;
    EditText edt_comment;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mlayoutManager;
    RecyclerView.Adapter mAdapter;
    ArrayList<Comment> comment_List;
    DatabaseReference dbRef;
    ChildEventListener commentChildEventListener;
    ValueEventListener postValueEventListener;


    public ViewpostFragment() {
        // Required empty public constructor
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    Post post;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_viewpost, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        anhxa(view);
        postValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                post = dataSnapshot.getValue(Post.class);
                txt_title.setText(post.getTitle());
                txt_date.setText(post.getDate());
                txt_content.setText(post.getContent());
                txt_un.setText(post.getUsername());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        commentChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                comment_List.add(comment);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        Log.d("postID", postID);
        dbRef.child(getString(R.string.posts_CODE) + postID)
                .addListenerForSingleValueEvent(postValueEventListener);
        dbRef.child(getString(R.string.postcomment_CODE) + "/" + postID)
                .addChildEventListener(commentChildEventListener);
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
        comment_List = new ArrayList<Comment>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_viewrev_rcyler_comments);
        mRecyclerView.setHasFixedSize(true);
        mlayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mlayoutManager);
        mAdapter = new Comment_rcyler_adapter(comment_List);
        mRecyclerView.setAdapter(mAdapter);
//        edt_comment.setFocusable(false);
        btn_sendcomment.setOnClickListener(this);
        btn_like.setOnClickListener(this);
        btn_comment.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frg_viewrev_txtv_like:
                Toast.makeText(getActivity(), postID, Toast.LENGTH_SHORT).show();
                dbRef.child(getResources().getString(R.string.posts_CODE)
                        + postID)
                        .runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT).show();
                                Post post = mutableData.getValue(Post.class);
                                if (post == null) {
                                    return Transaction.success(mutableData);
                                }
                                if (post.getLikes().containsKey(LoginSession.getInstance().getUserID())) {
                                    post.setLikeCount(post.getLikeCount() - 1);
                                    Map<String, Boolean> removeLike = post.getLikes();
                                    removeLike.remove(LoginSession.getInstance().getUserID());
                                    post.setLikes(removeLike);
                                } else {
                                    post.setLikeCount(post.getLikeCount() + 1);
                                    Map<String, Boolean> addLike = post.getLikes();
                                    addLike.put(LoginSession.getInstance().getUserID(), true);
                                    post.setLikes(addLike);
                                }
                                mutableData.setValue(post);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                            }
                        });
                break;
            case R.id.frg_viewrev_btn_sendcomment:
                if (!edt_comment.getText().toString().equals("")) {
                    Comment newComment = new Comment();
                    newComment.setContent(edt_comment.getText().toString());
                    newComment.setUsername(LoginSession.getInstance().getUsername());
                    newComment.setUserID(LoginSession.getInstance().getUserID());
                    newComment.setDate(new Times().getDate());
                    newComment.setTime(new Times().getTime());
                    edt_comment.setText(null);
                    String key = dbRef.child(getResources().getString(R.string.postcomment_CODE) + postID).push().getKey();
                    Map<String, Object> commentValues = newComment.toMap();
                    Map<String, Object> childUpdates = new HashMap<String, Object>();
                    childUpdates.put(getResources().getString(R.string.postcomment_CODE)
                            + postID + "/" + key, commentValues);
                    childUpdates.put(getResources().getString(R.string.posts_CODE)
                            + postID + "/commentCount", comment_List.size() + 1);
                    childUpdates.put(getResources().getString(R.string.locationpost_CODE) + post.getLocaID() + "/"
                            + postID + "/commentCount", comment_List.size() + 1);
                    childUpdates.put(getResources().getString(R.string.locauserpost_CODE)
                            + post.getLocaID() + "/"
                            + LoginSession.getInstance().getUserID() + "/"
                            + postID + "/commentCount", comment_List.size() + 1);
                    childUpdates.put(getResources().getString(R.string.userpost_CODE)
                            + LoginSession.getInstance().getUserID() + "/"
                            + postID + "/commentCount", comment_List.size() + 1);
                    dbRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(getActivity().getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
            case R.id.frg_viewrev_txtv_comment:
                edt_comment.requestFocusFromTouch();
                break;
        }
    }
}
