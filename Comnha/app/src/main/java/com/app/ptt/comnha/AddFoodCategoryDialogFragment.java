package com.app.ptt.comnha;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.FoodCategory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFoodCategoryDialogFragment extends DialogFragment implements View.OnClickListener {
    Button btn_addnew, btn_cancel;
    EditText edt_cateName;
    DatabaseReference dbRef;
    OnDismissListener onDismissListener;
    ProgressDialog progressDialog;

    public interface OnDismissListener {
        void onAddComplete(boolean isComplete);
    }

    public void setOnDismissListener(OnDismissListener listener) {
        onDismissListener = listener;
    }

    public AddFoodCategoryDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_food_category_dialog, container, false);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_path));
        anhxa(view);
        return view;
    }

    private void anhxa(View view) {

        btn_addnew = (Button) view.findViewById(R.id.frg_addfcategory_btnaddnew);
        btn_cancel = (Button) view.findViewById(R.id.frg_addfcategory_btncancel);
        edt_cateName = (EditText) view.findViewById(R.id.frg_addfcategory_edtname);
        btn_addnew.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(getResources().getString(R.string.text_addfoodCate));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_addfcategory_btnaddnew:

                if (edt_cateName.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(),
                            getString(R.string.txt_noFoodCateName), Toast.LENGTH_SHORT).show();
                } else {

                    String key = dbRef.child(getString(R.string.foodcategory_CODE)).push().getKey();
                    FoodCategory newFoodCategory = new FoodCategory();
                    newFoodCategory.setName(edt_cateName.getText().toString());
                    Map<String, Object> foodCateValue = newFoodCategory.topMap();
                    Map<String, Object> updateChild = new HashMap<>();
                    updateChild.put(getString(R.string.foodcategory_CODE) + key, foodCateValue);
                    progressDialog = ProgressDialog.show(getContext(), getString(R.string.txt_plzwait)
                            , getString(R.string.txt_addinfoodcate), true, false);
                    dbRef.updateChildren(updateChild).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                Toast.makeText(getContext(),
                                        getResources().getString(R.string.text_addnewFoodCate_successful), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                onDismissListener.onAddComplete(true);
                                getDialog().dismiss();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(),
                                        getString(R.string.txt_failaddfoodcate),
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                break;
            case R.id.frg_addfcategory_btncancel:
                dismiss();
                break;
        }
    }
}
