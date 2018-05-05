package com.example.yungui.music.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.ColumnLayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.bumptech.glide.Glide;
import com.example.yungui.music.MainFragment;
import com.example.yungui.music.R;
import com.example.yungui.music.adapter.MyDelegateAdapter;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.model.MusicBean;
import com.example.yungui.music.model.PlayList;
import com.example.yungui.music.model.PlayListBean;
import com.example.yungui.music.net.NetConstant;
import com.example.yungui.music.net.RequestController;
import com.example.yungui.music.utils.Constants;
import com.example.yungui.music.utils.JsonUtils;
import com.example.yungui.music.utils.MusicUtils;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * 音乐馆
 */

public class MusicHallFragment extends BaseFragment implements MyDelegateAdapter.OnItemChildClickListener, MyDelegateAdapter.OnItemClickListener {
    public static final String TAG = MusicHallFragment.class.getSimpleName();
    public static final String Fragment_Tag = "MusicHallFragment";
    @BindView(R.id.bannerView)
    MZBannerView mBannerView;
    @BindView(R.id.hall_singer)
    LinearLayout mHallSinger;
    @BindView(R.id.hall_ranking)
    LinearLayout mHallRanking;
    @BindView(R.id.hall_radio)
    LinearLayout mHallRadio;
    @BindView(R.id.hall_songList)
    LinearLayout mHallSongList;
    @BindView(R.id.hall_mv)
    LinearLayout mHallMv;
    @BindView(R.id.hall_album)
    LinearLayout mHallAlbum;
    @BindView(R.id.music_hall_recyclerView)
    RecyclerView mMusicHallRecyclerView;

    private List<String> imgUrls = new ArrayList<>();

    private MyDelegateAdapter adapter_Song_List,
            adapter_Daily_Suggest,
            adapter_New_CD,
            adapter_Special_Radio,
            adapter_Ranking,
            adapter_Musician,

    adapter_Song_List_Header,
            adapter_Daily_Suggest_Header,
            adapter_New_CD_Header,
            adapter_Special_Radio_Header,
            adapter_Ranking_Header,
            adapter_Musician_Header;

    private List<DelegateAdapter.Adapter> adapters = new LinkedList<>();
    private Disposable disposable;
    private String data;

    @Override
    public void onResume() {
        super.onResume();
        mBannerView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBannerView.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static MusicHallFragment newInstance() {
        MusicHallFragment fragment = new MusicHallFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_music_music_hall;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        imgUrls.add(NetConstant.BANNER_IMG_1);
        imgUrls.add(NetConstant.BANNER_IMG_2);
        imgUrls.add(NetConstant.BANNER_IMG_3);
        imgUrls.add(NetConstant.BANNER_IMG_4);
        imgUrls.add(NetConstant.BANNER_IMG_5);

        mBannerView.setPages(imgUrls, new MZHolderCreator() {
            @Override
            public MZViewHolder createViewHolder() {
                return new MyMZViewHolder();
            }
        });
        mBannerView.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
            @Override
            public void onPageClick(View view, int i) {
                switch (MusicUtils.AnalyzeUrl(NetConstant.BANNER_ITEM_HREF[i])) {
                    case MusicUtils.SONG:

                        break;
                    case MusicUtils.MV:

                        break;
                    case MusicUtils.DJRADIO:

                        break;

                }

            }
        });


        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(mContext);
        mMusicHallRecyclerView.setLayoutManager(virtualLayoutManager);

