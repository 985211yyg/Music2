package com.example.yungui.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupWindow;

import com.example.yungui.music.adapter.MainViewPagerAdapter;
import com.example.yungui.music.base.BaseActivity;
import com.example.yungui.music.fragment.BottomControlBarFragment;
import com.example.yungui.music.fragment.FindFragment;
import com.example.yungui.music.fragment.MusicBarFragment;
import com.example.yungui.music.fragment.MyFragment;
import com.example.yungui.music.modle.Song;
import com.example.yungui.music.service.MusicService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {


    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    public static final String TAG = "MainActivity";

    @Override
    protected void setTransition() {
//        Transition explode = TransitionInflater.from(this).inflateTransition(R.transition.explode);
//        getWindow().setEnterTransition(explode);
//        getWindow().setReenterTransition(explode);
//        getWindow().setExitTransition(explode);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected int getMenuID() {
        return 0;
    }

    /**
     * activity是否支持toolbar
     * @return
     */
    @Override
    protected boolean setToolBar() {
        return false;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initDrawer();
        MainFragment mainFragment = MainFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_container, mainFragment,"mainFragment")
                .commit();

    }

    private void initDrawer() {
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * 可以供fragment调用关联fragment的toolbar
     *
     * @param toolbar
     */
    public void initToolBarEvent(Toolbar toolbar) {
        if (toolbar != null) {
            toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                drawer.openDrawer(Gravity.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 抽屉栏的点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
