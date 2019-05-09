package com.example.rcmatrix;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

// following a fantastic tutorial by @ssaurel:
// https://www.ssaurel.com/blog/learn-to-create-a-paint-application-for-android/
public class PaintView extends View {

    // default values for stroke properties
    private static final int DEFAULT_FG_COLOR = Color.parseColor("#212121");
    private static final int DEFAULT_BG_COLOR = Color.parseColor("#fafafa");
    private static final int DEFAULT_STROKE_WIDTH = 20;

    // stroke properties
    private int mFgColor;
    private int mBgColor;
    private int mStrokeWidth;

    private final ArrayList<DrawPath> mDrawPaths = new ArrayList<>();
    private float mXPosition;
    private float mYPosition;
    private Path mPath;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private final Paint mPaint;
    private final Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // set up style
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_FG_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

        // use defaults
        mFgColor = DEFAULT_FG_COLOR;
        mBgColor = DEFAULT_BG_COLOR;
        mStrokeWidth = DEFAULT_STROKE_WIDTH;
    }

    // initialize canvas using bitmap
    public void initCanvas(int width, int height) {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    // clear all paths
    public void clear() {
        mDrawPaths.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(mBgColor);

        // redraw all paths
        for (DrawPath drawPath : mDrawPaths) {
            mPaint.setColor(drawPath.color);
            mPaint.setStrokeWidth(drawPath.strokeWidth);
            mPaint.setMaskFilter(null);
            mCanvas.drawPath(drawPath.path, mPaint);
        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        DrawPath drawPath = new DrawPath(mFgColor, mStrokeWidth, mPath);
        mDrawPaths.add(drawPath);
        mPath.reset();
        mPath.moveTo(x, y);
        mXPosition = x;
        mYPosition = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mXPosition);
        float dy = Math.abs(y - mYPosition);
        float tolerance = 4;
        if (dx >= tolerance || dy >= tolerance) {
            mPath.quadTo(mXPosition, mYPosition, (x + mXPosition) / 2, (y + mYPosition) / 2);
            mXPosition = x;
            mYPosition = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mXPosition, mYPosition);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    public void setStrokeWidth(int strokeWidth) {
        mStrokeWidth = (strokeWidth + 1) * DEFAULT_STROKE_WIDTH;
    }

    public void setFgColor(int color) {
        mFgColor = color;
    }

    public void setBgColor(int color) {
        mBgColor = color;
        mCanvas.drawColor(mBgColor);

        // redraw all paths
        for (DrawPath drawPath : mDrawPaths) {
            mPaint.setColor(drawPath.color);
            mPaint.setStrokeWidth(drawPath.strokeWidth);
            mPaint.setMaskFilter(null);
            mCanvas.drawPath(drawPath.path, mPaint);
        }
    }

    public int getFgColor() {
        return mFgColor;
    }

    public int getBgColor() {
        return mBgColor;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    private class DrawPath {

        private final int color;
        private final int strokeWidth;
        private final android.graphics.Path path;

        DrawPath(int color, int strokeWidth, android.graphics.Path path) {
            this.color = color;
            this.strokeWidth = strokeWidth;
            this.path = path;
        }
    }
}