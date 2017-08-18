package com.monkey.miclockview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by a123 on 2017/8/2.
 */

public class TemView extends View {
    float data=0;

    /* 半径，不包括padding值 */
    private float mRadius;
    /* 加一个默认的padding值，为了防止用camera旋转时钟时造成四周超出view大小 */
    private float mDefaultPadding;
    private float mPaddingLeft;
    private float mPaddingTop;
    private float mPaddingRight;
    private float mPaddingBottom;
    /* 刻度线长度 */
    private float mScaleLength;
    /* 刻度圆弧画笔 */
    private Paint mScaleArcPaint;
    /* 刻度线画笔 */
    private Paint mScaleLinePaint;
    /* 指针的最大位移 */
    private float mMaxCanvasTranslate;
    /* 梯度扫描渐变 */
    private SweepGradient mSweepGradient;
    /* 亮色，用于分针、秒针、渐变终止色 */
    private int mLightColor;
    /* 暗色，圆弧、刻度线、时针、渐变起始色 */
    private int mDarkColor;

    /* 指针的在x轴的位移 */
    private float mCanvasTranslateX=12;
    /* 指针的在y轴的位移 */
    private float mCanvasTranslateY=12;
    /* 刻度圆弧的外接矩形 */
    private RectF mScaleArcRectF;
    /* 渐变矩阵，作用在SweepGradient */
    private Matrix mGradientMatrix;

    public TemView(Context context) {
        super(context);
    }

    public TemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TemView, defStyleAttr, 0);
//        mBackgroundColor = ta.getColor(R.styleable.MiClockView_backgroundColor, Color.parseColor("#237EAD"));
//        setBackgroundColor(mBackgroundColor);
        mLightColor = ta.getColor(R.styleable.TemView_lColor, Color.parseColor("#f44141"));
        mDarkColor = ta.getColor(R.styleable.TemView_dColor, Color.parseColor("#80ffffff"));
//        mTextSize = ta.getDimension(R.styleable.MiClockView_textSize, DensityUtils.sp2px(context, 14));
        ta.recycle();

        init();

    }

    private void init() {
        mScaleLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScaleLinePaint.setStyle(Paint.Style.STROKE);
        mScaleLinePaint.setColor(Color.parseColor("#237EAD"));

        mScaleArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScaleArcPaint.setStyle(Paint.Style.STROKE);
        mScaleArcPaint.setColor(Color.parseColor("#f44141"));

        mScaleArcRectF = new RectF();
        mGradientMatrix = new Matrix();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureDimension(widthMeasureSpec), measureDimension(heightMeasureSpec));
    }

    private int measureDimension(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = 800;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }





    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //宽和高分别去掉padding值，取min的一半即表盘的半径
        mRadius = Math.min(w - getPaddingLeft() - getPaddingRight(),
                h - getPaddingTop() - getPaddingBottom()) / 2;
        mDefaultPadding = 0.12f * mRadius;//根据比例确定默认padding大小
        mPaddingLeft = mDefaultPadding ;
        mPaddingTop = mDefaultPadding;
        mPaddingRight = mPaddingLeft;
        mPaddingBottom = mPaddingTop;
        mScaleLength = 0.15f * mRadius;//根据比例确定刻度线长度
        mScaleArcPaint.setStrokeWidth(mScaleLength);
        mScaleLinePaint.setStrokeWidth(0.060f * mRadius);
        mMaxCanvasTranslate = 0.02f * mRadius;
        //梯度扫描渐变，以(w/2,h/2)为中心点，两种起止颜色梯度渐变
        //float数组表示，[0,0.75)为起始颜色所占比例，[0.75,1}为起止颜色渐变所占比例
        mSweepGradient = new SweepGradient(w / 2, h / 2,
                new int[]{mDarkColor, mLightColor}, new float[]{0.75f, 1});
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawScaleLine(canvas);
        invalidate();
    }
    /**
     * 画一圈梯度渲染的亮暗色渐变圆弧，重绘时不断旋转，上面盖一圈背景色的刻度线
     */


    private void drawScaleLine(Canvas mCanvas) {
        mCanvas.save();
        mCanvas.translate(mCanvasTranslateX, mCanvasTranslateY);
        mScaleArcRectF.set(mPaddingLeft + 1.5f * mScaleLength ,
                mPaddingTop + 1.5f * mScaleLength ,
                getWidth() - mPaddingRight  - 1.5f * mScaleLength,
                getHeight() - mPaddingBottom  - 1.5f * mScaleLength);
        //matrix默认会在三点钟方向开始颜色的渐变，为了吻合钟表十二点钟顺时针旋转的方向，把秒针旋转的角度减去90度
        mGradientMatrix.setRotate(data-225, getWidth() / 2, getHeight() / 2);
        mSweepGradient.setLocalMatrix(mGradientMatrix);
        mScaleArcPaint.setShader(mSweepGradient);
        mCanvas.drawArc(mScaleArcRectF, -225, 270, false, mScaleArcPaint);
        //画背景色刻度线
        for (int i = 0; i < 50; i++) {
            mCanvas.drawLine(getWidth() / 2, mPaddingTop + mScaleLength-5 ,
                    getWidth() / 2, mPaddingTop + 2 * mScaleLength+5, mScaleLinePaint);
            mCanvas.rotate(7.2f, getWidth() / 2, getHeight() / 2);
        }

        data++;
        if (data>270){
            data=0;
        }
        mCanvas.restore();
    }
}
