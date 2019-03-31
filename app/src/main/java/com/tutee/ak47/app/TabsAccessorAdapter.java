package com.tutee.ak47.app;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {

            case 0:
                GroupFragment group = new GroupFragment();
                return group;
            case 1:
                ContactsFragment feeds = new ContactsFragment();
                return feeds;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Discussion";
            case 1:
                return "Chats";
            default:
                return null;
        }


    }
}
