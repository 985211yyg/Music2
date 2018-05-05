package com.example.yungui.music.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.yungui.music.MainApplication;
import com.example.yungui.music.R;

/**
 * Created by 22892 on 2018/2/1.
 */

public class FragmentUtils {
    /**
     * fragment的切换
     *
     * @param showFragment
     * @param showTag
     * @param hideTag
     */
    public static void addToMainContent(FragmentManager fragmentManager, Fragment showFragment, String showTag, String hideTag) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.right_enter,
                R.anim.left_out,
                R.anim.left_enter,
                R.anim.right_out);
        fragmentTransaction
                .hide(fragmentManager.findFragmentByTag(hideTag))
                .add(R.id.main_container, showFragment, showTag)
                .addToBackStack(hideTag)
                .commit();
    }
}
