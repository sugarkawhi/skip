package me.sugarkahwi.skipcountdownbutton;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by sugarkawhi on 2017/8/26.
 */

public class SkipCountDownButton extends View {

    private Paint mBackgroundPaint;
    private Paint mEdgePaint;
    private Paint mTextPaint;
    private Path mEdgePath;

    // 背景色
    private int backgroundColor;
    //边框颜色
    private int edgeColor;
    //字体颜色
    private int textColor;
    //背景圆的半径
    private float radius;
    //边框宽度
    private float edgeWith;
    //字体大小
    private int textSize;
    //倒计时时间
    private int countDownMilliSecond;
    //文字：默认跳过
    private String text;

    private static final String DEFAULT_BACKGROUND_COLOR = "#80000000";
    private static final String DEFAULT_EDGE_COLOR = "#FF0000";
    private static final String DEFAULT_TEXT_COLOR = "#FFFFFF";
    private static final int DEFAULT_COUNT_DOWN_MILLISECOND = 3000;
    //默认WRAP_CONTENT下View的大小
    private static final int DEFAULT_WIDTH_SIZE = 120;
    //默认边框宽度
    private static final int DEFAULT_EDGE_WIDTH = 10;

    //中心X
    private float mCenterX;
    //中心Y
    private float mCenterY;
    //动画
    private ValueAnimator mValueAnimator;
    //截取edge
    private PathMeasure mEdgePathMeasure;
    //截取的子edge
    private Path mSubEdgePath;


    private OnSkipCountDownListener onSkipCountDownListener;

    public SkipCountDownButton(Context context) {
        this(context, null);
    }

    public SkipCountDownButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkipCountDownButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setOnCountDownStopListener(OnSkipCountDownListener onSkipCountDownListener) {
        this.onSkipCountDownListener = onSkipCountDownListener;
    }

    /**
     * 初始化
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SkipCountDownButton);
        backgroundColor = array.getColor(R.styleable.SkipCountDownButton_backgroundColor, Color.parseColor(DEFAULT_BACKGROUND_COLOR));
        edgeColor = array.getColor(R.styleable.SkipCountDownButton_edgeColor, Color.parseColor(DEFAULT_EDGE_COLOR));
        textColor = array.getColor(R.styleable.SkipCountDownButton_textColor, Color.parseColor(DEFAULT_TEXT_COLOR));
        textSize = array.getDimensionPixelSize(R.styleable.SkipCountDownButton_textSize,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));

        text = array.getString(R.styleable.SkipCountDownButton_text);
        if (null == text) {
            text = "跳过";//默认是跳过
        }
        countDownMilliSecond = array.getInt(R.styleable.SkipCountDownButton_millisecond, DEFAULT_COUNT_DOWN_MILLISECOND);
        edgeWith = array.getDimensionPixelSize(R.styleable.SkipCountDownButton_edgeWidth, DEFAULT_EDGE_WIDTH);
        array.recycle();
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(backgroundColor);

        mEdgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEdgePaint.setStyle(Paint.Style.STROKE);
        mEdgePaint.setColor(edgeColor);
        mEdgePaint.setStrokeCap(Paint.Cap.ROUND);
        mEdgePaint.setStrokeWidth(edgeWith);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);

        mEdgePath = new Path();
        mSubEdgePath = new Path();
        mEdgePathMeasure = new PathMeasure();

        initValueAnimator();
    }

    /**
     * 初始化动画
     */
    private void initValueAnimator() {
        mValueAnimator = ValueAnimator.ofFloat(1.0f, 0);
        mValueAnimator.setDuration(countDownMilliSecond);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (onSkipCountDownListener != null)
                    onSkipCountDownListener.onSkip();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //这里不管高度 设置宽度和高度相等
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            //设置一个默认宽度
            widthSize = Math.max(0, DEFAULT_WIDTH_SIZE - getPaddingLeft() - getPaddingRight());
            setMeasuredDimension(widthSize, widthSize);
        } else {
            setMeasuredDimension(widthSize, widthSize);
        }

        radius = widthSize / 2 - edgeWith ;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //先画背景
        doDrawBackground(canvas);
        //再画边框
        doDrawEdge(canvas);
        //最后画文字
        doDrawText(canvas);
    }

    /**
     * 画背景
     */
    private void doDrawBackground(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, radius, mBackgroundPaint);
    }

    /**
     * 画边框
     */
    private void doDrawEdge(Canvas canvas) {
        mEdgePath.reset();
        mSubEdgePath.reset();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mEdgePath.addArc(mCenterX - radius - edgeWith / 2, mCenterY - radius - edgeWith / 2,
                    mCenterX + radius + edgeWith / 2, mCenterY + radius + edgeWith / 2,
                    -90, 359.9f);
            //截取
            mEdgePathMeasure.setPath(mEdgePath, false);
            float animValue = (float) mValueAnimator.getAnimatedValue();
            Log.e("TAG", "doDrawEdge: " + mValueAnimator.isRunning() + " >> " + animValue);
            if (animValue != 0f) {
                float length = mEdgePathMeasure.getLength();
                mEdgePathMeasure.getSegment(length - (animValue * length),
                        length,
                        mSubEdgePath,
                        true);
                canvas.drawPath(mSubEdgePath, mEdgePaint);
            }
        }
    }

    private Paint.FontMetrics metrics;
    private float fontWidth;

    /**
     * 画文字
     */
    private void doDrawText(Canvas canvas) {
        metrics = mTextPaint.getFontMetrics();
        fontWidth = mTextPaint.measureText(text);
        canvas.drawText(text, mCenterX - fontWidth / 2, mCenterY + Math.abs(metrics.ascent + metrics.descent) / 2, mTextPaint);
    }

    public void start() {
        mValueAnimator.cancel();
        mValueAnimator.start();
    }

    public void skip() {
        mValueAnimator.cancel();
        if (onSkipCountDownListener != null)
            onSkipCountDownListener.onSkip();
    }

    public interface OnSkipCountDownListener {
        void onSkip();
    }

    @Override
    protected void onDetachedFromWindow() {
        mValueAnimator.cancel();
        super.onDetachedFromWindow();
    }
}

