package com.app.ptt.comnha;


import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PickLocationBottomSheetDialogFragment extends BottomSheetDialogFragment {
    Toolbar mToolbar;
    AppBarLayout mAppBarLayout;
    ListView listView;
    ArrayAdapter<CharSequence> mAdapter;
    int whatProvince;
    onPickListener onPickListener;

    public void setOnPickListener(onPickListener listener) {
        onPickListener = listener;
    }

    public interface onPickListener {
        void onPicProvince(String province, int position);

        void onPickDistrict(String district);
    }

    public void setWhatProvince(int whatProvince) {
        this.whatProvince = whatProvince;
    }

    public PickLocationBottomSheetDialogFragment() {
        // Required empty public constructor
    }

    private void anhxa(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.frg_pickLoca_bottomsheet_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.black));

//        ActionBar actionBar = activity.getSupportActionBar();
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setShowHideAnimationEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_black_18dp);
        listView = (ListView) view.findViewById(R.id.frg_pickLocaBtmSheetDialog_listV);
        if (getTag().equals("pickProvinceDialog")) {
            mToolbar.setTitle(getString(R.string.text_pickProvince));
            mAdapter = ArrayAdapter.createFromResource(getContext(), R.array.tinhtp, android.R.layout.simple_list_item_1);
        } else if (getTag().equals("pickDistrictDialog")) {
            mToolbar.setTitle(getString(R.string.text_pickDistrict));
            if (whatProvince == 0) {
                mAdapter = ArrayAdapter.createFromResource(getContext(), R.array.hanoi, android.R.layout.simple_list_item_1);
            }
            if (whatProvince == 1) {
                mAdapter = ArrayAdapter.createFromResource(getContext(), R.array.hochiminh, android.R.layout.simple_list_item_1);
            }
        }
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getTag().equals("pickProvinceDialog")) {
                    onPickListener.onPicProvince(mAdapter.getItem(position).toString(), position);
                } else if (getTag().equals("pickDistrictDialog")) {
                    onPickListener.onPickDistrict(mAdapter.getItem(position).toString());
                }
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        setHasOptionsMenu(true);
        View contentView = View.inflate(getContext(), R.layout.fragment_pick_location_bottom_sheet_dialog, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        final CoordinatorLayout.Behavior behavior = params.getBehavior();
        anhxa(contentView);
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                    if (newState == BottomSheetBehavior.STATE_SETTLING)
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            dismiss();
                        }

                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }
    }
}
