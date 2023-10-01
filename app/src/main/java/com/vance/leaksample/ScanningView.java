package com.vance.leaksample;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import org.jetbrains.annotations.NotNull;


public class ScanningView extends View implements LifecycleObserver {
    private static final long ANIMATOR_DURATION = 400L;

    private Paint mScanPaint;

    private Rect mSrcRect;

    private RectF mDestRest;

    private float mDistance;

    private double mDegrees;

    private ValueAnimator mAnimator;

    private float mStartY;

    private float mTranslateY;

    private Bitmap mBitmap;

    public ScanningView(@NonNull Context context) {
        this(context, null, 0);

    }

    public ScanningView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScanningView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化光图片
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scanning_view_light);
        mSrcRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        mDestRest = new RectF();
        mTranslateY = mStartY = -mBitmap.getHeight();

        //初始化画笔 设置抗锯齿和防抖动
        mScanPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScanPaint.setDither(true);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //计算出控件的对角线夹角角度
        double radians = Math.atan((double) h / (double) w);
        mDegrees = Math.toDegrees(radians);
        //计算出光要移动的距离
        mDistance = (float) (Math.sin(radians) * w * 2);

        //设置光初始绘画位置
        int lightWidth = w > h ? w : h;
        mDestRest.set(-lightWidth, 0, lightWidth * 2, mBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.rotate((float) -mDegrees);
        canvas.translate(-getWidth() / 2, mTranslateY);
        canvas.drawBitmap(mBitmap, mSrcRect, mDestRest, mScanPaint);
        canvas.restore();
    }

    @Override
    protected void onVisibilityChanged(@NonNull @NotNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
//        if (visibility == View.VISIBLE) {
//            startAnimation();
//        } else {
//            stopAnimation();
//        }
    }


    private void initAnimator() {
        mAnimator = ValueAnimator.ofFloat(0, 1f);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.setDuration(ANIMATOR_DURATION);
        mAnimator.setRepeatCount(Animation.INFINITE);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTranslateY = mDistance * (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
    }

    public void startAnimation() {
        if (mAnimator == null) {
            initAnimator();
        } else if (mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        mAnimator.start();
    }


    public void stopAnimation() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        mTranslateY = mStartY;
        postInvalidate();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume() {
        startAnimation();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause() {
        stopAnimation();
    }
}