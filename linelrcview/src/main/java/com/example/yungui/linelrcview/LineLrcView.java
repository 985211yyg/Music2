package com.example.yungui.linelrcview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import java.util.List;

/**
 * Created by yungui on 2017/11/29.
 * 单行歌词显示控件
 * 需求：1.可以灵活设置切换动画，比如歌词淡入淡出，从顶部出底部进入
 * 2.可以单独添加文字作为水平或者垂直滚动条，如京东的垂直滚动条
 * 3.作为歌词控件展示时，能够逐字渐变歌词
 */

public class LineLrcView extends View implements View.OnClickListener {
    public static final String TAG = LineLrcView.class.getSimpleName();
    public static final int MSG = 0X123;
    public static final int DEF_LRC_COLOR = Color.parseColor("#f5f5f5");
    public static final int DEF_SINGING_LRC_COLOR = Color.parseColor("#30be7a");
    public static final int DEF_DURATION = 30000;//一秒钟
    public static final int DEF_LRC_TEXTSIZE = 16;//一秒钟
    private String hint = "聆听天籁";

    private LrcInfo lrcInfo;
    private Context mContext;
    private String[] textContents;

    private int lrcTextSize;
    private long duration;
    private int lrcColor;
    private long currentTime = 0;
    private String currentLrc = hint;
    private int lrcLineCount;
    private float mTextWidth, mTextHeight;
    private Paint lrcPaint;
    private Paint.FontMetrics fontMetrics;
    private Bitmap textBitmap;
    private float progress;//歌词的进度
    private ValueAnimator valueAnimator;
    //是否是新的歌词
    private boolean isNewLrc;
    private boolean isAnimatorRunning = false;
    //歌词演唱延时时间
    private long delayTime = 0;
    private int index = 0;
    private long lastDuration = 0;

    private OnClickListener onClickListener;


    public LineLrcView(Context context) {
        this(context, null);
    }

