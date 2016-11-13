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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Comment_rcyler_adapter;
import com.app.ptt.comnha.Adapters.Photos_rcyler_adapter;
import com.app.ptt.comnha.Classes.Times;
import com.app.ptt.comnha.FireBase.Comment;
import com.app.ptt.comnha.FireBase.Image;
import com.app.ptt.comnha.FireBase.Post;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewpostFragment extends Fragment implements View.OnClickListener {
    private String postID, tinh, huyen;
    TextView txt_un, txt_date, txt_title,
            txt_content, txt_likenumb, btn_like, btn_comment,
            txt_gia, txt_vs, txt_pv;
    Button btn_sendcomment;
    EditText edt_comment;
    RecyclerView mRecyclerView, mRecyclerViewAlbum;
    RecyclerView.LayoutManager mlayoutManager, mlayoutManagerAlbum;
    RecyclerView.Adapter mAdapter, mAdapterAlbum;
    ArrayList<Comment> comment_List;
    ArrayList<Image> albumList;
    DatabaseReference dbRef;
    StorageReference storageRef;
    ChildEventListener commentChildEventListener, albumChildEventListener;
    ValueEventListener postValueEventListener;
    LinearLayout linearAlbum;

    public ViewpostFragment() {
        // Required empty public constructor
    }

    Post post;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_viewpost, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        storageRef = FirebaseStorage
                .getInstance()
                .getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
        tinh = ChoosePost.getInstance().getTinh();
        huyen = ChoosePost.getInstance().getHuyen();
        postID = ChoosePost.getInstance().getPostID();
        anhxa(view);
        albumChildEventListener = new ChildEventListener() {
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
                            albumList.add(image);
                            mAdapterAlbum.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
        postValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                post = dataSnapshot.getValue(Post.class);
                post.setPostID(dataSnapshot.getKey());
                txt_title.setText(post.getTitle());
                txt_date.setText(post.getDate());
                txt_content.setText(post.getContent());
                txt_un.setText(post.getUsername());
                txt_likenumb.setText(post.getLikeCount() + " Likes   " + post.getCommentCount() + " Comments");
                txt_gia.setText(post.getGia() + "");
                txt_pv.setText(post.getPhucvu() + "");
                txt_vs.setText(post.getVesinh() + "");
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
        dbRef.child(tinh + "/" + huyen + "/" +
                getString(R.string.posts_CODE) + postID)
                .addValueEventListener(postValueEventListener);
        dbRef.child(tinh + "/" + huyen + "/" +
                getString(R.string.postcomment_CODE)).orderByChild("postID").equalTo(postID)
                .addChildEventListener(commentChildEventListener);
        dbRef.child(getResources().getString(R.string.images_CODE))
                .orderByChild("postID")
                .equalTo(postID)
                .limitToFirst(3)
                .addChildEventListener(albumChildEventListener);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ChoosePost.getInstance().setPostID(null);
        ChoosePost.getInstance().setTinh(null);
        ChoosePost.getInstance().setHuyen(null);
    }

    private void anhxa(View view) {
        linearAlbum = (LinearLayout) view.findViewById(R.id.frg_viewrev_lnearAlbum);
        txt_gia = (TextView) view.findViewById(R.id.frg_frg_viewpost_txt_gia);
        txt_vs = (TextView) view.findViewById(R.id.frg_frg_viewpost_txt_vesinh);
        txt_pv = (TextView) view.findViewById(R.id.frg_frg_viewpost_txt_phucvu);
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
        albumList = new ArrayList<>();
        mRecyclerViewAlbum = (RecyclerView) view.findViewById(R.id.frg_frg_viewpost_rcyler_album);
        mRecyclerViewAlbum.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerViewAlbum.setLayoutManager(gridLayoutManager);
        mAdapterAlbum = new Photos_rcyler_adapter(albumList, getActivity());
        mRecyclerViewAlbum.setAdapter(mAdapterAlbum);
        linearAlbum.setOnClickListener(this);
//        edt_comment.setFocusable(false);
        btn_sendcomment.setOnClickListener(this);
        btn_like.setOnClickListener(this);
        btn_comment.setOnClickListener(this);
        if (LoginSession.getInstance().getUserID() == null) {
            edt_comment.setEnabled(false);
        } else {
            edt_comment.setEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frg_viewrev_txtv_like:

                break;
            case R.id.frg_viewrev_btn_sendcomment:
                if (!edt_comment.getText().toString().equals("")) {
                    Comment newComment = new Comment();
                    newComment.setContent(edt_comment.getText().toString());
                    newComment.setUsername(LoginSession.getInstance().getUsername());
                    newComment.setUserID(LoginSession.getInstance().getUserID());
                    newComment.setDate(new Times().getDate());
                    newComment.setTime(new Times().getTime());
                    newComment.setPostID(postID);
                    edt_comment.setText(null);
                    String key = dbRef.child(getResources().getString(R.string.postcomment_CODE) + postID).push().getKey();
                    Map<String, Object> commentValues = newComment.toMap();
                    Map<String, Object> childUpdates = new HashMap<String, Object>();
                    childUpdates.put(tinh + "/" + huyen + "/" +
                            getResources().getString(R.string.postcomment_CODE)
                            + key, commentValues);
                    childUpdates.put(tinh + "/" + huyen + "/" +
                            getResources().getString(R.string.posts_CODE)
                            + postID + "/commentCount", comment_List.size() + 1);
//                    childUpdates.put(tinh + "/" + huyen + "/" +
//                            getResources().getString(R.string.locationpost_CODE) + post.getLocaID() + "/"
//                            + postID + "/commentCount", comment_List.size() + 1);
//                    childUpdates.put(getResources().getString(R.string.locauserpost_CODE)
//                            + post.getLocaID() + "/"
//                            + post.getUid() + "/"
//                            + postID + "/commentCount", comment_List.size() + 1);
                    childUpdates.put(tinh + "/" + huyen + "/" +
                            getResources().getString(R.string.userpost_CODE)
                            + post.getUid() + "/"
                            + postID + "/commentCount", comment_List.size() + 1);
                    dbRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.e("updateChildrenComment", databaseError.getMessage());
                                Toast.makeText(getActivity().getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
            case R.id.frg_viewrev_txtv_comment:
                edt_comment.requestFocusFromTouch();
                break;
            case R.id.frg_viewrev_lnearAlbum:
                break;
        }
    }
}
