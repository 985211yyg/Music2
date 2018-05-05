package com.example.yungui.music;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupWindow;

import com.example.yungui.music.adapter.MainViewPagerAdapter;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.fragment.MusicFindFragment;
import com.example.yungui.music.fragment.MusicHallFragment;
import com.example.yungui.music.fragment.MusicMyFragment;

import java.util.List;

public class MainFragment extends BaseFragment {
    public static final String Fragment_Tag = "MainFragment";

    private ViewPager viewPager;
    private MainViewPagerAdapter mainViewPagerAdapter;
    private TabLayout tabLayout;
    private Toolbar toolbar;

    private String[] tabText = new String[]{"我的", "音乐馆", "发现"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    public MainFragment() {

    }


    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        toolbar = rootView.findViewById(R.id.toolbar);
        //toolbar与drawer联动
        ((MainActivity) getActivity()).initToolBarEvent(toolbar);
        //是的fragment支持toolbar,然后可以设置menu
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        tabLayout = rootView.findViewById(R.id.tabLayout);
        viewPager = rootView.findViewById(R.id.main_ViewPager);
        mainViewPagerAdapter = new MainViewPagerAdapter(getActivity().getSupportFragmentManager(), tabText);
        mainViewPagerAdapter.addFragment(MusicMyFragment.newInstance());
        mainViewPagerAdapter.addFragment(MusicHallFragment.newInstance());
        mainViewPagerAdapter.addFragment(MusicFindFragment.newInstance());
        viewPager.setAdapter(mainViewPagerAdapter);
        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void loadData() {

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.more) {
            PopupWindow popupWindow = new PopupWindow(mContext);
            popupWindow.setContentView(LayoutInflater.from(mContext).inflate(R.layout.pup_more, null));
            popupWindow.setOutsideTouchable(false);
            popupWindow.setBackgroundDrawable(mContext.getDrawable(R.drawable.pup_background));
            popupWindow.setElevation(1);
            popupWindow.setFocusable(true);
            popupWindow.showAsDropDown(toolbar, 0, 0, Gravity.RIGHT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
