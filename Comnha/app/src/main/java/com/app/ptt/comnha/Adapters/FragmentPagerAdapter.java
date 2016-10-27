package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.app.ptt.comnha.ActivityFragment;
import com.app.ptt.comnha.PhotoFragment;
import com.app.ptt.comnha.ProfileFragment;

/**
 * Created by PTT on 10/22/2016.
 */

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabtitles[] = new String[]{"Profile", "Activities", "Photos"};
    private Context context;

    public FragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ProfileFragment();
            case 1:
                return new ActivityFragment();
            case 2:
                return new PhotoFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}