        //歌单推荐
        LinearLayoutHelper song_list_header = new LinearLayoutHelper();
        adapter_Song_List_Header = new MyDelegateAdapter(mContext, song_list_header, 1, null, Constants.SONG_LIST_HEADER);
        PlayListBean playListBean = new PlayListBean();
        ArrayList<PlayListBean> beans1 = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            beans1.add(playListBean);
        }
        GridLayoutHelper song_list = new GridLayoutHelper(3, 6, 1, 3);
        song_list.setPadding(1, 1, 1, 1);
        adapter_Song_List = new MyDelegateAdapter(mContext, song_list, 6, beans1, Constants.SONG_LIST);
        adapter_Song_List_Header.setOnItemChildClickListener(this);
        adapter_Song_List.setOnItemChildClickListener(this);
        adapter_Song_List_Header.setOnItemClickListener(this);
        adapter_Song_List.setOnItemClickListener(this);

        //每日推荐
        LinearLayoutHelper daily_suggest_header = new LinearLayoutHelper();
        adapter_Daily_Suggest_Header = new MyDelegateAdapter(mContext, daily_suggest_header, 1, null, Constants.DAILY_SUGGEST_HEADER);
        MusicBean musicBean2 = new MusicBean();
        ArrayList<MusicBean> beans2 = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            beans2.add(musicBean2);
        }
        LinearLayoutHelper daily_suggest = new LinearLayoutHelper();
        daily_suggest.setPadding(1, 0, 0, 0);
        daily_suggest.setDividerHeight(2);
        adapter_Daily_Suggest = new MyDelegateAdapter(mContext, daily_suggest, 5, beans2, Constants.DAILY_SUGGEST);

        //新歌速递
        LinearLayoutHelper new_cd_header = new LinearLayoutHelper();
        adapter_New_CD_Header = new MyDelegateAdapter(mContext, new_cd_header, 1, null, Constants.NEW_CD_HEADER);
        GridLayoutHelper new_cd = new GridLayoutHelper(3, 3);
        new_cd.setGap(2);
        MusicBean musicBean3 = new MusicBean();
        ArrayList<MusicBean> beans3 = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            beans3.add(musicBean3);
        }
        adapter_New_CD = new MyDelegateAdapter(mContext, new_cd, 3, beans3, Constants.NEW_CD);

        //精选电台
        LinearLayoutHelper special_radio_header = new LinearLayoutHelper();
        adapter_Special_Radio_Header = new MyDelegateAdapter(mContext, special_radio_header, 1, null, Constants.SPECIAL_RADIO_HEADER);
        ColumnLayoutHelper special_radio = new ColumnLayoutHelper();
        special_radio.setItemCount(3);
        special_radio.setMargin(4, 0, 4, 0);
        MusicBean musicBean4 = new MusicBean();
        ArrayList<MusicBean> beans4 = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            beans4.add(musicBean4);
        }
        adapter_Special_Radio = new MyDelegateAdapter(mContext, special_radio, 3, beans4, Constants.SPECIAL_RADIO);

        //排行版
        LinearLayoutHelper ranking_header = new LinearLayoutHelper();
        adapter_Ranking_Header = new MyDelegateAdapter(mContext, ranking_header, 1, null, Constants.RANKING_HEADER);
        MusicBean musicBean5 = new MusicBean();
        ArrayList<MusicBean> beans5 = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            beans5.add(musicBean5);
        }
        GridLayoutHelper ranking = new GridLayoutHelper(2, 6);
        ranking.setGap(2);
        adapter_Ranking = new MyDelegateAdapter(mContext, ranking, 6, beans5, Constants.RANKING);

        //音乐人
        LinearLayoutHelper musician_header = new LinearLayoutHelper();
        adapter_Musician_Header = new MyDelegateAdapter(mContext, musician_header, 1, null, Constants.MUSICIAN_HEADER);
        MusicBean musicBean6 = new MusicBean();
        ArrayList<MusicBean> beans6 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            beans6.add(musicBean6);
        }
        LinearLayoutHelper musician = new LinearLayoutHelper(1, 5);
        musician.setMarginLeft(1);
        adapter_Musician = new MyDelegateAdapter(mContext, musician, 5, beans6, Constants.MUSICIAN);


        adapters.add(adapter_Song_List_Header);
        adapters.add(adapter_Song_List);
        adapters.add(adapter_Daily_Suggest_Header);
        adapters.add(adapter_Daily_Suggest);
        adapters.add(adapter_New_CD_Header);
        adapters.add(adapter_New_CD);
        adapters.add(adapter_Special_Radio_Header);
        adapters.add(adapter_Special_Radio);
        adapters.add(adapter_Ranking_Header);
        adapters.add(adapter_Ranking);
        adapters.add(adapter_Musician_Header);
        adapters.add(adapter_Musician);
        for (DelegateAdapter.Adapter adapter : adapters) {
            ((MyDelegateAdapter) adapter).setOnItemClickListener(this);
            ((MyDelegateAdapter) adapter).setOnItemChildClickListener(this);
        }


        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        //添加多个adapter
        delegateAdapter.setAdapters(adapters);
        mMusicHallRecyclerView.setAdapter(delegateAdapter);
    }


    /**
     * 加载数据
     */
    @Override
    protected void loadData() {
        JsonUtils.getPlayList("playlist.json")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<List<PlayList>>() {
                    //首先更新专辑名字
                    @Override
                    public void accept(List<PlayList> playLists) throws Exception {
                        for (PlayList playList : playLists) {

                        }

                    }
                })
                .flatMap(new Function<List<PlayList>, ObservableSource<List<PlayListBean>>>() {
                    @Override
                    public ObservableSource<List<PlayListBean>> apply(List<PlayList> playLists) throws Exception {
                        final List<PlayListBean> playListBeans = new ArrayList<>();
                        for (PlayList playList : playLists) {
                            RequestController
                                    .getMusicApi()
                                    .getPlayListBean("playlist", playList.getLink())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Observer<PlayListBean>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onNext(PlayListBean value) {
                                            playListBeans.add(value);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.e(TAG, "onError: " + e);

                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });

                        }
                        return Observable.create(new ObservableOnSubscribe<List<PlayListBean>>() {
                            @Override
                            public void subscribe(ObservableEmitter<List<PlayListBean>> e) throws Exception {
                                e.onNext(playListBeans);
                            }
                        });
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<PlayListBean>>() {
                    @Override
                    public void accept(List<PlayListBean> playListBeans) throws Exception {
                        adapter_Song_List.addData(playListBeans, Constants.SONG_LIST);
                    }
                });


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

    /**
     * fragment的切换
     *
     * @param showFragment
     * @param showTag
     * @param hideTag
     */
    private void addToMainContent(Fragment showFragment, String showTag, String hideTag) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.right_enter,
                R.anim.left_out,
                R.anim.left_enter,
                R.anim.right_out);
        fragmentTransaction
                .hide(getFragmentManager().findFragmentByTag(hideTag))
                .add(R.id.main_container, showFragment, showTag)
                .addToBackStack(hideTag)
                .commit();
    }


    //=================recycle人view的点击事件======================
    @Override
    public void itemChildClick(View childView, int position) {
        Log.e(TAG, "itemChildClick: " + "类型");
    }

    @Override
    public void itemClick(View view, int position) {
        Log.e(TAG, "itemClick: " + view.getId());
    }

    //bindView点击事件
    @OnClick({R.id.hall_singer, R.id.hall_ranking, R.id.hall_radio, R.id.hall_songList, R.id.hall_mv, R.id.hall_album})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.hall_singer:
                break;
            case R.id.hall_ranking:
                addToMainContent(MusicHallRankingFragment.newInstance(),
                        MusicHallRankingFragment.Fragment_Tag, MainFragment.Fragment_Tag);
                break;
            case R.id.hall_radio:
                break;
            case R.id.hall_songList:
                //替换猪fragment
                addToMainContent(MusicHallCategoryFragment.newInstance(data),
                        MusicHallCategoryFragment.Fragment_Tag, MainFragment.Fragment_Tag);
                break;
            case R.id.hall_mv:
                break;
            case R.id.hall_album:
                break;
        }
    }

    private class MyMZViewHolder implements MZViewHolder<String> {
        public ImageView imageView;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.fragment_music_music_hall_banner_item, null, false);
            imageView = view.findViewById(R.id.bannerView_iv);
            return view;
        }

        @Override
        public void onBind(Context context, int i, String s) {
            Glide.with(context)
                    .load(s)
                    .transition(withCrossFade())
                    .into(imageView);
        }
    }


}
