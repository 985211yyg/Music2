package com.example.yungui.music.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.example.yungui.music.R;

/**
 * Created by yungui on 2017/11/9.
 */

public class QQPlayButton extends ToggleButton {
    //通用颜色
    private int normalColor;
    //自定义播放状态颜色
    private int playColor;
    //自定义暂停状态颜色
    private int pauseColor;
    //进度的颜色
    private int progressColor;
    //刷新的颜色
    private int refreshColor;
    //半径
    private int mRadius;
    private static final int DefRadius = 50;
    private static final int DefWidth = 3;
    private static final float DefRefreshAngle = 20.0f;
    //外框的宽度
    private float outBorderWidth;
    private int DefColor;
    //进度
    private float progress;

    //刷新角度
    private float refreshAngle;
    //开始刷新的角度
    private float startRefreshAngle = 0.0f;

    private boolean refreshing;
    private boolean isPlay;
    //定义画笔
    private Paint outBorderPaint;//外环画笔
    private Paint pausePaint;//暂停按钮画笔
    private Paint playPaint;//播放按钮画笔
    private Paint progressPaint;//画纸进度的画笔
    private Paint refreshPaint;//刷新画笔
    private float cx, cy;
    private OnPlayOrPauseListener playOrPauseListener;

    public QQPlayButton(Context context) {
        this(context, null);
    }

