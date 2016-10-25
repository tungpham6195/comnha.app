package com.app.ptt.comnha;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Classes.CalcuAVGRate;
import com.app.ptt.comnha.Classes.Posts;
import com.app.ptt.comnha.Classes.Times;
import com.app.ptt.comnha.FireBase.Post;
import com.app.ptt.comnha.SingletonClasses.DoPost;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddPostFragment extends Fragment implements View.OnClickListener {

    Button btn_save;
    ImageView img;
    TextView txt_name, txt_address;
    EditText edt_title, edt_content;
    Posts posts;
    static final int PICK_LOCATION_REQUEST = 1;
    ProgressDialog mProgressDialog;
    FloatingActionButton fab_choseloca, fab_addphoto, fab_rate;
    FloatingActionMenu fab_menu;

    ArrayList<Post> postlist;
    CalcuAVGRate newcalcu;

    public AddPostFragment() {
        // Required empty public constructor
    }

    Firebase ref;
    StorageReference storeRef;
    FirebaseStorage storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_addpost, container, false);
        anhXa(view);
        Firebase.setAndroidContext(getActivity());
        ref = new Firebase(getResources().getString(R.string.firebase_path));
        storage = FirebaseStorage.getInstance();
        storeRef = storage.getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
        return view;

    }

    void anhXa(View view) {
        img = (ImageView) view.findViewById(R.id.frg_post_img);
        txt_name = (TextView) view.findViewById(R.id.frg_post_name);
        txt_address = (TextView) view.findViewById(R.id.frg_post_address);
        btn_save = (Button) view.findViewById(R.id.btn_save);
        fab_choseloca = (FloatingActionButton) view.findViewById(R.id.frg_post_fabchoseloca);
        fab_addphoto = (FloatingActionButton) view.findViewById(R.id.frg_post_fabchoseimg);
        fab_rate = (FloatingActionButton) view.findViewById(R.id.frg_post_fabrate);
        fab_menu = (FloatingActionMenu) view.findViewById(R.id.frg_post_fabMenu);
        edt_title = (EditText) view.findViewById(R.id.edt_title);
        edt_content = (EditText) view.findViewById(R.id.edt_content);
        fab_addphoto.setOnClickListener(this);
        fab_choseloca.setOnClickListener(this);
        fab_rate.setOnClickListener(this);
        fab_menu.setClosedOnTouchOutside(true);
        btn_save.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText(getActivity().getApplicationContext(), "resume post Frag with key: " + locaID, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frg_post_fabchoseloca:
                Intent intent = new Intent(getActivity(), AdapterActivity.class);
                intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frag_chooseloca_CODE));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.frg_post_fabchoseimg:
                Intent intent1 = new Intent(getActivity().getApplicationContext(), AdapterActivity.class);
                intent1.putExtra(getResources().getString(R.string.fragment_CODE),
                        getResources().getString(R.string.frag_chooseimg_CODE));
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                break;
            case R.id.frg_post_fabrate:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DoVoteFragment dovoteFragment = DoVoteFragment.newIntance(getResources().getString(R.string.text_vote));
                dovoteFragment.show(fm, "fragment_vote");
                break;
            case R.id.btn_save:
                savePost(view);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.i("startFrag", "start");
        try {
            if (!DoPost.getInstance().getLocaID().isEmpty()) {
                img.setVisibility(View.VISIBLE);
                txt_address.setVisibility(View.VISIBLE);
                txt_address.setText(DoPost.getInstance().getAddress());
                txt_name.setText(DoPost.getInstance().getName());
            }
        } catch (NullPointerException mess) {
            img.setVisibility(View.GONE);
            txt_address.setVisibility(View.GONE);
            Log.e("nullChooseloca", mess.getMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        DoPost.getInstance().setLocaID(null);
        DoPost.getInstance().setAddress(null);
        DoPost.getInstance().setName(null);
        DoPost.getInstance().setVesinh(0);
        DoPost.getInstance().setGia(0);
        DoPost.getInstance().setPhucvu(0);
    }

    private void savePost(View view) {
        boolean checloca = false;
        boolean checkrate = false;
        try {
            if (DoPost.getInstance().getLocaID().equals("")) {
                checloca = true;
            }
        } catch (NullPointerException mess) {
            checloca = true;
        }
        try {
            if (DoPost.getInstance().getGia() < 1
                    && DoPost.getInstance().getVesinh() < 1
                    && DoPost.getInstance().getPhucvu() < 1) {
                checkrate = true;
            }
        } catch (NullPointerException mess) {
            checkrate = true;
        }
        if (edt_title.getText().toString().trim().equals("")) {
            Snackbar.make(view, getResources().getString(R.string.txt_notitle), Snackbar.LENGTH_SHORT).show();
        } else if (edt_content.getText().toString().trim().equals("")) {
            Snackbar.make(view, getResources().getString(R.string.txt_nocontent), Snackbar.LENGTH_SHORT).show();
        } else if (checloca) {
            Snackbar.make(view, getResources().getString(R.string.txt_nochoseloca), Snackbar.LENGTH_SHORT).show();
        } else if (checkrate) {
            Snackbar.make(view, getResources().getString(R.string.txt_norate), Snackbar.LENGTH_SHORT).show();
        } else {
            mProgressDialog = ProgressDialog.show(getActivity(),
                    getResources().getString(R.string.txt_plzwait),
                    getResources().getString(R.string.txt_addinpost),
                    true, false);
            Post newPost = new Post();
            newPost.setTitle(edt_title.getText().toString());
            newPost.setContent(edt_content.getText().toString());
            newPost.setUid(LoginSession.getInstance().getUserID());
            newPost.setUsername(LoginSession.getInstance().getUsername());
            newPost.setDate(new Times().getTime());
            newPost.setTime(new Times().getDate());
            newPost.setGia(DoPost.getInstance().getGia());
            newPost.setVesinh(DoPost.getInstance().getVesinh());
            newPost.setPhucvu(DoPost.getInstance().getPhucvu());
            final String key = ref.child("Posts").push().getKey();
            Map<String, Object> postValue = newPost.toMap();
            final Map<String, Object> childUpdates = new HashMap<String, Object>();
            childUpdates.put(getResources().getString(R.string.posts_CODE) + key, postValue);
            childUpdates.put(getResources().getString(R.string.userpost_CODE)
                    + LoginSession.getInstance().getUserID() + "/" + key, postValue);
            childUpdates.put(getResources().getString(R.string.locationpost_CODE) +
                    DoPost.getInstance().getLocaID() + "/" + key, postValue);
            childUpdates.put(getResources().getString(R.string.locauserpost_CODE)
                    + DoPost.getInstance().getLocaID() + "/"
                    + LoginSession.getInstance().getUserID() + "/" + key, postValue);
            ref.updateChildren(childUpdates, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getActivity(), "Đăng bài bị lỗi" + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        boolean checkComplete = false;
                        UploadTask uploadTask = null;
                        try {
                            if (DoPost.getInstance().getFiles().size() > 0) {
                                Log.i("size", DoPost.getInstance().getFiles().size() + "");
                                for (File f : DoPost.getInstance().getFiles()) {
                                    Uri uri = Uri.fromFile(f);
                                    StorageReference childRef = storeRef.child(getResources().getString(R.string.users_CODE)
                                            + getResources().getString(R.string.posts_CODE) + uri.getLastPathSegment());
                                    uploadTask = childRef.putFile(uri);
                                }
                                for (File f : DoPost.getInstance().getFiles()) {
                                    Uri uri = Uri.fromFile(f);
                                    StorageReference childRef = storeRef.child(getResources().getString(R.string.locations_CODE)
                                            + DoPost.getInstance().getLocaID() + "/" + uri.getLastPathSegment());
                                    uploadTask = childRef.putFile(uri);
                                }
                                for (File f : DoPost.getInstance().getFiles()) {
                                    Uri uri = Uri.fromFile(f);
                                    StorageReference childRef = storeRef.child(getResources().getString(R.string.posts_CODE)
                                            + key + "/" + uri.getLastPathSegment());
                                    uploadTask = childRef.putFile(uri);
                                }
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                                        getActivity().finish();

                                    }
                                });
                            } else {
                                mProgressDialog.dismiss();
                                Toast.makeText(getActivity(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                        } catch (NullPointerException mess) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getActivity(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }


                    }
                }
            });
        }
    }
}
