package com.app.ptt.comnha;


import android.app.ProgressDialog;
import android.content.Intent;
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
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.app.ptt.comnha.SingletonClasses.DoRate;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

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

    Firebase ref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_addpost, container, false);
        anhXa(view);
        Firebase.setAndroidContext(getActivity());
        Firebase.setAndroidContext(getContext());
        ref = new Firebase(getResources().getString(R.string.firebase_path));
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
                DovoteFragment dovoteFragment = DovoteFragment.newIntance(getResources().getString(R.string.text_vote));
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
            if (!ChooseLoca.getInstance().getLocaID().isEmpty()) {
                img.setVisibility(View.VISIBLE);
                txt_address.setVisibility(View.VISIBLE);
                txt_address.setText(ChooseLoca.getInstance().getAddress());
                txt_name.setText(ChooseLoca.getInstance().getName());
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
        ChooseLoca.getInstance().setLocaID(null);
        ChooseLoca.getInstance().setAddress(null);
        ChooseLoca.getInstance().setName(null);
        DoRate.getInstance().setVesinh(0);
        DoRate.getInstance().setGia(0);
        DoRate.getInstance().setPhucvu(0);
    }

    private void savePost(View view) {
        boolean checloca = false;
        boolean checkrate = false;
        try {
            if (ChooseLoca.getInstance().getLocaID().equals("")) {
                checloca = true;
            }
        } catch (NullPointerException mess) {
            checloca = true;
        }
        try {
            if (DoRate.getInstance().getGia() < 1
                    && DoRate.getInstance().getVesinh() < 1
                    && DoRate.getInstance().getPhucvu() < 1) {
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
                    true, true);
            Post newPost = new Post();
            newPost.setTitle(edt_title.getText().toString());
            newPost.setContent(edt_content.getText().toString());
            newPost.setUid(LoginSession.getInstance().getUserID());
            newPost.setUsername(LoginSession.getInstance().getUsername());
            newPost.setDate(new Times().getTime());
            newPost.setTime(new Times().getDate());
            newPost.setGia(DoRate.getInstance().getGia());
            newPost.setVesinh(DoRate.getInstance().getVesinh());
            newPost.setPhucvu(DoRate.getInstance().getPhucvu());
            String key = ref.child("Posts").push().getKey();
            Map<String, Object> postValue = newPost.toMap();
            Map<String, Object> childUpdates = new HashMap<String, Object>();
            childUpdates.put(getResources().getString(R.string.posts_CODE) + key, postValue);
            childUpdates.put(getResources().getString(R.string.userpost_CODE)
                    + LoginSession.getInstance().getUserID() + "/" + key, postValue);
            childUpdates.put(getResources().getString(R.string.locationpost_CODE) +
                    ChooseLoca.getInstance().getLocaID() + "/" + key, postValue);
            childUpdates.put(getResources().getString(R.string.locauserpost_CODE)
                    + LoginSession.getInstance().getUserID() + "/" + key, postValue);
            ref.updateChildren(childUpdates, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    mProgressDialog.dismiss();
                    if (firebaseError != null) {
                        Toast.makeText(getActivity(), "Đăng bài bị lỗi" + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("postComplete", "completed");

                        Toast.makeText(getActivity(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                }
            });

        }
    }
}
