package com.app.ptt.comnha;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Photos_rcyler_adapter;
import com.app.ptt.comnha.Adapters.Reviewlist_rcyler_adapter;
import com.app.ptt.comnha.Adapters.Thucdon_rcyler_adapter;
import com.app.ptt.comnha.Classes.CalcuAVGRate;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener;
import com.app.ptt.comnha.FireBase.Food;
import com.app.ptt.comnha.FireBase.Image;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.Post;
import com.app.ptt.comnha.Modules.ConnectionDetector;
import com.app.ptt.comnha.Modules.Storage;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.DoPost;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.app.ptt.comnha.SingletonClasses.ReportLocal;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class LocadetailFragment extends Fragment {
    private String locaID, tinh, huyen;
    private static final String LOG = LocadetailFragment.class.getSimpleName();
    DatabaseReference dbRef;
    RecyclerView mRecyclerView, menuRecyclerView, albumRecyclerView;
    RecyclerView.Adapter mAdapter, menuAdapter, albumAdapter;
    RecyclerView.LayoutManager mlayoutManager, menulayoutManager, albumLayoutManager;
    TextView txt_name, txt_diachi, txt_gio, txt_gia, txt_vesinh, txt_phucvu, txt_sdt, txt_tien;
    LinearLayout btn_themanh, btn_dangreview;
    ArrayList<Post> postlist;
    ValueEventListener locationValueEventListener;
    ChildEventListener postChildEventListener, locaMenuChildEventListener, imageChildEventListener;
    ActionBar actionBar;
    Toolbar toolbar;
    MyLocation location;
    StorageReference storageRef;
    ArrayList<Food> foodList;
    ArrayList<Image> files;
    String fileName;
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    boolean isConnected=false;
    IntentFilter mIntentFilter;
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(mBroadcastSendAddress)) {
                Log.i(LOG+".onReceive form Service","isConnected= "+ intent.getBooleanExtra("isConnected", false));
                if (intent.getBooleanExtra("isConnected", false)) {
                    if(!isConnected)
                        getData();
                    isConnected = true;
                } else
                    isConnected = false;
            }
        }
    };
    public LocadetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        isConnected= MyService.returnIsConnected();
        if(!isConnected){
            Toast.makeText(getContext(),"Offline mode",Toast.LENGTH_SHORT).show();
        }
        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        getContext().registerReceiver(broadcastReceiver,mIntentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locadetail, container, false);
        isConnected= MyService.returnIsConnected();
        locaID = ChooseLoca.getInstance().getLocaID();
        tinh = ChooseLoca.getInstance().getTinh();
        huyen = ChooseLoca.getInstance().getHuyen();
        fileName = ChooseLoca.getInstance().getInfo();
        Log.d(LOG + ".onCreateView", "fileName="+fileName);
        Log.d(LOG + ".onCreateView", locaID);
        if (tinh != null && huyen != null && locaID != null) {
            andxa(view);
            if (!isConnected) {
                Log.d(LOG + ".onCreateView", locaID);
                //Toast.makeText(getContext(), "Offline mode", Toast.LENGTH_LONG).show();
                if (Storage.readFile(getContext(), fileName) != null) {
                    ArrayList<MyLocation> locations;
                    String a = Storage.readFile(getContext(), fileName);
                    if (a != null) {
                        locations = Storage.readJSONMyLocation(a);
                        if (locations.size() > 0) {
                            for (MyLocation mLocation : locations) {
                                if (mLocation.getLocaID().equals(locaID)) {
                                    location=mLocation;
                                    location.setQuanhuyen(LoginSession.getInstance().getHuyen());
                                    location.setTinhtp(LoginSession.getInstance().getTinh());
                                    String gio = location.getTimestart() + " - " + location.getTimeend();
                                    String tenquan = location.getName();
                                    String diachi = location.getDiachi();
                                    String sdt = location.getSdt();
                                    txt_sdt.setText(sdt);
                                    txt_diachi.setText(diachi);
                                    txt_tien.setText(String.valueOf(location.getGiamin()) +
                                            " - " + String.valueOf(location.getGiamax()));
                                    txt_gio.setText(gio);
                                    txt_name.setText(tenquan);
                                    try {
                                        txt_gia.setText(String.valueOf(location.getGiaAVG() + ""));
                                        txt_vesinh.setText(String.valueOf(location.getVsAVG() + ""));
                                        txt_phucvu.setText(String.valueOf(location.getPvAVG() + ""));
                                    } catch (ArithmeticException mess) {
                                        txt_gia.setText(String.valueOf(0));
                                        txt_vesinh.setText(String.valueOf(0));
                                        txt_phucvu.setText(String.valueOf(0));
                                    }
                                }
                            }
                        }
                    }
                }
                if (Storage.readFile(getContext(), "postlist_" + 1 + "_" + tinh + "_" + huyen) != null) {
                    String a = Storage.readFile(getContext(), "postlist_" + 1 + "_" + tinh + "_" + huyen);
                    ArrayList<Post> posts = null;
                    if (a != null) {
                        try {
                            // posts = Storage.readJSONPost(a);
                            posts = new ArrayList<>();
                            posts = Storage.readJSONPost1(a);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (posts.size() > 0) {
                            for (Post post : posts) {
                                if (post.getLocaID().equals(locaID)) {
                                    postlist.add(post);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
                return view;
            } else {
                getData();
                return view;
            }
        }


        return null;
    }
    public void getData(){
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        storageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(getResources().getString(R.string.firebaseStorage_path));
        locationValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                location = dataSnapshot.getValue(MyLocation.class);
                location.setLocaID(dataSnapshot.getKey());
                location.setQuanhuyen(LoginSession.getInstance().getHuyen());
                location.setTinhtp(LoginSession.getInstance().getTinh());
                String gio = location.getTimestart() + " - " + location.getTimeend();
                String tenquan = location.getName();
                String diachi = location.getDiachi();
                String sdt = location.getSdt();
                txt_sdt.setText(sdt);
                txt_diachi.setText(diachi);
                txt_tien.setText(String.valueOf(location.getGiamin()) +
                        " - " + String.valueOf(location.getGiamax()));
                txt_gio.setText(gio);
                txt_name.setText(tenquan);
                try {
                    txt_gia.setText(String.valueOf(location.getGiaAVG() + ""));
                    txt_vesinh.setText(String.valueOf(location.getVsAVG() + ""));
                    txt_phucvu.setText(String.valueOf(location.getPvAVG() + ""));
                } catch (ArithmeticException mess) {
                    txt_gia.setText(String.valueOf(0));
                    txt_vesinh.setText(String.valueOf(0));
                    txt_phucvu.setText(String.valueOf(0));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        postChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                post.setPostID(dataSnapshot.getKey());
                postlist.add(post);
                CalcuAVGRate newcalcu = new CalcuAVGRate(postlist);

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
        locaMenuChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Food food = dataSnapshot.getValue(Food.class);
                food.setMonID(dataSnapshot.getKey());
                foodList.add(food);
                menuAdapter.notifyDataSetChanged();
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
        imageChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
//                    Toast.makeText(getActivity(), dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("checkListenerFromImages", "have changed");
                    final Image image = dataSnapshot.getValue(Image.class);
                    image.setImageID(dataSnapshot.getKey());
                    storageRef.child(getString(R.string.images_CODE) + "/"
                            + image.getName())
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            image.setPath(uri);
                            files.add(image);
                            albumAdapter.notifyDataSetChanged();
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
        Log.d(getResources().getString(R.string.key_CODE), locaID);
        dbRef.child(tinh + "/" + huyen + "" + "/"
                + getString(R.string.locations_CODE) + locaID).addValueEventListener(locationValueEventListener);
        dbRef.child(tinh + "/" + huyen + "" + "/" +
                getResources().getString(R.string.posts_CODE)).orderByChild("locaID").equalTo(locaID)
                .addChildEventListener(postChildEventListener);
        dbRef.child(tinh + "/" + huyen + "" + "/" +
                getResources().getString(R.string.thucdon_CODE)).orderByChild("locaID")
                .equalTo(locaID).addChildEventListener(locaMenuChildEventListener);
        dbRef.child(getResources().getString(R.string.images_CODE))
                .orderByChild("locaID").equalTo(locaID).limitToFirst(3)
                .addChildEventListener(imageChildEventListener);
    }
    private void andxa(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.frg_locadetial_toolbar);
        txt_tien = (TextView) view.findViewById(R.id.frg_lcdetail_txt_tien);
        txt_diachi = (TextView) view.findViewById(R.id.frg_lcdetail_txt_diachi);
        txt_name = (TextView) view.findViewById(R.id.frg_lcdetail_txt_tenquan);
        txt_gio = (TextView) view.findViewById(R.id.frg_lcdetail_txt_thoigian);
        txt_gia = (TextView) view.findViewById(R.id.frg_lcdetail_txt_gia);
        txt_vesinh = (TextView) view.findViewById(R.id.frg_lcdetail_txt_vesinh);
        txt_phucvu = (TextView) view.findViewById(R.id.frg_lcdetail_txt_phucvu);
        btn_themanh = (LinearLayout) view.findViewById(R.id.frg_lcdetail_btn_themanh);
        btn_dangreview = (LinearLayout) view.findViewById(R.id.frg_lcdetail_dangreview);
        txt_sdt = (TextView) view.findViewById(R.id.frg_lcdetail_txt_sdt);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Location Detail");
        setHasOptionsMenu(true);
        postlist = new ArrayList<Post>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_lcdetail_rcyler_review);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mlayoutManager = linearLayoutManager;
        mRecyclerView.setLayoutManager(mlayoutManager);
        mAdapter = new Reviewlist_rcyler_adapter(postlist, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        menuRecyclerView = (RecyclerView) view.findViewById(R.id.frg_lcdetail_rcyler_dsMon);
//        LinearLayoutManager linearLayoutManager1 =
//                new LinearLayoutManager(getActivity(),
//                        LinearLayoutManager.HORIZONTAL, false);
        menulayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        menuRecyclerView.setLayoutManager(menulayoutManager);
        foodList = new ArrayList<>();
        menuAdapter = new Thucdon_rcyler_adapter(foodList, getActivity());
        menuRecyclerView.setAdapter(menuAdapter);
        albumRecyclerView = (RecyclerView) view.findViewById(R.id.frg_lcdetail_rcyler_album);
        albumLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        albumRecyclerView.setLayoutManager(albumLayoutManager);
        files = new ArrayList<>();
        albumAdapter = new Photos_rcyler_adapter(files, getActivity());
        albumRecyclerView.setAdapter(albumAdapter);

        btn_dangreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected) {
                    if (LoginSession.getInstance().getUserID() == null) {
                        Toast.makeText(getActivity(), getString(R.string.txt_needlogin),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        DoPost.getInstance().setMyLocation(location);
                        Intent intent = new Intent(getActivity(), Adapter2Activity.class);
                        intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frag_addpost_CODE));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity().getApplicationContext()
                , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isConnected) {
                    if (LoginSession.getInstance().getUserID() == null) {
                        Toast.makeText(getActivity(), getString(R.string.txt_needlogin),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getActivity(), Adapter2Activity.class);
                        intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frg_viewpost_CODE));
                        ChoosePost.getInstance().setPostID(postlist.get(position).getPostID());
                        ChoosePost.getInstance().setTinh(tinh);
                        ChoosePost.getInstance().setHuyen(huyen);
                        startActivity(intent);
                    }
                } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.option_menu_locadetail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    getActivity().finish();
                    return true;
                case R.id.menu_locadetail_themmon:
                    if (isConnected) {
                    if (LoginSession.getInstance().getUserID() == null) {
                    } else {
                        Intent intent = new Intent(getActivity(), Adapter2Activity.class);
                        intent.putExtra(getResources().getString(R.string.fragment_CODE),
                                getResources().getString(R.string.frg_themmon_CODE));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if(location!=null) {
                            ChooseLoca.getInstance().setLocaID(location.getLocaID());
                            ChooseLoca.getInstance().setName(location.getName());
                            ChooseLoca.getInstance().setAddress(location.getDiachi());
                            ChooseLoca.getInstance().setTinh(tinh);
                            ChooseLoca.getInstance().setHuyen(huyen);
                            startActivity(intent);
                        }
                        else {
                            ConnectionDetector.showLoadingAlert(getContext());
                        }
                    }
                    return true;
                    } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
                case R.id.menu_locadetail_report:
                    if (isConnected) {
                    ReportLocal.getInstance().setMyLocation(location);
                    ReportStoreDialogFragment reportStoreDialog = new ReportStoreDialogFragment();
                    reportStoreDialog.show(getActivity().getSupportFragmentManager(), "fragment_reportStore");
                    return true;
                    } else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
                default:
                    return super.onOptionsItemSelected(item);
            }
    }



    @Override
    public void onDetach() {
        super.onDetach();
//        ChooseLoca.getInstance().setLocaID(null);
        ChooseLoca.getInstance().setName(null);
        ChooseLoca.getInstance().setAddress(null);
        ChoosePost.getInstance().setPostID(null);
        ChoosePost.getInstance().setTinh(null);
        ChoosePost.getInstance().setHuyen(null);
    }
}