    public LineLrcView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineLrcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setOnClickListener(this);
        initAttrs(context, attrs);
        initComponent();
        //确定绘制的歌词，一行歌词的时间
        initLrc();
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LineLrcView);
        lrcTextSize = (int) typedArray.getDimension(R.styleable.LineLrcView_lrcTextSize, applyDimension(TypedValue.COMPLEX_UNIT_SP, DEF_LRC_TEXTSIZE));
        duration = (int) typedArray.getDimension(R.styleable.LineLrcView_duration, DEF_DURATION);
        lrcColor = typedArray.getColor(R.styleable.LineLrcView_lrcColor, DEF_LRC_COLOR);
        typedArray.recycle();

    }

    private void initComponent() {
        lrcPaint = new Paint();
        lrcPaint.setAntiAlias(true);
        lrcPaint.setColor(lrcColor);
        lrcPaint.setTextSize(lrcTextSize);

        lrcPaint.setTextSize(applyDimension(TypedValue.COMPLEX_UNIT_SP, DEF_LRC_TEXTSIZE));
        lrcPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        lrcPaint.setTextAlign(Paint.Align.LEFT);
        //文字精确高度
        fontMetrics = lrcPaint.getFontMetrics();
    }

    /**
     * 初始化歌词和动画
     * 歌词：1、时间+歌词 2、时间+null 3、连续两个 时间+null
     */
    private void initLrc() {
        for (int i = 0; i < lrcLineCount; i++) {
            LineInfo lineInfo = lrcInfo.getLineInfos().get(i);
            //除去空歌词行
            if (lineInfo.getStartTime() >= currentTime) {
                if (i > 0) {
                    index = i - 1;
                } else {
                    index = i;
                }
                duration = lrcInfo.getLineInfos().get(i + 1).getDuration();
                currentLrc = lrcInfo.getLineInfos().get(index).getLrc();
                //测量歌词文字的宽高
                mTextHeight = fontMetrics.bottom - fontMetrics.descent - fontMetrics.ascent;
                mTextWidth = lrcPaint.measureText(currentLrc);
                if (lastDuration != duration) {
                    isNewLrc = true;
                    initAnimator(0, mTextWidth);
                    Log.e(TAG, "当前时间" + covertTime(currentTime));
                }
                lastDuration = duration;

                //找到第一行对应的歌词就跳出就循环,不然会一直循环到最后一个
                break;
            }
        }
    }

    private String covertTime(long currentTime) {
        return currentTime / 1000 / 60 + ":" + currentTime / 1000 % 60;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 40;
        int width = 300;
        //获取宽的测量模式和大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //获取干的测量模式和大小
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT
                && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension((int) applyDimension(TypedValue.COMPLEX_UNIT_DIP, width), (int) applyDimension(TypedValue.COMPLEX_UNIT_DIP, height));

        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(width, heightSize);

        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, height);
        } else {
            setMeasuredDimension(widthSize, heightSize);
        }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        //创建目标底图
        textBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        //在底图上绘制歌词
        Canvas c = new Canvas(textBitmap);
        c.drawText(currentLrc, getMeasuredWidth() / 2 - mTextWidth / 2, getMeasuredHeight() / 2 + mTextHeight / 2, lrcPaint);

        //在绘制一个一样大小的矩形，颜色为高亮颜色，使用xfermMode SRC_IN
        lrcPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        lrcPaint.setColor(DEF_SINGING_LRC_COLOR);
        //有进度控制宽度的矩形
        Rect srcRect = new Rect(0, 0, (int) progress, getMeasuredHeight());
        c.drawRect(srcRect, lrcPaint);
        //将底图绘制在画布上
        canvas.drawBitmap(textBitmap, 0, 0, null);
        lrcPaint.setXfermode(null);
    }


    private void initAnimator(float StartX, float EndX) {
        Log.e(TAG, "initAnimator:初始化歌词成功 ");
        valueAnimator = ValueAnimator.ofFloat(StartX, EndX);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.e(TAG, "onAnimationStart: ");
                isAnimatorRunning = true;

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.e(TAG, "onAnimationStart: ");
                isNewLrc = false;
                isAnimatorRunning = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.e(TAG, "onAnimationStart: ");


            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                initComponent();
                //只是重新调用onDraw()方法，所以要提起重新初始化组件
                invalidateView();
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        valueAnimator = null;
    }

    /**
     * 转换尺寸
     *
     * @param unit TypedValue.COMPLEX_UNIT_SP ...
     * @param size
     * @return
     */
    private float applyDimension(int unit, float size) {
        Resources r;
        if (mContext == null) {
            r = Resources.getSystem();
        } else {
            r = mContext.getResources();
        }
        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());

    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            //当期那线程是主线程
            invalidate();
        } else {
            //不是UI主线程
            postInvalidate();
        }
    }


    /**
     * =========================对外公开的方法========================
     *
     */

    /**
     * 根据时间更新歌词
     *
     * @param millisTime
     */
    public void setCurrentTime(long millisTime) {
        this.currentTime = millisTime;
        //更新时间之后再次初始化歌词
        initLrc();
        if (isNewLrc && !isAnimatorRunning) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    startAnimator();
                }
            }, delayTime);
        }
        invalidateView();
    }

    public String getCurrentLrc() {
        return currentLrc;
    }

    public void startAnimator() {
        if (valueAnimator != null) {
            valueAnimator.start();
        }
    }

    public void stopAnimator() {
        if (valueAnimator.isRunning()) {
            valueAnimator.pause();
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public LrcInfo getLrcInfo() {
        return lrcInfo;
    }

    public void setLrcInfo(LrcInfo lrcInfo) {
        if (lrcInfo != null && lrcInfo.getLineInfos().size() > 0) {
            lrcLineCount = lrcInfo.getLineInfos().size() - 1;
            this.lrcInfo = lrcInfo;
            initLrc();

            invalidateView();
        }
    }

    public int getLrcTextSize() {
        return lrcTextSize;
    }

    public void setLrcTextSize(int lrcTextSize) {
        this.lrcTextSize = (int) applyDimension(TypedValue.COMPLEX_UNIT_SP, lrcTextSize);
        invalidateView();
    }


    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        invalidateView();
    }

    public int getLrcColor() {
        return lrcColor;
    }

    public void setLrcColor(@ColorRes int lrcColor) {
        this.lrcColor = mContext.getResources().getColor(lrcColor);
        invalidateView();
    }

    public long getCurrentTime() {
        return currentTime;
    }


    public void setCurrentLrc(String currentLrc) {
        this.currentLrc = currentLrc;
        invalidateView();
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
        invalidateView();
    }


    public void setTextContent(List<String> textList) {
        if (textList.size() <= 1) {
            throw new IllegalArgumentException("list.size() must more than 1 ");
        }
        for (int i = 0; i < textList.size(); i++) {
            textContents[i] = textList.get(i);
        }
    }

    public void setTextContent(String[] textContents) {
        if (textContents.length <= 1) {
            throw new IllegalArgumentException("String[].length() must more than 1 ");
        }
        this.textContents = textContents;
    }

    public void setTextContent(@ArrayRes int resourcesID) {
        textContents = mContext.getResources().getStringArray(resourcesID);
        if (textContents.length <= 1) {
            throw new IllegalArgumentException("String[].length() must more than 1 ");
        }
    }


    @Override
    public void onClick(View v) {
        if (onClickListener != null) {
            onClickListener.onClick(v);
        }
    }

    public interface OnClickListener {
        void onClick(View view);
    }

}

