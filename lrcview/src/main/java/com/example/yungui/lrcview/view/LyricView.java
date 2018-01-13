package com.example.yungui.lrcview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.OverScroller;

import com.example.yungui.lrcview.R;
import com.example.yungui.lrcview.bean.LineInfo;
import com.example.yungui.lrcview.bean.LrcInfo;

/**
 * @author yungui
 * @date 2017/11/15
 * 实现当前播放高亮显示、歌词回弹效果、歌词淡入淡出效果以及滑动歌词快速播放等功能
 */

public class LyricView extends View {
    public static final String TAG = LyricView.class.getSimpleName();
    public static final int Def_Line_Height = 20;
    public static final int Def_Normal_Color = Color.GRAY;
    public static final int Def_Text_Size = 14;
    public static final int Def_Height_Light_Color = Color.parseColor("#30be7a");
    public static final int Margin = 10;
    public static final int Show_Line_Color = Color.BLACK;
    public static final int Def_Time_Text_Size = 12;

    public static final float Def_Shader_Width = 120;
    public static final float Def_Indicator_Button_Radius = 12;
    public static final int Def_Indicator_Color = Color.BLACK;

    public static final int MSG_PLAYER_HIDE = 0X123;
    public static final int MSG_PLAYER_SLIDE = 0x124;
    public static final int MSG_LRC_SCROLL = 0x125;

    private int lineCount;//行数
    private float lineHeight;//行高
    private float lineSpace;//行间距
    private float shaderWidth;//渐变阴影宽度

    private float indicatorButtonRadius;//指示器按钮的大小
    private float indicatorMargin;//默认的指示器边距

    private int indicatorColor;//指示器的颜色
    private int normalColor;//普通字体体颜色
    private int heightLightColor;//高亮字体颜色
    private float textSize;//歌词大小
    private float timeTextSize;//指示器提示时间的字体大小

    private float mScrollY = 0;  //竖直偏移量
    private float mScrollX = 0;
    private float mVelocity = 0;  // 数值方向上的滑动速度
    private int mCurrentShowLine = 0;  // 当前拖动位置对应的行数
    private int mCurrentPlayLine = 0;  // 当前播放位置对应的行数
    private int mMinStartFlingSpeed = 1200;  // 最低滑行启动速
    private int maxFlingVelocity;  // 最大纵向滑动速度
    private boolean showIndicator;

    private Context mContext;

    private LrcInfo lrcInfo;
    private String defTime = "00:00";//默认时间
    private String defLrc = "你是我的歌 \n 我的锅！";
    private String currentLrc = "聆听天籁";//当前播放的歌词

    private Paint textPaint, buttonPaint, indicatorPaint, indicatorButtonPaint, dashPaint, timePaint;
    private VelocityTracker velocityTracker;//速度追踪器
    private ValueAnimator flingAnimator, scrollAnimator;

    private boolean isUserTouch = false;  // 判断当前用户是否触摸
    private boolean isShowIndicator = false;  // 判断当前滑动指示器是否显示
    private boolean isScroll = false;
    private boolean isYScroll = false;//标记是否是数值方向的滚动


    private Rect timeBound, LrcBound, defLrcBound;


    private onIndicatorPlayerClickListener indicatorPlayerClickListener;
    private OnClickListener onClickListener;
    private OverScroller overScroller;

    /**
     * 触摸事件相关  手指按下坐标，滑动距离
     */
    private int downX;
    private int downY;
    private int lastScrollY;
    private int lastScrollX;

