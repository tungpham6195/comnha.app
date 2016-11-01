package com.app.ptt.comnha;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.app.ptt.comnha.SingletonClasses.DoPost;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by PTT on 10/25/2016.
 */

public class ActivityFragment extends Fragment {
    FirebaseDatabase database;
    DatabaseReference dbRef;
    Button btn_chooseloca;
    TextView txt_locaName;

    public ActivityFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
        Log.d("onCreateViewActFrag", "createdView");
        anhxa(view);
        try {
            Log.d("LocaID", ChooseLoca.getInstance().getLocaID());
        } catch (NullPointerException mess) {

        }

//        switch (whatFilter) {
//            case 1://hiện tất cả các review (chưa có vị trí xác định)
////                Toast.makeText(getActivity(), "none", Toast.LENGTH_SHORT).show();
//                dbRef.child(getResources().getString(R.string.userpost_CODE)
//                        + "/" + LoginSession.getInstance().getUserID()).addChildEventListener(userPostEventListener);
//                break;
//            case 2://hiện tất cả review theo vị trí xác định
////                Toast.makeText(getActivity(), ChooseLoca.getInstance().getLocaID(), Toast.LENGTH_SHORT).show();
//                dbRef.child(getResources().getString(R.string.locauserpost_CODE)
//                        + "/" + ChooseLoca.getInstance().getLocaID() + "/" +
//                        LoginSession.getInstance().getUserID()).addChildEventListener(userPostEventListener);
//                break;
//        }
        if (view.findViewById(R.id.frg_activity_frame_adapter) != null) {
            if (getActivity().getSupportFragmentManager().findFragmentById(R.id.frg_activity_frame_adapter) == null) {
                ActListFragment actListFragment = new ActListFragment();
                actListFragment.setFilter(1);
                actListFragment.setArguments(getActivity().getIntent().getExtras());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frg_activity_frame_adapter, actListFragment)
                        .commit();
            }
        }
        return view;
    }

    void anhxa(View view) {
        txt_locaName = (TextView) view.findViewById(R.id.frg_activity_txtLoca);
//        postList = new ArrayList<>();
//        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_activity_recyler);
//        mRecyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
//        mlayoutManager = linearLayoutManager;
//        mRecyclerView.setLayoutManager(mlayoutManager);
//        mAdapter = new Reviewlist_rcyler_adapter(postList, getActivity());
//        mRecyclerView.setAdapter(mAdapter);
        try {
            if (!ChooseLoca.getInstance().getName().equals("")) {
                txt_locaName.setText(ChooseLoca.getInstance().getName());
            }
        } catch (NullPointerException mess) {
            Log.e("nullLocaName", mess.getMessage());
        }
        final PopupMenu popup = new PopupMenu(getActivity(), txt_locaName, Gravity.START);
        popup.getMenuInflater().inflate(R.menu.popup_menu_view_activity, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_activity_none:
                        ChooseLoca.getInstance().setLocaID("");

                        break;
                    case R.id.action_activity_choseLoca:

                        break;
                }
                return true;
            }
        });
        btn_chooseloca = (Button) view.findViewById(R.id.frg_activity_btnChoseloca);
        btn_chooseloca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                popup.show();
            }
        });
        txt_locaName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });
//        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                String key = postList.get(position).getPostID();
//                Intent intent = new Intent(getActivity().getApplicationContext(), AdapterActivity.class);
//                intent.putExtra(getResources().getString(R.string.fragment_CODE),
//                        getResources().getString(R.string.frg_viewpost_CODE));
//                intent.putExtra(getResources().getString(R.string.key_CODE), key);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getActivity().getApplicationContext().startActivity(intent);
//            }
//        }));
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("onStopFromActFrag", "Stoped");
        DoPost.getInstance().setMyLocation(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
