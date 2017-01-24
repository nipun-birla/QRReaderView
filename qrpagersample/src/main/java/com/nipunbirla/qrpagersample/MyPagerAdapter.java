package com.nipunbirla.qrpagersample;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nipun on 1/24/2017.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 3;
    Map<Integer, Fragment> mFragmentMap = new HashMap<>();


    public MyPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                QRFragment fragment = QRFragment.newInstance();
                mFragmentMap.put(0, fragment);
                return fragment;
            case 1: // Fragment # 0 - This will show FirstFragment different title
                DummyFragment dummyFragment = DummyFragment.newInstance(1, "Page # 2");
                mFragmentMap.put(1, dummyFragment);
                return dummyFragment;
            case 2: // Fragment # 1 - This will show SecondFragment
                DummyFragment dummyFragment2 = DummyFragment.newInstance(2, "Page # 3");
                mFragmentMap.put(2, dummyFragment2);
                return dummyFragment2;
            default:
                return null;
        }
    }

    public Fragment getInstantiatedFragment(int pos){
        return mFragmentMap.get(pos);
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

}
