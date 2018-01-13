package com.example.yungui.music.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.yungui.music.Interface.MusicStateListener;
import com.example.yungui.music.MainActivity;
import com.example.yungui.music.MainFragment;
import com.example.yungui.music.R;
import com.example.yungui.music.adapter.MusicAdapter;
import com.example.yungui.music.base.BaseFragment;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;

public class MusicMyFragment extends BaseFragment implements MusicStateListener, View.OnClickListener {
    public static final String Fragment_Tag = "MusicMyFragment";
    @BindView(R.id.local)
    LinearLayout local;

    @BindView(R.id.download_song)
    LinearLayout download_song;

    @BindView(R.id.download_mv)
    LinearLayout download_mv;

    @BindView(R.id.recent)
    LinearLayout recent;

    @BindView(R.id.like)
    LinearLayout like;

    @BindView(R.id.bought)
    LinearLayout bought;

    @BindView(R.id.cover)
    ImageView cover;

    private MusicAdapter musicAdapter;
    private MainActivity mainActivity;
    private Disposable disposable;

    public MusicMyFragment() {
    }

    public static MusicMyFragment newInstance() {
        MusicMyFragment fragment = new MusicMyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_music_music_my;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        local.setOnClickListener(this);
        download_song.setOnClickListener(this);
        download_mv.setOnClickListener(this);
        recent.setOnClickListener(this);
        like.setOnClickListener(this);
        bought.setOnClickListener(this);
        cover.setOnClickListener(this);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void loadData() {

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
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.local:
                addToMainContent(MusicMyLocalFragment.newInstance(), MusicMyFragment.Fragment_Tag, MainFragment.Fragment_Tag);
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
