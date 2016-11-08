package com.app.ptt.comnha;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.app.ptt.comnha.FireBase.Image;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.Post;
import com.app.ptt.comnha.SingletonClasses.DoPost;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.firebase.client.Firebase;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
public class AddpostFragment extends Fragment implements View.OnClickListener {

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

    public AddpostFragment() {
        // Required empty public constructor
    }

    DatabaseReference dbRef;
    StorageReference storeRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_addpost, container, false);
        anhXa(view);
        Firebase.setAndroidContext(getActivity());
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        storeRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
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
                Intent intent = new Intent(getActivity(), Adapter2Activity.class);
                intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frag_chooseloca_CODE));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.frg_post_fabchoseimg:
                Intent intent1 = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
                intent1.putExtra(getResources().getString(R.string.fragment_CODE),
                        getResources().getString(R.string.frag_chooseimg_CODE));
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                break;
            case R.id.frg_post_fabrate:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DoVoteFragment dovoteFragment = DoVoteFragment.newIntance(getResources().getString(R.string.text_vote));
                dovoteFragment.show(fm, "fragment_dovote");
                break;
            case R.id.btn_save:
                savePost(view);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (DoPost.getInstance().getMyLocation() != null) {
                img.setVisibility(View.VISIBLE);
                txt_address.setVisibility(View.VISIBLE);
                txt_address.setText(DoPost.getInstance().getMyLocation().getDiachi());
                txt_name.setText(DoPost.getInstance().getMyLocation().getName());
            } else {
                img.setVisibility(View.GONE);
                txt_address.setVisibility(View.GONE);
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
        DoPost.getInstance().setMyLocation(null);
        DoPost.getInstance().setVesinh(0);
        DoPost.getInstance().setGia(0);
        DoPost.getInstance().setFiles(null);
        DoPost.getInstance().setPhucvu(0);
    }

    private void savePost(View view) {
        boolean checloca = false;
        boolean checkrate = false;
        try {
            if (DoPost.getInstance().getMyLocation() == null) {
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
            UploadTask uploadTask = null;
            final String key = dbRef.child(getString(R.string.posts_CODE)).push().getKey();
            final MyLocation updateLoca = DoPost.getInstance().getMyLocation();
            final String locaID = DoPost.getInstance().getMyLocation().getLocaID(),
                    tinh = DoPost.getInstance().getMyLocation().getTinhtp() + "/",
                    huyen = DoPost.getInstance().getMyLocation().getQuanhuyen() + "/";
            updateLoca.setLocaID(null);
            updateLoca.setTinhtp(null);
            updateLoca.setQuanhuyen(null);
            try {
                if (DoPost.getInstance().getFiles().size() > 0) {
                    Log.i("size", DoPost.getInstance().getFiles().size() + "");
//                    for (File f : DoPost.getInstance().getFiles()) {
//                        Uri uri = Uri.fromFile(f);
//                        StorageReference childRef = storeRef.child(
//                                tinh+"/"+huyen+"/"+
//                                getResources().getString(R.string.users_CODE) + LoginSession.getInstance().getUserID() + "/"
//                                        + getResources().getString(R.string.posts_CODE) + uri.getLastPathSegment());
//                        uploadTask = childRef.putFile(uri);
//                    }
                    for (File f : DoPost.getInstance().getFiles()) {
                        Uri uri = Uri.fromFile(f);
                        StorageReference childRef = storeRef.child(
                                getResources().getString(R.string.images_CODE)
                                        + uri.getLastPathSegment());
                        uploadTask = childRef.putFile(uri);
                    }
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            addpost(key, locaID, tinh, huyen, updateLoca);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("OnFailureUploadPhoto", e.getMessage());
                        }
                    });
                } else {
                    addpost(key, locaID, tinh, huyen, updateLoca);
                }
            } catch (NullPointerException mess) {
                addpost(key, locaID, tinh, huyen, updateLoca);
            }
        }
    }

    private void addpost(String key, String locaID, String tinh, String huyen, MyLocation updateLoca) {
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
        newPost.setLocaID(locaID);
        newPost.setLocaName(updateLoca.getName());
        newPost.setDiachi(updateLoca.getDiachi());
        Map<String, Object> postValue = newPost.toMap();
        Map<String, Object> childUpdates = new HashMap<String, Object>();
        childUpdates.put(tinh + huyen +
                getResources().getString(R.string.posts_CODE) + key, postValue);
        childUpdates.put(tinh + huyen + getResources().getString(R.string.userpost_CODE)
                + LoginSession.getInstance().getUserID() + "/" + key, postValue);
//        childUpdates.put(tinh + huyen + getResources().getString(R.string.locationpost_CODE) +
//                locaID + "/" + key, postValue);
//        childUpdates.put(tinh + huyen + getResources().getString(R.string.locauserpost_CODE)
//                + DoPost.getInstance().getMyLocation().getLocaID() + "/"
//                + LoginSession.getInstance().getUserID() + "/" + key, postValue);

        long giaTong = updateLoca.getGiaTong() + DoPost.getInstance().getGia(),
                vsTong = updateLoca.getVsTong() + DoPost.getInstance().getVesinh(),
                pvTong = updateLoca.getPvTong() + DoPost.getInstance().getPhucvu(),
                size = updateLoca.getSize() + 1;
        updateLoca.setGiaTong(giaTong);
        updateLoca.setVsTong(vsTong);
        updateLoca.setPvTong(pvTong);
        updateLoca.setSize(size);
        updateLoca.setGiaAVG(giaTong / size);
        updateLoca.setVsAVG(vsTong / size);
        updateLoca.setPvAVG(pvTong / size);
        updateLoca.setTongAVG((giaTong + vsTong + pvTong) / (size * 3));
        childUpdates.put(tinh + huyen +
                getResources().getString(R.string.locations_CODE)
                + locaID, updateLoca);
        MyLocation usertrackLoca = updateLoca;
        childUpdates.put(tinh + huyen + getString(R.string.usertrackloca_CODE) + locaID, usertrackLoca);
        try {
            for (File f : DoPost.getInstance().getFiles()) {
                Uri uri = Uri.fromFile(f);
                String fileKey = dbRef.child(getResources().getString(R.string.images_CODE)).push().getKey();
                Image newImage = new Image();
                newImage.setName(uri.getLastPathSegment());
                newImage.setPostID(key);
                newImage.setUserID(LoginSession.getInstance().getUserID());
                newImage.setLocaID(locaID);
                childUpdates.put(getResources().getString(R.string.images_CODE)
                        + fileKey, newImage);
            }
        } catch (NullPointerException mess) {
        }

        dbRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), "Đăng bài bị lỗi" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
        });
    }

}