    public QQPlayButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QQPlayButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DefColor = getResources().getColor(R.color.themeColor);
        initAttr(context, attrs);
        initVar();
        this.setChecked(true);
        //没有使用toggleButton的checkChangeListener,不便于控制播放状态与图标联动
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playOrPauseListener != null) {
                    if (isChecked()) {
                        playOrPauseListener.pause();
                    } else {
                        playOrPauseListener.play();
                    }
                }
            }
        });
    }

    private void initAttr(Context context, AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.QQPlayButton);
        normalColor = typedArray.getColor(R.styleable.QQPlayButton_normalColor, DefColor);
        pauseColor = typedArray.getColor(R.styleable.QQPlayButton_pauseColor, DefColor);
        playColor = typedArray.getColor(R.styleable.QQPlayButton_playColor, DefColor);
        mRadius = (int) typedArray.getDimensionPixelSize(R.styleable.QQPlayButton_Radius, DefRadius);
        outBorderWidth = typedArray.getDimension(R.styleable.QQPlayButton_outBorderWidth, DefWidth);
        progressColor = typedArray.getColor(R.styleable.QQPlayButton_progressColor, DefColor);
        refreshColor = typedArray.getColor(R.styleable.QQPlayButton_refreshColor, Color.WHITE);
        progress = typedArray.getFloat(R.styleable.QQPlayButton_progress, 0);
        refreshAngle = typedArray.getFloat(R.styleable.QQPlayButton_refreshAngle, DefRefreshAngle);
        refreshing = typedArray.getBoolean(R.styleable.QQPlayButton_refreshing, false);
        typedArray.recycle();
    }

    private void initVar() {
        outBorderPaint = new Paint();
        outBorderPaint.setColor(normalColor);
        outBorderPaint.setStrokeWidth(outBorderWidth);
        outBorderPaint.setAntiAlias(true);
        outBorderPaint.setDither(true);
        outBorderPaint.setStyle(Paint.Style.STROKE);

        pausePaint = new Paint();
        pausePaint.setStyle(Paint.Style.FILL);
        pausePaint.setAntiAlias(true);
        pausePaint.setColor(pauseColor);
        CornerPathEffect cornerPathEffect = new CornerPathEffect(4);
        pausePaint.setPathEffect(cornerPathEffect);


        playPaint = new Paint();
        playPaint.setStyle(Paint.Style.FILL);
        playPaint.setAntiAlias(true);
        playPaint.setColor(playColor);

        progressPaint = new Paint();
        progressPaint.setColor(progressColor);
        progressPaint.setStrokeWidth(outBorderWidth * 2);
        progressPaint.setAntiAlias(true);
        progressPaint.setDither(true);
        progressPaint.setStyle(Paint.Style.STROKE);

        refreshPaint = new Paint();
        refreshPaint.setColor(refreshColor);
        refreshPaint.setStrokeWidth(outBorderWidth * 2);
        refreshPaint.setAntiAlias(true);
        refreshPaint.setDither(true);
        refreshPaint.setStyle(Paint.Style.STROKE);

    }

    /**
     * 需要对view wrap_content的情况进行测量,否则副view的测量结果是match_parent
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        //如果模式是精确地 则已制定具体的大小或者是MATCH_PARENT
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            //没有精确制定，包括 MeasureSpec.AT_MOST,MeasureSpec.UNSPECIFIED(不常用)，主要针对warp_parent
            width = (int) (getPaddingRight() + getPaddingRight() + mRadius * 2 + outBorderWidth);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) (getPaddingBottom() + getPaddingTop() + mRadius * 2 + outBorderWidth);
        }
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        cx = getMeasuredWidth() / 2;
        cy = getMeasuredHeight() / 2;
        canvas.drawCircle(cx, cy, mRadius - 1, outBorderPaint);
        if (this.isChecked()) {
            drawPause(canvas, cx, cy, mRadius, pausePaint);
        } else {
            drawPlay(canvas, cx, cy, mRadius, playPaint);
        }
        //绘制进度条
        drawProgress(canvas, outBorderWidth, progress, progressPaint);
        if (refreshing) {
            //绘制刷新条
            drawRefresh(canvas, outBorderPaint, refreshAngle, refreshPaint);
        }
    }

    private void drawRefresh(Canvas canvas, Paint outBorderPaint, float refreshAngle, Paint refreshPaint) {
        RectF rectF = new RectF(getPaddingLeft() + outBorderWidth, getPaddingTop() + outBorderWidth, mRadius * 2, mRadius * 2);
        canvas.drawArc(rectF, -90 + startRefreshAngle, refreshAngle, false, refreshPaint);
    }

    //播放双竖线图标
    private void drawPlay(Canvas canvas, float cx, float cy, int Radius, Paint paint) {
        Rect rect1 = new Rect((int) (cx - mRadius / 3)
                , (int) (cy - mRadius * Math.sqrt(3) / 4)
                , (int) (cx - mRadius / 6)
                , (int) (cy + mRadius * Math.sqrt(3) / 4));
        Rect rect2 = new Rect((int) (cx + mRadius / 6)
                , (int) (cy - mRadius * Math.sqrt(3) / 4)
                , (int) (cx + mRadius / 3)
                , (int) (cy + mRadius * Math.sqrt(3) / 4));
        canvas.drawRect(rect1, playPaint);
        canvas.drawRect(rect2, playPaint);
    }

    //暂停三角图标
    private void drawPause(Canvas canvas, float cx, float cy, int mRadius, Paint playPaint) {
        Path path = new Path();
        path.moveTo(cx - mRadius / 4, (float) (cy - mRadius * Math.sqrt(3) / 4));
        path.lineTo(cx + mRadius / 2, cy);
        path.lineTo(cx - mRadius / 4, (float) (cy + mRadius * Math.sqrt(3) / 4));
        path.close();
        canvas.drawPath(path, pausePaint);
    }

    //绘制进度条，宽度是外框的两倍
    private void drawProgress(Canvas canvas, float outBorderWidth, float sweepAngle, Paint paint) {
        RectF rectF = new RectF(getPaddingLeft() + outBorderWidth + 2, getPaddingTop() + outBorderWidth + 2,
                mRadius * 2 - 2,
                mRadius * 2 - 2);
        canvas.drawArc(rectF, -90, sweepAngle, false, paint);
    }

    public ValueAnimator cacheRefresh() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 360f);
        valueAnimator.setDuration(3000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setStartRefreshAngle(value);
            }
        });
        return valueAnimator;
    }

    /**
     * 对外的监听接口
     *
     * @param playOrPauseListener
     */
    public void setOnPlayAndPauseListener(OnPlayOrPauseListener playOrPauseListener) {
        this.playOrPauseListener = playOrPauseListener;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = (float) (3.6 * progress);
        postInvalidate();
    }

    public synchronized float getStartRefreshAngle() {
        return startRefreshAngle;
    }

    public synchronized void setStartRefreshAngle(float startRefreshAngle) {
        this.startRefreshAngle = startRefreshAngle;
        postInvalidate();
    }

    public synchronized void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
        postInvalidate();
    }

    public float getRefreshAngle() {
        return refreshAngle;
    }

    public void setRefreshAngle(float refreshAngle) {
        this.refreshAngle = refreshAngle;
        postInvalidate();
    }

    public void UpdateStatue(boolean isPlay) {
        this.setChecked(!isPlay);
        postInvalidate();
    }

    public interface OnPlayOrPauseListener {
        void play();

        void pause();
    }

}


