package com.example.yungui.music.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.yungui.music.MainActivity;
import com.example.yungui.music.R;
import com.example.yungui.music.adapter.PlayListCategoryAdapter;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.model.PlayListCategory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yungui on 2017/12/7.
 */

public class MusicHallCategoryFragment extends BaseFragment {
    public static final String Fragment_Tag = "MusicHallCategoryFragment";
    @BindView(R.id.hall_category_toolbar)
    Toolbar toolbar;
    @BindView(R.id.category_recyclerView)
    RecyclerView recyclerView;
    private PlayListCategoryAdapter playListCategoryAdapter;
    private List<PlayListCategory> categories = new ArrayList<>();

    public static MusicHallCategoryFragment newInstance(String data) {
        Bundle args = new Bundle();
        args.putString(Fragment_Tag,data);
        MusicHallCategoryFragment fragment = new MusicHallCategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int getLayoutID() {
        return R.layout.fragment_music_music_hall_category;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ButterKnife.bind(MusicHallCategoryFragment.this, rootView);
        setHasOptionsMenu(true);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        for (int i = 0; i < 11; i++) {
            PlayListCategory playListCategory= new PlayListCategory();
            if (i == 0) {
                playListCategory.setItemType(PlayListCategory.CategoryBig);
                playListCategory.setCategory("轻音乐");
            } else {
                playListCategory.setItemType(PlayListCategory.CategoryNormal);
                playListCategory.setCategory("轻音乐");
            }
            categories.add(playListCategory);
        }
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        playListCategoryAdapter = new PlayListCategoryAdapter(categories);
        recyclerView.setAdapter(playListCategoryAdapter);
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_music_hall_category,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
