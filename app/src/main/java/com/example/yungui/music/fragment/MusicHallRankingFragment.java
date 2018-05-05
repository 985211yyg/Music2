package com.example.yungui.music.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yungui.music.MainActivity;
import com.example.yungui.music.MainFragment;
import com.example.yungui.music.R;
import com.example.yungui.music.adapter.MyRankingItemAdapter;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.model.Music;
import com.example.yungui.music.utils.FragmentUtils;
import com.example.yungui.music.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MusicHallRankingFragment extends BaseFragment {
    public static final String Fragment_Tag = "MusicHallRankingFragment";
    private static final String TAG = "MusicHallRankingFragmen";
    @BindView(R.id.ranking_detail_title)
    TextView mRankingDetailTitle;
    @BindView(R.id.ranking_detail_toolBar)
    Toolbar mRankingDetailToolBar;
    @BindView(R.id.ranking_detail_recyclerView)
    RecyclerView mRankingDetailRecyclerView;
    @BindView(R.id.ranking_detail_loading)
    LinearLayout mLoading;

    private MusicDetailFragment mMusicDetailFragment;
    private MyRankingItemAdapter mMyRankingItemAdapter;
    private List<Music> mMusics = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public MusicHallRankingFragment() {

    }

    public static MusicHallRankingFragment newInstance() {
        MusicHallRankingFragment fragment = new MusicHallRankingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getLayoutID() {
        return R.layout.fragment_music_hall_ranking;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setSupportActionBar(mRankingDetailToolBar);
        ((MainActivity) getActivity()).setDisplayHomeAsUpEnabled(true);
        mRankingDetailToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
        mMyRankingItemAdapter = new MyRankingItemAdapter(R.layout.fragment_music_hall_ranking_item, null);
        mRankingDetailRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRankingDetailRecyclerView.setAdapter(mMyRankingItemAdapter);
        mMyRankingItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mMusicDetailFragment = MusicDetailFragment.newInstance(null, null);
                FragmentUtils.addToMainContent(getFragmentManager(),
                        mMusicDetailFragment,
                        MusicDetailFragment.Fragment_Tag,
                        MusicHallRankingFragment.Fragment_Tag);
            }
        });
        mLoading.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoading.setVisibility(View.GONE);
                loadData();
            }
        }, 400);

    }

    @Override
    protected void loadData() {
        mMusics = JsonUtils.paresMusicFromAssetsSource("musics.json");
        if (mMusics != null && mMusics.size() != 0) {
            mMyRankingItemAdapter.addData(mMusics);
            mMyRankingItemAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
