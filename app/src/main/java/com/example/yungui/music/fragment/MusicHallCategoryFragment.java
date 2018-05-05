package com.example.yungui.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.yungui.music.MainActivity;
import com.example.yungui.music.R;
import com.example.yungui.music.adapter.PlayListCategoryAdapter;
import com.example.yungui.music.adapter.PlayListCategorySuggestAdapter;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.model.PlayList;
import com.example.yungui.music.model.PlayListCategory;
import com.example.yungui.music.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yungui on 2017/12/7.
 */

public class MusicHallCategoryFragment extends BaseFragment {
    private static final String TAG = "MusicHallCategoryFragment";
    public static final String Fragment_Tag = "MusicHallCategoryFragment";
    @BindView(R.id.hall_category_toolbar)
    Toolbar toolbar;
    @BindView(R.id.category_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.suggest_category_recyclerView)
    RecyclerView suggestRecyclerView;

    private PlayListCategoryAdapter playListCategoryAdapter;
    private PlayListCategorySuggestAdapter mPlayListCategorySuggestAdapter;
    private List<PlayListCategory> categories = new ArrayList<>();
    private List<PlayList> mPlayLists = new ArrayList<>();

    public static MusicHallCategoryFragment newInstance(String data) {
        Bundle args = new Bundle();
        args.putString(Fragment_Tag, data);
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
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
        loadData();

    }

    @Override
    protected void loadData() {
        for (int i = 0; i < 11; i++) {
            PlayListCategory playListCategory = new PlayListCategory();
            if (i == 0) {
                playListCategory.setItemType(PlayListCategory.CategoryBig);
                playListCategory.setCategory("轻音乐");
            } else {
                playListCategory.setItemType(PlayListCategory.CategoryNormal);
                playListCategory.setCategory("轻音乐");
            }
            categories.add(playListCategory);
        }
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        playListCategoryAdapter = new PlayListCategoryAdapter(categories);
        recyclerView.setAdapter(playListCategoryAdapter);
        mPlayLists = JsonUtils.paresPlayListFromAssetsSource("playlist.json");
        suggestRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        if (mPlayLists != null && mPlayLists.size() > 0) {
            Log.e(TAG, "loadData: "+mPlayLists.size() );
            mPlayListCategorySuggestAdapter = new PlayListCategorySuggestAdapter(R.layout.fragment_music_music_hall_category_item, mPlayLists);
            suggestRecyclerView.setAdapter(mPlayListCategorySuggestAdapter);
        }


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
        inflater.inflate(R.menu.menu_music_hall_category, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
