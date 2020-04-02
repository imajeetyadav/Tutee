package com.tutee.ak47.app.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tutee.ak47.app.fragment.ContactsFragment;
import com.tutee.ak47.app.fragment.GroupFragment;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {

            case 0:
                return new GroupFragment();
            case 1:
                return new ContactsFragment();
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
