package com.example.yungui.music.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.yungui.music.MainActivity;
import com.example.yungui.music.R;
import com.example.yungui.music.adapter.ViewPagerAdapter;
import com.example.yungui.music.base.BaseFragment;

import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MusicMyLocalFragment extends BaseFragment{
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.local_viewPager)
    ViewPager viewPager;
    @BindView(R.id.local_tabLayout)
    TabLayout tabLayout;

    private ViewPagerAdapter viewPagerAdapter;


    public MusicMyLocalFragment() {

    }

    public static MusicMyLocalFragment newInstance() {
        MusicMyLocalFragment fragment = new MusicMyLocalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_local;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(MusicFragment.newInstance());
        viewPagerAdapter.addFragment(ArtistFragment.newInstance());
        viewPagerAdapter.addFragment(AlbumFragment.newInstance());
        viewPagerAdapter.addFragment(FolderFragment.newInstance());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_local, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_local_more) {
            PopupDialogFragment.newInstance().show(getFragmentManager(),"popup");
            return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
