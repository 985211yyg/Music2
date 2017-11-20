package com.example.yungui.music.widget;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by yungui on 2017/11/14.
 * 这是一个特定的 三页面 ViewPager.PageTransformer，
 * 以中间的page为主，左侧的从左侧进入，主页位置不变但透明度逐渐变高；
 * 右侧的从右侧进入主页，主页变化相同
 */

public class ViewPagerHelper implements ViewPager.OnPageChangeListener, View.OnTouchListener {

    private ViewPager mViewPager;
    private PageTransformerType type;
    private double lastPositionOffsetSum;//之前的偏移综合
    private OnPageScrollListener onPageScrollListener;//回调的监听器
    private float ScrollPercent;//滑动百分比
    protected boolean rightToLeft;


    public ViewPagerHelper(ViewPager viewPager) {
        this(viewPager, null);
    }

    public ViewPagerHelper(ViewPager viewPager, PageTransformerType transformerType) {
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOnTouchListener(this);
        type = transformerType;
    }

    /**
     * 关键是判断左右滑动
     *
     * @param position             当前页面的位置，int型  比如0------1--------2----
     * @param positionOffset       页面的偏移量   左滑 从0增大到1 为一次页面滑动  右滑 1减小到0
     * @param positionOffsetPixels 页面的偏移量以手机屏幕像素的形式表示
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        float currentPositionOffsetSum = (float) position + positionOffset;
        //左滑
        rightToLeft = this.lastPositionOffsetSum <= (double) currentPositionOffsetSum;

        if ((double) currentPositionOffsetSum != this.lastPositionOffsetSum) {
            int enterPosition;
            int leavePosition;
            //左滑
            if (rightToLeft) {
                enterPosition = positionOffset == 0.0f ? position : position + 1;
                leavePosition = enterPosition - 1;
                ScrollPercent = positionOffset == 0.0f ? 1.0f : positionOffset;
            } else {
                //右滑
                enterPosition = position;
                leavePosition = position + 1;
                ScrollPercent = 1.0F - positionOffset;
            }

            if (this.onPageScrollListener != null) {
                this.onPageScrollListener.onPageScrolled(enterPosition, leavePosition, ScrollPercent);
            }
            this.lastPositionOffsetSum = (double) currentPositionOffsetSum;
        }


    }

    @Override
    public void onPageSelected(int position) {
//        onPageScrollListener.onPagerSelected(position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        onPageScrollListener.onPageScrollStateChanged(state);

    }

    public ViewPager.PageTransformer getPageTransformer() {
        return new CustomTransformer();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return false;
    }

    private class CustomTransformer implements ViewPager.PageTransformer {

        public CustomTransformer() {
        }

        @Override
        public void transformPage(View page, float position) {
            if (type != null) {
                switch (type) {
                    case DEPTH:
                        transformDepth(page, position);
                        break;
                    case LR_ENTER:
                        transformLRenter(page, position, ScrollPercent);
                        break;
                    case ZOOM_OUT:
                        transformZoomOut(page, position);
                        break;
                }

            }
        }

        private void transformLRenter(View page, float position, float ScrollPercent) {
            int width = page.getWidth();
            if (position < -1) {
                //(~,-1) 此时page在界面的左边并且已经不显示在当前界面

            } else if (position <= 0) {
                //[-1,0]  此时page正从中间往左侧移动

                if (rightToLeft) {
                    page.setTranslationX(0);//主页不动
                    page.setAlpha(1 + position);//逐渐变透明
                } else {
                    //右滑 左侧页面进入 -1-----0
                    page.setTranslationX(width * (1 - position));

                }

            } else if (position <= 1) {
                //(0,1] 此时page正从右侧往中间移动

                if (rightToLeft) {
                    // 1--0 右侧页面,进入
                    page.setTranslationX(width * (1 - position));
                } else {
                    //右滑 主页0---1 透明 ，不动
                    page.setTranslationX(0);
                    page.setAlpha(1 - position);
                }

            } else {
                //(1,~)  此时page在界面的右边并且已经不显示在当前界面
            }
        }

        private void transformZoomOut(View page, float position) {
            final float MIN_SCALE = 0.85f;
            final float MIN_ALPHA = 0.5f;
            int pageWidth = page.getWidth();
            int pageHeight = page.getHeight();
            Log.e("TAG", page + " , " + position + "");
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(0);
            } else if (position <= 1) //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
            { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    page.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    page.setTranslationX(-horzMargin + vertMargin / 2);
                }
                // Scale the page down (between MIN_SCALE and 1)
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                // Fade the page relative to its size.
                page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
                        / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(0);
            }
        }

        private void transformDepth(View page, float position) {
            float MIN_SCALE = 0.75f;
            int pageWidth = page.getWidth();
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(0);
            } else if (position <= 0) {
                // [-1,0]
                // Use the default slide transition when moving to the left page
                page.setAlpha(1);
                page.setTranslationX(0);
                page.setScaleX(1);
                page.setScaleY(1);
            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                page.setAlpha(1 - position);
                // Counteract the default slide transition
                page.setTranslationX(pageWidth * -position);
                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(0);
            }
        }

    }

    public void bindScrollListener(OnPageScrollListener onPageScrollListener) {
        this.onPageScrollListener = onPageScrollListener;
    }

    public enum PageTransformerType {
        DEPTH, ZOOM_OUT, LR_ENTER
    }

    public interface OnPageScrollListener {

        void onPageScrolled(int enterPosition, int leavePosition, float percent);

        void onPagerSelected(int Selected);

        void onPageScrollStateChanged(int state);
    }

}



