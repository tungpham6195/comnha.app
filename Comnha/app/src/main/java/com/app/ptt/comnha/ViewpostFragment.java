package com.app.ptt.comnha;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Comment_rcyler_adapter;
import com.app.ptt.comnha.Adapters.Photos_rcyler_adapter;
import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Classes.Times;
import com.app.ptt.comnha.FireBase.Comment;
import com.app.ptt.comnha.FireBase.Image;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.Post;
import com.app.ptt.comnha.FireBase.TrackLocation;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    ImageView img_option;
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
    ValueEventListener postValueEventListener, locaValueEventListener, tracklocaValueEventListener;
    LinearLayout linearAlbum;
    ProgressDialog mProgressDialog;
    MyLocation myLocation;
    boolean hastrack = false;
    TrackLocation trackLocation;

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
        mProgressDialog.show();
        tracklocaValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                trackLocation = dataSnapshot.getValue(TrackLocation.class);
                trackLocation.setLocaID(dataSnapshot.getKey());
                hastrack = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        locaValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myLocation = dataSnapshot.getValue(MyLocation.class);
                myLocation.setLocaID(dataSnapshot.getKey());
                dbRef.child(tinh + "/" + huyen + "/" + getString(R.string.usertrackloca_CODE)
                        + post.getUid() + "/" + myLocation.getLocaID())
                        .addListenerForSingleValueEvent(tracklocaValueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        albumChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
//                    Toast.makeText(getActivity(), dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("checkListenerFromImages", "have changed");
                    final Image image = dataSnapshot.getValue(Image.class);
                    image.setImageID(dataSnapshot.getKey());
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
                mProgressDialog.dismiss();
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
                .addListenerForSingleValueEvent(postValueEventListener);
        dbRef.child(tinh + "/" + huyen + "/" +
                getString(R.string.postcomment_CODE) + postID)
                .addChildEventListener(commentChildEventListener);
        dbRef.child(getResources().getString(R.string.images_CODE))
                .orderByChild("postID")
                .equalTo(postID).limitToFirst(3)
                .addChildEventListener(albumChildEventListener);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dbRef.child(tinh + "/" + huyen + "/"
                        + getString(R.string.locations_CODE) + post.getLocaID())
                        .addListenerForSingleValueEvent(locaValueEventListener);

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ChoosePost.getInstance().setPostID(null);
        ChoosePost.getInstance().setTinh(null);
        ChoosePost.getInstance().setHuyen(null);
    }

    private void anhxa(View view) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.txt_plzwait));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        img_option = (ImageView) view.findViewById(R.id.frg_viewreview_imgoption);
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
        img_option.setOnClickListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        dbRef.removeEventListener(tracklocaValueEventListener);
        dbRef.removeEventListener(locaValueEventListener);
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
                            getResources().getString(R.string.postcomment_CODE) + postID + "/"
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
            case R.id.frg_viewreview_imgoption:
                AnimationUtils.rotate90postoption(img_option);
                PopupMenu popupMenu = new PopupMenu(getActivity(), img_option, Gravity.START);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_reviewdetial, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu menu) {
                        AnimationUtils.rotate90postoptionBack(img_option);
                    }
                });
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_reviewdetail_delete:
                                if (!post.getUid().equals(LoginSession.getInstance()
                                        .getUserID())) {
                                    Toast.makeText(getContext(), "Bạn không thể xóa, bài này thuộc sở hữa của người khác",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    new AlertDialog.Builder(getActivity())
                                            .setMessage("Bạn muốn xóa bài viết này?")
                                            .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dbRef.removeEventListener(postValueEventListener);
                                                    mProgressDialog.show();
                                                    delete();
                                                }
                                            })
                                            .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                }
                                return true;
                            case R.id.popup_reviewdetail_report:
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                break;
        }
    }

    private void deletepost() {
        if (hastrack) {
            int trackcount = trackLocation.getCountTrack() - 1;
            if (trackLocation.getCountTrack() == 0) {
                dbRef.child(tinh + "/" + huyen + "/"
                        + getString(R.string.usertrackloca_CODE) +
                        post.getUid()
                        + "/" + myLocation.getLocaID()).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                delete();
                            }
                        });
            } else if (trackLocation.getCountTrack() > 0) {
                trackLocation.setCountTrack(trackcount);
                Map<String, Object> updatechild = new HashMap<>();
                updatechild.put(tinh + "/" + huyen + "/"
                        + getString(R.string.usertrackloca_CODE) +
                        post.getUid()
                        + "/" + myLocation.getLocaID(), trackLocation);
                dbRef.updateChildren(updatechild)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                delete();
                            }
                        });
            }
        } else {
//            Toast.makeText(getContext(),"false",Toast.LENGTH_SHORT).show();
            delete();
        }
    }

    private void delete() {
        long size = myLocation.getSize() - 1;
        myLocation.setSize(size);
        if (size != 0) {
            myLocation.setGiaTong(myLocation.getGiaTong() - post.getGia());
            myLocation.setVsTong(myLocation.getVsTong() - post.getVesinh());
            myLocation.setPvTong(myLocation.getPvTong() - post.getPhucvu());
            myLocation.setGiaAVG(myLocation.getGiaTong() / size);
            myLocation.setVsAVG(myLocation.getVsTong() / size);
            myLocation.setPvAVG(myLocation.getPvTong() / size);
        } else {
            myLocation.setGiaTong(0);
            myLocation.setVsTong(0);
            myLocation.setPvTong(0);
            myLocation.setGiaAVG(0);
            myLocation.setVsAVG(0);
            myLocation.setPvAVG(0);
        }

        myLocation.setTongAVG((myLocation.getGiaAVG() + myLocation.getVsAVG() +
                myLocation.getPvAVG()) / 3);
        Map<String, Object> updateChild = new HashMap<>();
        updateChild.put(tinh + "/" + huyen + "/" +
                getString(R.string.locations_CODE) + myLocation.getLocaID(), myLocation);
        dbRef.updateChildren(updateChild).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dbRef.child(tinh + "/" + huyen + "/"
                        + getString(R.string.posts_CODE) + postID).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isComplete()) {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getContext(),
                                            task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    dbRef.child(tinh + "/" + huyen + "/" +
                                            getString(R.string.userpost_CODE)
                                            + post.getUid() + "/" + postID).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isComplete()) {
                                                        Toast.makeText(getContext(),
                                                                task.getException().getMessage(),
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        dbRef.child(tinh + "/" + huyen + "/" +
                                                                getString(R.string.postcomment_CODE) +
                                                                postID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (!task.isComplete()) {
                                                                    Toast.makeText(getContext(),
                                                                            task.getException().getMessage(),
                                                                            Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    mProgressDialog.dismiss();
                                                                    Toast.makeText(getContext(), "Xóa thành công",
                                                                            Toast.LENGTH_SHORT).show();
                                                                    getActivity().finish();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }
        });
    }
}
