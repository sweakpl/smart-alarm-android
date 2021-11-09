package com.sweak.smartalarm.ui;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatButton;

public class PopButton extends AppCompatButton {

    private float actualScale = 1f;
    private ValueAnimator animationDown, animationUp;

    public PopButton(Context context) {
        super(context, null, android.R.attr.buttonStyle);
        initPopButton();
    }

    public PopButton(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.buttonStyle);
        initPopButton();
    }

    public PopButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPopButton();
    }

    private void initPopButton() {
        float popScale = 1.05f;

        ValueAnimator.AnimatorUpdateListener listener = valueAnimator -> {
            actualScale = (float) valueAnimator.getAnimatedValue();
            invalidate();
        };
        animationDown = ValueAnimator.ofFloat(
                actualScale, actualScale + (actualScale - popScale)
        );
        animationUp = ValueAnimator.ofFloat(
                actualScale + (actualScale - popScale), popScale, actualScale
        );
        animationDown.setDuration(250).addUpdateListener(listener);
        animationUp.setDuration(250).addUpdateListener(listener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setScaleX(actualScale);
        setScaleY(actualScale);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            animationDown.start();
            animationUp.end();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            animationDown.end();
            animationUp.start();
        }

        return true;
    }
}
