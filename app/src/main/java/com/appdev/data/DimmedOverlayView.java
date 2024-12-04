package com.appdev.data;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

public class DimmedOverlayView extends View {
    private Paint mPaint;
    private float cutoutX, cutoutY, cutoutRadius;

    public DimmedOverlayView(Context context) {
        super(context);
        init();
    }

    public DimmedOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAlpha(150); // Set alpha for dimming effect
        mPaint.setAntiAlias(true);
        cutoutRadius = 100; // Adjust the radius as needed
    }

    public void setCutout(float x, float y) {
        this.cutoutX = x;
        this.cutoutY = y;
        invalidate(); // Redraw the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the dimming effect
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);

        // Set up the paint for the cutout
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawCircle(cutoutX, cutoutY, cutoutRadius, mPaint);
        mPaint.setXfermode(null); // Reset the paint mode
    }
}
