package com.example.yungui.music.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.example.mpointindicator.PointIndicator;
import com.example.yungui.music.MainActivity;
import com.example.yungui.music.R;
import com.example.yungui.music.adapter.QueueViewPagerAdapter;
import com.example.yungui.music.service.MusicPlayer;
import com.example.yungui.music.widget.QQPlayButton;

import java.util.ArrayList;

/**
 * Created by yungui on 2017/10/20.
 */

public class QueueFragment extends DialogFragment {
    private View rootView;
    private ViewPager viewPager;
    private Button close;
    private QQPlayButton playButton;
    private PointIndicator pointIndicator;
    private QueueViewPagerAdapter queueViewPagerAdapter;
    private FragmentManager fragmentManager;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    public QueueFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_queue_dailog, container, false);
        initView();
        return rootView;
    }

    private void initView() {
        viewPager = rootView.findViewById(R.id.queue_viewPager);
        close = rootView.findViewById(R.id.queue_close);
        pointIndicator = rootView.findViewById(R.id.queue_pointIndicator);
        fragments.add(QueueItemFragment.newInstance());
        queueViewPagerAdapter = new QueueViewPagerAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(queueViewPagerAdapter);
        pointIndicator.bindViewPager(viewPager);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        Toast.makeText(getActivity(), "Queue" + MusicPlayer.duration(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        window.setNavigationBarColor(Color.WHITE);
    }

    /**
     * Window----->DecorView---->FrameLayout------>FrameLayout-----编写的layout
     */
    @Override
    public void onStart() {
        super.onStart();

    }

    public void slideToUp(View view) {
        Animation slideToUp = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0);
        slideToUp.setDuration(100);
        slideToUp.setFillEnabled(true);
        slideToUp.setFillAfter(true);
        view.startAnimation(slideToUp);
        slideToUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void slideToBottom(View view) {
        Animation slideToBottom = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1.0F);
        slideToBottom.setDuration(100);
        slideToBottom.setFillEnabled(true);
        slideToBottom.setFillAfter(true);
        view.startAnimation(slideToBottom);
        slideToBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getDialog().dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
