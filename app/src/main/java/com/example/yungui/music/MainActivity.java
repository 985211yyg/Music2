package com.example.yungui.music;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import com.example.yungui.music.base.BaseActivity;
import com.example.yungui.music.fragment.BottomControlBarFragment;

import java.util.List;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static final String TAG = "MainActivity";

    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    /**
     * activity是否支持toolbar
     *
     * @return
     */
    @Override
    public boolean setToolBar() {
        return false;
    }

    @Override
    public void onPlayBackServiceConnected(@NonNull MediaControllerCompat mediaControllerCompat) {

    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {

    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {

    }

    @Override
    public void onMediaItemsLoaded(List<MediaBrowserCompat.MediaItem> mediaItems) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initDrawer();
        MainFragment mainFragment = MainFragment.newInstance();
        BottomControlBarFragment bottomControlBarFragment = BottomControlBarFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.bottom_bar_container, bottomControlBarFragment, BottomControlBarFragment.Fragment_Tag)
                .add(R.id.main_container, mainFragment, MainFragment.Fragment_Tag)
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