    /**
     * 用于歌词滚动的handler
     */
    private Handler postHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_PLAYER_HIDE:
                    postHandler.sendEmptyMessageDelayed(MSG_PLAYER_SLIDE, 1200);
                    isShowIndicator = false;
                    invalidateView();
                case MSG_PLAYER_SLIDE:
                    smoothScrollTo(measureCurrentScrollY(mCurrentPlayLine));
                    postHandler.sendEmptyMessageDelayed(MSG_LRC_SCROLL, 0);
                    invalidateView();
                case MSG_LRC_SCROLL:
                    //重新开始歌词滚动动画
                    if (scrollAnimator != null && !scrollAnimator.isPaused()) {
                        scrollAnimator.start();
                    }
                    invalidateView();
            }
        }
    };


    public LyricView(Context context) {
        this(context, null);
    }

    public LyricView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initComponent(context);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LyricView);
        lineHeight = a.getDimension(R.styleable.LyricView_lineHeight, applyDimension(TypedValue.COMPLEX_UNIT_DIP, Def_Line_Height));
        lineSpace = a.getDimension(R.styleable.LyricView_lineSpace, applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10));
        textSize = a.getDimension(R.styleable.LyricView_textSize, applyDimension(TypedValue.COMPLEX_UNIT_SP, Def_Text_Size));
        timeTextSize = a.getDimension(R.styleable.LyricView_timeTextSize, applyDimension(TypedValue.COMPLEX_UNIT_SP, Def_Time_Text_Size));
        shaderWidth = a.getDimension(R.styleable.LyricView_shaderWidth, applyDimension(TypedValue.COMPLEX_UNIT_DIP, Def_Shader_Width));
        indicatorButtonRadius = a.getDimension(R.styleable.LyricView_shaderWidth, applyDimension(TypedValue.COMPLEX_UNIT_DIP, Def_Indicator_Button_Radius));
        indicatorMargin = a.getDimension(R.styleable.LyricView_shaderWidth, applyDimension(TypedValue.COMPLEX_UNIT_DIP, Margin));
        normalColor = a.getColor(R.styleable.LyricView_normalColor, Def_Normal_Color);
        heightLightColor = a.getColor(R.styleable.LyricView_heightLightColor, Def_Height_Light_Color);
        indicatorColor = a.getColor(R.styleable.LyricView_shaderWidth, Def_Indicator_Color);
        showIndicator = a.getBoolean(R.styleable.LyricView_showIndicator, false);

    }

    private void initComponent(Context context) {
        lineHeight = lineHeight + lineSpace;
        overScroller = new OverScroller(context, new AccelerateDecelerateInterpolator());

        textPaint = new Paint();
        textPaint.setDither(true);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(normalColor);
        //获取绘制文字的范围
        LrcBound = new Rect();
        //没有歌词，绘制，默认提示
        defLrcBound = new Rect();
        textPaint.getTextBounds(defLrc, 0, defLrc.length(), defLrcBound);

        timePaint = new Paint();
        timePaint.setColor(indicatorColor);
        timePaint.setAntiAlias(true);
        timePaint.setTextSize(timeTextSize);


        buttonPaint = new Paint();
        buttonPaint.setColor(indicatorColor);
        buttonPaint.setAntiAlias(true);
        buttonPaint.setDither(true);

        indicatorPaint = new Paint();
        indicatorPaint.setDither(true);
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setColor(indicatorColor);

        indicatorButtonPaint = new Paint();
        indicatorButtonPaint.setAntiAlias(true);
        indicatorButtonPaint.setColor(indicatorColor);
        indicatorButtonPaint.setStyle(Paint.Style.STROKE);
        indicatorButtonPaint.setStrokeWidth(3);
        //所有拐角变为圆角
        PathEffect pathEffect = new CornerPathEffect(4);
        indicatorButtonPaint.setPathEffect(pathEffect);

        dashPaint = new Paint();
        dashPaint.setAntiAlias(true);
        dashPaint.setColor(indicatorColor);
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setStrokeWidth(3);
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{10, 8}, 0);
        dashPaint.setPathEffect(dashPathEffect);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置渐变阴影的宽度
        shaderWidth = getMeasuredHeight() * 0.3f;
    }

    /**
     * 通过控制mScrollY的值不断的绘制歌词
     * canvas.drawText(x,y)绘制文字是的参数 x--左下角的起点，y数值方向的基线
     *
     * @param canvas ······························································
     *               (x,y) canvas.drawText(x,y)绘制文字是的参数 x--左下角的起点，y数值方向的基线
     *               ······························································
     *               (x,y)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //检查是否有歌词然后可以滚动
        if (checkScrollable()) {
            for (int i = 0; i < lineCount-1; i++) {
                String text = lrcInfo.lineInfos.get(i).getLrc();
                textPaint.getTextBounds(text, 0, text.length(), LrcBound);
                float startX = getMeasuredWidth() / 2 - LrcBound.width() / 2;
                //设置绘制的基线,通过计算mScrollY的值动态控制基线的位置，上移滚共显示歌词，或者响应触摸计算滚动值移动歌词
                float baseLine = getMeasuredHeight() / 2 + lineHeight * i - LrcBound.height() / 2 - mScrollY;
                //基线位于view顶部之外是跳过本次，开始下一次绘制
                if (baseLine + lineHeight * 0.5f < 0) {
                    continue;
                }
                //基线位于屏幕bottom的时候不在绘制
                if (baseLine - lineHeight * 0.5f > getMeasuredHeight()) {
                    break;
                }
                //绘制高亮歌词
                if (i == mCurrentPlayLine - 1) {
                    textPaint.setColor(heightLightColor);
                } else {
                    //如果指示器显示了，并且指示器的位置位于在行歌词
                    //则显示选中颜色
                    if (isShowIndicator && mCurrentShowLine == i) {
                        textPaint.setColor(Show_Line_Color);
                    } else {
                        //普通歌词
                        textPaint.setColor(normalColor);
                    }
                }
                //绘制渐变阴影 越往上越不透明，往下也是一样
                if (baseLine > getMeasuredHeight() - shaderWidth || baseLine <= shaderWidth) {
                    //上部渐变阴影，自上而下增加透明度，26---230，0全透明，255不透明
                    if (baseLine <= shaderWidth) {
                        textPaint.setAlpha((int) (26 + 230 * (baseLine / shaderWidth)));
                    } else {
                        //绘制下部阴影，自上而下减小透明度230--26或者其他
                        textPaint.setAlpha((int) (26 + 230 * ((getMeasuredHeight() - baseLine) / shaderWidth)));
                    }
                } else {
                    textPaint.setAlpha(255);//全透明
                }

                //绘制歌词
                canvas.drawText(lrcInfo.lineInfos.get(i).getLrc(), startX, baseLine, textPaint);
            }
        } else {
            //默认提示
            //绘制在中间位置基线半个字符高度,
            canvas.drawText(defLrc, getMeasuredWidth() / 2 - defLrcBound.width() * 0.5f, getMeasuredHeight() / 2 + defLrcBound.height() / 2, textPaint);
        }
        //滑动是指示器的显示
        if (showIndicator && isShowIndicator && checkScrollable()) {
            //如果可以播放
            drawPlayerButton(canvas);
            drawIndicator(canvas);
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (visibility == INVISIBLE) {
            isShowIndicator = false;
        } else {
            invalidateView();
        }
    }

    /**
     * 绘制指示器线
     *
     * @param canvas
     */
    private void drawIndicator(Canvas canvas) {
        timeBound = new Rect();
        timePaint.getTextBounds(defTime, 0, defTime.length(), timeBound);

        //虚线左边的位置，|~00:00~-----------------~O~|
        float startX = indicatorMargin + timeBound.width() + indicatorMargin;
        float startY = getMeasuredHeight() / 2 + 3 / 2;
        //虚线右边位置右边去除按钮和边距
        float endX = getMeasuredWidth() - indicatorMargin - indicatorButtonRadius * 2 - indicatorMargin;
        float endY = startY;

        //提示时间的位置
        float textStartX = indicatorMargin;
        float baseLineY = getMeasuredHeight() / 2 + timeBound.height() / 2;

        Path dashPath = new Path();
        dashPath.moveTo(startX, startY);
        dashPath.lineTo(endX, endY);
        canvas.drawPath(dashPath, dashPaint);
        //绘制时间
        canvas.drawText(defTime, textStartX, baseLineY, timePaint);
    }

    /**
     * 绘制播放按钮
     *
     * @param canvas
     */
    private void drawPlayerButton(Canvas canvas) {
        float cx = getMeasuredWidth() - indicatorMargin - indicatorButtonRadius;
        float cy = getMeasuredHeight() / 2;
        canvas.drawCircle(cx, cy, indicatorButtonRadius, indicatorButtonPaint);
        //暂停三角图标
        Path path = new Path();
        path.moveTo(cx - indicatorButtonRadius / 4, (float) (cy - indicatorButtonRadius * Math.sqrt(3) / 4));
        path.lineTo(cx + indicatorButtonRadius / 2, cy);
        path.lineTo(cx - indicatorButtonRadius / 4, (float) (cy + indicatorButtonRadius * Math.sqrt(3) / 4));
        path.close();
        canvas.drawPath(path, indicatorButtonPaint);

    }

    /**
     * 检查点击事件是否在playerButton的点击范围内
     *
     * @param event
     * @return
     */
    private boolean checkIsPlayerClick(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        //播放按钮的方位
        int left = getMeasuredWidth() - (int) (indicatorMargin * 2 + indicatorButtonRadius * 2);
        int rightX = getMeasuredWidth();
        int topY = (int) (getMeasuredHeight() / 2 - indicatorButtonRadius - Margin);
        int bottomY = (int) (getMeasuredHeight() / 2 + indicatorButtonRadius + Margin);

        if (left <= x && x <= rightX & y >= topY && y <= bottomY) {
            return true;
        }
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //初始化velocityTracker
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                actionUp(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                actionCancel(event);
                break;

        }
        return true;
    }

    /**
     * 处理按下事件
     *
     * @param event
     */
    private void actionDown(MotionEvent event) {
        ///移除隐藏指示器和滚回元位置的消息，比如用户滑动了歌词然后抬起收，
        /// 突然想不通，有滑动了歌词，这时候如果消息还是传出去，
        /// 用户在滑动的过程中歌词突然滑到原位，指示器突然隐藏，这就很不好
        postHandler.removeMessages(MSG_PLAYER_HIDE);
        postHandler.removeMessages(MSG_PLAYER_SLIDE);
        //用户触摸，可以显示指示器
        downY = (int) event.getY();
        downX = (int) event.getX();
        //记录上次滚动的距离
        lastScrollY = (int) mScrollY;
        lastScrollX = (int) mScrollX;

        //每次发生点击事件之前重置动画
        if (flingAnimator != null) {
            flingAnimator.cancel();
            flingAnimator = null;
        }
    }

    /**
     * 处理滚动事件
     *
     * @param event
     */
    private void actionMove(MotionEvent event) {
        //todo 左右滑动
        if ((Math.abs(downX - event.getX()) - Math.abs(downY - event.getY())) > 10) {
            isShowIndicator = false;
            isUserTouch = false;
            mScrollX = lastScrollX + (downX - event.getX());
        } else if ((Math.abs(downX - event.getX()) - Math.abs(downY - event.getY())) < -10) {
            //todo 上下滑动
            isUserTouch = true;
            isShowIndicator = true;
            //如果有歌词，可以滚动
            if (checkScrollable()) {
                VelocityTracker tracker = velocityTracker;
                tracker.computeCurrentVelocity(1000, maxFlingVelocity);
                float scrollY = lastScrollY + (downY - event.getY());
                //计算滚动位置和内容中间位置的的距离，上负下正
                float value01 = scrollY - (lineCount * lineHeight * 0.5f);
                float value02 = ((Math.abs(value01) - (lineCount * lineHeight * 0.5f)));
                //mScrollY 的值==滚动的距离+摩擦减速的距离ok!
                //问题的关键就是计算着个摩擦距离
                //在阻尼效果的作用下能够继续滚动的距离，按比例减小，
                //就是理想情况应该是滑动多少view滚动多少，当实际上有一个逐渐减速的效果
                //value01 / Math.abs(value01)  控制滑动方向
                mScrollY = value02 > 0 ? scrollY - (measureDampingDistance(value02) * value01 / Math.abs(value01)) : scrollY;
                //竖直方向的滚动速度
                mVelocity = tracker.getYVelocity();
                //滚动的同时根据滚动的距离，计算当前的行数
                measureCurrentLine();
            }

        } else {
            //todo [-10,10]

        }

    }

    /**
     * 抬起手指
     *
     * @param event
     */
    private void actionUp(MotionEvent event) {
        //手指抬起时的时间判断
        if (isUserTouch && Math.abs(downX - event.getX()) == 0 && Math.abs(downY - event.getY()) == 0) {
            if (onClickListener != null) {
                onClickListener.onClick(this);
            }
        }
        releaseVelocityTracker();
        // 触摸结束 2.4s 后发送一个指示器隐藏的消息
        postHandler.sendEmptyMessageDelayed(MSG_PLAYER_HIDE, 2400);

        if (checkScrollable()) {
            isUserTouch = false; // 用户手指离开屏幕，取消触摸标记
            //判断是否过度滚动
            if (overScrolled() && mScrollY < 0) {
                //滚回歌词开始位置
                smoothScrollTo(0);
                return;
            }

            //滚回超出了最下范围，回滚到最下面
            if (overScrolled() && mScrollY > lineHeight * (lineCount - 1)) {
                smoothScrollTo(lineHeight * (lineCount - 1));
                return;
            }

            //如果竖直滚动速度大于指定滚动速度，直接飞过去
            if (Math.abs(mVelocity) > mMinStartFlingSpeed) {
                //跑的不行用飞的？
                doFlingAnimator(mVelocity);
                return;
            }

            //如果指示器显示并且手指抬起之前点击的是按钮，播放点击位置
            if (isShowIndicator && checkIsPlayerClick(event)) {
                if (mCurrentShowLine != mCurrentPlayLine) {
                    isShowIndicator = false;
                    if (indicatorPlayerClickListener != null) {
                        indicatorPlayerClickListener
                                .onIndicatorPlayerClick(lrcInfo.lineInfos.get(mCurrentShowLine - 1).getStartTime(),
                                        mCurrentShowLine - 1);
                    }
                }
            }
        }
    }

    /**
     * 取消触摸事件
     *
     * @param event
     */
    private void actionCancel(MotionEvent event) {
        //重置速度追踪器
        releaseVelocityTracker();
    }


    /**
     * 计算阻尼效果的大小
     *
     * @param value2
     * @return
     */
    //最大的摩擦减速距离
    private final int maxDampingDistance = 360;

    private float measureDampingDistance(float value2) {
        //如果这个距离大于
        return value2 > maxDampingDistance ? (maxDampingDistance * 0.6f + (value2 - maxDampingDistance) * 0.72f) : value2 * 0.6f;
    }

    /**
     * 根据滚动的距离计算滚动的行数，容许半个行高的缓冲
     */
    private void measureCurrentLine() {
        //当前的滚动值
        float baseScrollY = mScrollY;
        //取整
        mCurrentShowLine = (int) (baseScrollY / lineHeight + 1);
        calculateIndicatorTime(mCurrentShowLine);
    }

    /**
     * 根据指定行号，计算需要滚动的距离
     *
     * @param line 当前指定行号
     */
    private float measureCurrentScrollY(int line) {
        return (line - 1) * lineHeight;
    }

    /**
     * 根据行数计算指示器显示的时间
     *
     * @param line
     */
    private void calculateIndicatorTime(int line) {
        if (lrcInfo != null
                && lrcInfo.lineInfos != null
                && lrcInfo.lineInfos.size() > 0
                && line >= 0 && line <= lineCount - 1) {
            int minute = (int) (lrcInfo.getLineInfos().get(line).getStartTime() / 1000 / 60);
            int second = (int) (lrcInfo.getLineInfos().get(line).getStartTime() / 1000 % 60);
            if (second < 10) {
                defTime = "0" + minute + ":0" + second;
            } else {
                defTime = "0" + minute + ":" + second;
            }
            invalidate();
        }
    }


    /**
     * 滑行动画 让子弹飞一会？？
     *
     * @param velocity 滑动速度
     */
    private void doFlingAnimator(float velocity) {
        // 计算就当前的滑动速度理论上的滑行距离是多少
        float distance = (velocity / Math.abs(velocity) * Math.min((Math.abs(velocity) * 0.05f), 640));
//        float distance = (float) getSplineFlingDistance((int) velocity);
        // 综合考虑边界问题后得出的实际滑行距离
        float to = Math.min(Math.max(0, (mScrollY - distance)), (lineCount - 1) * lineHeight);
        //滚动范围是时间滑动的距离到要飞？的距离
        flingAnimator = ValueAnimator.ofFloat(mScrollY, to);
        flingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //回调更行滚动距离
                mScrollY = (float) animation.getAnimatedValue();
                //滚动的时候根据mScrollY，不断计算滚到到了哪一行，便于滚回到原来播放的位置
                measureCurrentLine();
                //重绘！！！
                invalidateView();
            }
        });
        //监听了值的变化，并作出相应还不够，还要监听动画的状态
        flingAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mVelocity = mMinStartFlingSpeed - 1;
                isScroll = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isScroll = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }
        });

        flingAnimator.setDuration(420);
        flingAnimator.setInterpolator(new DecelerateInterpolator());
        flingAnimator.start();
    }

    /**
     * 给定时间，让歌词滚动到与时间对应的位置
     * ，根据时间匹配歌词的时间，得到所在歌词行数，然后计算要滚动的距离
     *
     * @param currentTime
     */
    private void scrollToCurrentTime(long currentTime) {
        int position = 0;
        //可以滚动
        if (checkScrollable()) {
            //遍历到与指定时间对应的位置
            for (int i = 0; i < lineCount; i++) {
                LineInfo lineInfo = lrcInfo.getLineInfos().get(i);
                if (lineInfo != null && lineInfo.getStartTime() > currentTime) {
                    //找到位置
                    position = i;
                    break;
                }
                if (i == lineCount - 1) {
                    position = lineCount;
                }
            }

            //没有原地打滚，没有在滚，没有用户阻止和打断
            if (!isUserTouch
                    && !isShowIndicator
                    && mCurrentPlayLine != position) {
                //重置当前播放位置
                mCurrentPlayLine = position;
                currentLrc = lrcInfo.getLineInfos().get(mCurrentPlayLine).getLrc();
                //用播放位置计算滚动距离，然后滚
                smoothScrollTo(measureCurrentScrollY(mCurrentPlayLine));
            } else {
                //如果是当前的行，不用滚
                if (!isUserTouch && !isShowIndicator) {
                    mCurrentPlayLine = mCurrentShowLine = position;
                }

            }
        }
    }


    /**
     * 滑动到指定的位置
     *
     * @param toY 指定滑动到的位置坐标
     */
    private void smoothScrollTo(float toY) {
        scrollAnimator = ValueAnimator.ofFloat(mScrollY, toY);
        scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                用户打断滑动
                if (isUserTouch) {
                    scrollAnimator.pause();
                    return;
                }
                mScrollY = (float) animation.getAnimatedValue();
                invalidateView();
            }
        });

        scrollAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isScroll = true;
                measureCurrentLine();

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isScroll = false;
            }
        });
        scrollAnimator.setDuration(640);
        scrollAnimator.setInterpolator(new OvershootInterpolator(0.5f));
        scrollAnimator.start();
    }

    /**
     * 判断是否过度滚动，滚动的距离大于所有行高之和？？？，或者小于0
     * 在大于和小于是给定一个缓冲距离，展示粘性效果???
     *
     * @return
     */
    private boolean overScrolled() {
        return checkScrollable() && (mScrollY > lineHeight * (lineCount - 1) || mScrollY < 0);
    }

    /**
     * 释放速度追踪器
     */
    private void releaseVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.clear();
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    /**
     * 检查是否可以滑动
     *
     * @param
     * @return
     */
    private boolean checkScrollable() {
        return lrcInfo != null && lrcInfo.lineInfos != null && lrcInfo.lineInfos.size() > 0;
    }


    /**
     * 刷新View
     */
    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            //  当前线程是主UI线程，直接刷新。
            invalidate();
        } else {
            //  当前线程是非UI线程，post刷新。
            postInvalidate();
        }
    }


    /**
     * 转换尺寸
     *
     * @param unit TypedValue.COMPLEX_UNIT_SP ...
     * @param size
     * @return
     */
    private float applyDimension(int unit, float size) {
        Context c = getContext();
        Resources r;
        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());

    }

    private void restView() {
        mCurrentPlayLine = mCurrentShowLine = 0;
        restLrc();
        //重绘
        invalidateView();
        //重置行数
        lineCount = 0;
        //重置滚动距离
        mScrollY = 0;
    }

    private void restLrc() {
        if (lrcInfo != null) {
            lrcInfo = null;
        }
    }

    /**
     * 歌词界面滑动显示的按钮点击监听
     */
    public interface onIndicatorPlayerClickListener {
        void onIndicatorPlayerClick(long time, int currentLrcLine);
    }

    public interface OnClickListener {
        void onClick(View view);
    }



    /*
    =======================================对外的方法============================
     */

    /**
     * 设置指示器按钮的点击事件回调
     *
     * @param indicatorPlayerClickListener
     */
    public void setPlayerClickListener(onIndicatorPlayerClickListener indicatorPlayerClickListener) {
        this.indicatorPlayerClickListener = indicatorPlayerClickListener;
    }

    /**
     * 设置歌词界面的单击事件
     *
     * @param onClickListener
     */
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * 重置歌词显示器，设置默认提示
     *
     * @param defHint
     */
    public void rest(String defHint) {
        defLrc = defHint;
        restView();
    }

    /**
     * 指定时间并显示对应时间的歌词，
     * 比如快进后随影歌词显示，或者再次回到歌词界面，
     * 根据记录的播放位置，显示歌词
     *
     * @param currentTime
     */

    public void setCurrentTime(long currentTime) {
        scrollToCurrentTime(currentTime);
    }

    /**
     * 控制指示器是否显示
     *
     * @param showIndicator
     */
    public void setShowIndicator(boolean showIndicator) {
        this.showIndicator = showIndicator;
        invalidateView();
    }

    public LrcInfo getLrcInfo() {
        return lrcInfo;
    }

    /**
     * 设置歌词资源
     *
     * @param lrcInfo
     */
    public void setLrcInfo(LrcInfo lrcInfo) {
        Log.e(TAG, "setLrcInfo:   success! ");
        if (lrcInfo != null && lrcInfo.lineInfos.size() > 0) {
            this.lrcInfo = lrcInfo;
            lineCount = lrcInfo.lineInfos.size();
        }
        invalidateView();
    }

    public void setScrollValue(float mScrollY) {
        this.mScrollY = mScrollY;
        invalidateView();
    }


    /**
     * 设置歌词行高
     *
     * @param lineHeight
     */
    public void setLineHeight(float lineHeight) {
        this.lineHeight = applyDimension(TypedValue.COMPLEX_UNIT_DIP, lineHeight);
        invalidateView();
    }

    /**
     * 设置上下渐变阴影的宽度
     *
     * @param shaderWidth
     */
    public void setShaderWidth(float shaderWidth) {
        this.shaderWidth = applyDimension(TypedValue.COMPLEX_UNIT_DIP, shaderWidth);
        invalidateView();
    }

    /**
     * 设置指示器按钮的大小
     *
     * @param indicatorButtonRadius
     */
    public void setIndicatorButtonRadius(float indicatorButtonRadius) {
        this.indicatorButtonRadius = applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorButtonRadius);
        invalidateView();
    }

    /**
     * 色好孩子指示器的边距
     *
     * @param indicatorMargin
     */
    public void setIndicatorMargin(float indicatorMargin) {
        this.indicatorMargin = applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorMargin);
        invalidateView();
    }

    /**
     * 设置指示器的颜色
     *
     * @param indicatorColor
     */
    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = mContext.getResources().getColor(indicatorColor);
        indicatorPaint.setColor(this.indicatorColor);
        invalidateView();
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    /**
     * 返回当亲播放的而歌词
     *
     * @return
     */
    public String getCurrentLrc() {
        return currentLrc;
    }

    /**
     * 设置一般歌词的颜色
     *
     * @param normalColor
     */
    public void setNormalColor(int normalColor) {
        this.normalColor = mContext.getResources().getColor(normalColor);
        invalidateView();
    }

    public int getNormalColor() {
        return normalColor;
    }

    /**
     * 设置播放歌词的颜色
     *
     * @param heightLightColor
     */
    public void setHeightLightColor(int heightLightColor) {
        this.heightLightColor = mContext.getResources().getColor(heightLightColor);
        invalidateView();
    }

    public int getHeightLightColor() {
        return heightLightColor;
    }

    /**
     * 设置歌词的大小
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        this.textSize = applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize);
        textPaint.setTextSize(this.textSize);
        invalidateView();
    }

    public float getTextSize() {
        return textSize;
    }


    /**
     * 设置当前展示的行
     *
     * @param mCurrentShowLine
     */
    public void setCurrentShowLine(int mCurrentShowLine) {
        this.mCurrentShowLine = mCurrentShowLine;
        invalidateView();
    }

    public int getCurrentShowLine() {
        return mCurrentShowLine;
    }

    /**
     * 获取正在播放的行
     *
     * @return
     */
    public int getCurrentPlayLine() {
        return mCurrentPlayLine;
    }

    /**
     * 设置当前的播放行
     *
     * @param mCurrentPlayLine
     */
    public void setCurrentPlayLine(int mCurrentPlayLine) {
        this.mCurrentPlayLine = mCurrentPlayLine;
        smoothScrollTo(measureCurrentScrollY(mCurrentPlayLine));
        invalidateView();
    }

}
