package com.needapps.birds.birdua;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * FragmentPagerAdapter is used when you have a limited number of tabs that want to swipe through.
 */
public class PageAdapter extends FragmentPagerAdapter {
    // need to define a variable that hold the size of Android TabLayout tabs and update the constructor to include that variable.
    private final List<Fragment> lstFragment = new ArrayList<>();
    private final List<String> lstTitles = new ArrayList<>();

    // PageAdapter constructor is used to communicate between this class and MainActivity.java.
    public PageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return lstFragment.get(position);
    }

    // getCount will return the number of tabs that will appear in Android TabLayout.
    @Override
    public int getCount() {
        return lstTitles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return lstTitles.get(position);
    }

    public void AddFragment(Fragment fragment, String title) {
        lstFragment.add(fragment);
        lstTitles.add(title);
    }
}
