package com.example.aman.login;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.aman.login.Chats;
import com.example.aman.login.Group;
import com.example.aman.login.Notics;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                Chats chats = new Chats();
                return chats;
            case 1:
                Group group = new Group();
                return group;
            case 2:
                Notics notics = new Notics();
                return notics;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats";
            case 1:
                return "Group";
            case 2:
                return "Notics";
            default:
                return null;
        }


    }
}
