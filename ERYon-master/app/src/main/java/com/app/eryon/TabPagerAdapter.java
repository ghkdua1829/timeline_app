package com.app.eryon;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabPagerAdapter  extends FragmentStatePagerAdapter {

    // Count number of tabs
    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        // Returning the current tabs
        switch (position) {
            case 0:
                fragment_timeline timeline = new fragment_timeline();
                return timeline;
            case 1:
                Fragment_Friend fragmentFriend = new Fragment_Friend();
                return fragmentFriend;
            case 2:
                Fragment_Post FragmentPost= new Fragment_Post();
                return FragmentPost;
            case 3:
                Fragment_Alarm FragmentAlarm = new Fragment_Alarm();
                return FragmentAlarm;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
