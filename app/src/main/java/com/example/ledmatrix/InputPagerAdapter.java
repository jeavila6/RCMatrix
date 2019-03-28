package com.example.ledmatrix;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class InputPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment[] fragments;
    private String[] pageTitles;

    InputPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments = new Fragment[] {
                new PhotoFragment(),
                new DrawFragment(),
                new TextFragment()
        };

        pageTitles = new String[] {
                "Photo",
                "Draw",
                "Text"
        };

    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }
}
