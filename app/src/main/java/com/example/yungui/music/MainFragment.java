package com.example.yungui.music;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.example.yungui.music.adapter.MainViewPagerAdapter;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.fragment.FindFragment;
import com.example.yungui.music.fragment.MusicBarFragment;
import com.example.yungui.music.fragment.MyFragment;

public class MainFragment extends BaseFragment {

    private ViewPager viewPager;
    private MainViewPagerAdapter mainViewPagerAdapter;
    private TabLayout tabLayout;
    private Toolbar toolbar;

    private String[] tabText = new String[]{"我的", "音乐馆", "发现"};
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


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
    protected void initView() {
        toolbar = rootView.findViewById(R.id.toolbar);
        //toolbar与drawer联动
        ((MainActivity)getActivity()).initToolBarEvent(toolbar);
        //是的fragment支持toolbar,然后可以设置menu
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        tabLayout = rootView.findViewById(R.id.tabLayout);
        viewPager = rootView.findViewById(R.id.main_ViewPager);
        mainViewPagerAdapter = new MainViewPagerAdapter(getActivity().getSupportFragmentManager(), tabText);
        mainViewPagerAdapter.addFragment(MyFragment.newInstance());
        mainViewPagerAdapter.addFragment(MusicBarFragment.newInstance());
        mainViewPagerAdapter.addFragment(FindFragment.newInstance());
        viewPager.setAdapter(mainViewPagerAdapter);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

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
