package com.example.yungui.music.fragment;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.transition.Transition;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.yungui.music.Interface.MusicStateListener;
import com.example.yungui.music.MainActivity;
import com.example.yungui.music.R;
import com.example.yungui.music.activity.SongDetailActivity;
import com.example.yungui.music.adapter.MusicAdapter;
import com.example.yungui.music.base.BaseFragment;
import com.example.yungui.music.service.MusicService;

import io.reactivex.disposables.Disposable;

public class MyFragment extends BaseFragment implements MusicStateListener, View.OnClickListener {
    private RecyclerView recyclerView;
    private LinearLayout local, download_song, download_mv, recent, like, bought;
    private ImageView cover;
    private MusicAdapter musicAdapter;
    private MainActivity mainActivity;
    private Disposable disposable;
    private MusicService.MusicBinder musicBinder;
    public static final String BINDER = "binder";

    public MyFragment() {
    }

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_my;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void initView() {
        local = rootView.findViewById(R.id.local);
        download_song = rootView.findViewById(R.id.download_song);
        download_mv = rootView.findViewById(R.id.download_mv);
        recent = rootView.findViewById(R.id.recent);
        like = rootView.findViewById(R.id.like);
        bought = rootView.findViewById(R.id.bought);
        cover = rootView.findViewById(R.id.cover);

        local.setOnClickListener(this);
        download_song.setOnClickListener(this);
        download_mv.setOnClickListener(this);
        recent.setOnClickListener(this);
        like.setOnClickListener(this);
        bought.setOnClickListener(this);
        cover.setOnClickListener(this);


    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initData() {
        mainActivity = (MainActivity) getActivity();
        mainActivity.setMusicStateListener(this);
//        //使用RxJava进行异步操作
//        SongsUtils.getSongs(getActivity())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<List<Song>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        disposable = d;
//                    }
//
//                    @Override
//                    public void onNext(List<Song> value) {
//                        if (value != null) {
//                            musicAdapter.addData(value);
//                        }
//                        musicAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Toast.makeText(getActivity(), "加载出错", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (disposable != null) {
            disposable.isDisposed();
        }
        mainActivity.removeMusicStateListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.local:
                addToMainContent(LocalFragment.newInstance());
                break;
            case R.id.download_song:
                break;
            case R.id.recent:
                break;
            case R.id.like:
                break;
            case R.id.download_mv:
                break;
            case R.id.bought:
                break;
            case R.id.cover:

                break;

        }

    }

    private void addToMainContent(Fragment fragment) {
//        mContext.initToolBarEvent(null);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.right_enter,
                R.anim.left_out,
                R.anim.left_enter,
                R.anim.right_out);
        fragmentTransaction
                .hide(getFragmentManager().findFragmentByTag("mainFragment"))
                .add(R.id.main_container, LocalFragment.newInstance())
                .addToBackStack("mainFragment")
                .commit();
    }

    @Override
    public void updateTrackInfo() {

    }

    @Override
    public void updateTime() {

    }

    @Override
    public void updateTheme() {

    }

    @Override
    public void updateAdapter() {

    }
}
