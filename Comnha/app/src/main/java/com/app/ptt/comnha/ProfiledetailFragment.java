package com.app.ptt.comnha;


import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.app.ptt.comnha.Adapters.FragmentPagerAdapter;
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.app.ptt.comnha.SingletonClasses.LoginSession;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfiledetailFragment extends Fragment {
    private CollapsingToolbarLayout collapsLayout;
    Toolbar mToolbar;
    ViewPager viewPager;
    TabLayout tabLayout;

    public ProfiledetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profiledetail, container, false);
        anhxa(view);
        return view;
    }

    void anhxa(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.frag_prodetail_toolbar);
        viewPager = (ViewPager) view.findViewById(R.id.frag_prodetail_viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.frag_prodetail_tablayout);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(LoginSession.getInstance().getUsername().trim() + "'s profile");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setShowHideAnimationEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        collapsLayout = (CollapsingToolbarLayout) view.findViewById(R.id.frag_prodetail_collapsing_toolbar);
        collapsLayout.setTitle(LoginSession.getInstance().getUsername());
        viewPager.setAdapter(
                new FragmentPagerAdapter(activity.getSupportFragmentManager(), activity.getApplicationContext())
        );
        tabLayout.setupWithViewPager(viewPager);
//        dynamicToolbarColor();
//        toolbarTextApperance();
    }

    private void toolbarTextApperance() {
//        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.ic_logo);
//        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
//            @Override
//            public void onGenerated(Palette palette) {
//                collapsLayout.setContentScrimColor(palette.getMutedColor(R.color.colorPrimary));
//                collapsLayout.setStatusBarScrimColor(palette.getMutedColor(R.color.colorPrimaryDark));
//            }
//        });
    }

    private void dynamicToolbarColor() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ChooseLoca.getInstance().setLocaID(null);
        ChooseLoca.getInstance().setName(null);
        ChooseLoca.getInstance().setAddress(null);
    }
}
