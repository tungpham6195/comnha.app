package com.app.ptt.comnha;


import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.app.ptt.comnha.FireBase.FoodCategory;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PickFoodCategoDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    Button btn_addnewCatogo;
    ListView listView;
    DatabaseReference dbRef;
    ChildEventListener foodCategoryChildEventListener;
    PickFoodCategoryListener pickFoodCategoryListener;
    ArrayAdapter<String> mAdapter;
    ArrayList<FoodCategory> foodCategories;
    ArrayList<String> foodStrings;
    AddFoodCategoryDialogFragment addFoodCategoryDialogFragment;
    FragmentManager dialogFm;

    public interface PickFoodCategoryListener {
        void onPickFoodCategory(FoodCategory foodCategory);
    }

    public void setFoodCategoryChildEventListener(PickFoodCategoryListener listener) {
        pickFoodCategoryListener = listener;
    }

    public PickFoodCategoDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pick_food_catego_dialog, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_path));
        anhxa(view);
        foodCategoryChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FoodCategory foodCategory = dataSnapshot.getValue(FoodCategory.class);
                foodCategory.setFoodCategoryID(dataSnapshot.getKey());
                foodCategories.add(foodCategory);
                foodStrings.add(foodCategory.getName());
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
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.child(getString(R.string.foodcategory_CODE))
                .addChildEventListener(foodCategoryChildEventListener);
        return view;
    }

    private void anhxa(View view) {
        dialogFm = getActivity().getSupportFragmentManager();
        addFoodCategoryDialogFragment = new AddFoodCategoryDialogFragment();
        btn_addnewCatogo = (Button) view.findViewById(R.id.frg_chosefCatego_btnaddNew);
        listView = (ListView) view.findViewById(R.id.frg_chosefCatego_listV);
        foodCategories = new ArrayList<>();
        foodStrings = new ArrayList<>();
        mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, foodStrings);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        btn_addnewCatogo.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        pickFoodCategoryListener.onPickFoodCategory(foodCategories.get(position));
        dismiss();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(getResources().getString(R.string.text_choosefoodcateg));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_chosefCatego_btnaddNew:
                addFoodCategoryDialogFragment.show(dialogFm, "fragment_addFoodCategory");
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dbRef.removeEventListener(foodCategoryChildEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }
}
