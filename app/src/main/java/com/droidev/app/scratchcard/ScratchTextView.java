/*
  The MIT License

  Copyright (c) 2017 Shajeer Ahamed (info4shajeer@gmail.com)

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
 */

package com.droidev.app.scratchcard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;

/**
 * Author : Shajeer Ahamed KP
 * e-mail : info4shajeer@gmail.com
 * Date : 26-Sep-2018
 * Project : Ipru-Touch
 */
public class ScratchTextView extends AppCompatTextView {


    public interface RevealListener {
        void onRevealed(ScratchTextView tv);

        void onRevealPercentChangedListener(ScratchTextView stv, float percent);
    }

    /**
     * Increase or decrease based on the scratch brush size requirement
     */
    public static final float STROKE_WIDTH = 10f;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    /**
     * Bitmap holding the scratch region.
     */
    private Bitmap mScratchBitmap;

    /**
     * Drawable canvas area through which the scratchable area is drawn.
     */
    private Canvas mCanvas;

    /**
     * Path holding the erasing path done by the user.
     */
    private Path mErasePath;

    /**
     * Path to indicate where the user have touched.
     */
    private Path mTouchPath;

    /**
     * Paint properties for drawing the scratch area.
     */
    private Paint mBitmapPaint;

    /**
     * Paint properties for erasing the scratch region.
     */
    private Paint mErasePaint;

    /**
     * Sample Drawable bitmap having the scratch pattern.
     */
    private BitmapDrawable mDrawable;


    /**
     * Listener object callback reference to send back the callback when the text has been revealed.
     */
    private RevealListener mRevealListener;

    /**
     * Reveal percent value.
     */
    private float mRevealPercent;

    /**
     * Thread Count
     */
    private int mThreadCount = 0;


    public ScratchTextView(Context context) {
        super(context);
        init();

    }

    public ScratchTextView(Context context, AttributeSet set) {
        super(context, set);
        init();
    }

    public ScratchTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Set the strokes width based on the parameter multiplier.
     *
     * @param multiplier can be 1,2,3 and so on to set the stroke width of the paint.
     */
    public void setStrokeWidth(int multiplier) {
        mErasePaint.setStrokeWidth(multiplier * STROKE_WIDTH);
    }

    /**
     * Initialises the paint drawing elements.
     */
    private void init() {
        mTouchPath = new Path();
        mErasePaint = new Paint();
        mErasePaint.setAntiAlias(true);
        mErasePaint.setDither(true);
        mErasePaint.setColor(0xFFFF0000);
        mErasePaint.setStyle(Paint.Style.STROKE);
        mErasePaint.setStrokeJoin(Paint.Join.BEVEL);
        mErasePaint.setStrokeCap(Paint.Cap.ROUND);
        mErasePaint.setXfermode(new PorterDuffXfermode(
                PorterDuff.Mode.CLEAR));
        setStrokeWidth(6);

        mErasePath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        Bitmap scratchBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gold_card);
        mDrawable = new BitmapDrawable(getResources(), scratchBitmap);
//        mDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mScratchBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mScratchBitmap);

        Rect rect = new Rect(0, 0, mScratchBitmap.getWidth(), mScratchBitmap.getHeight());
        mDrawable.setBounds(rect);

        mDrawable.draw(mCanvas);
    }

    /**
     * method can be called to set scratch image, this will be the background set before scratching
     *
     * @param imageId id of drawable to be set as background
     */
    public void setScratchBitmap(@DrawableRes int imageId) {
        Bitmap scratchBitmap = BitmapFactory.decodeResource(getResources(), imageId);
        mDrawable = new BitmapDrawable(getResources(), scratchBitmap);

        Rect rect = new Rect(0, 0, mScratchBitmap.getWidth(), mScratchBitmap.getHeight());
        mDrawable.setBounds(rect);

        mDrawable.draw(mCanvas);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mScratchBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mErasePath, mErasePaint);
    }

    private void touchStart(float x, float y) {
        mErasePath.reset();
        mErasePath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mErasePath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            drawPath();
        }
        mTouchPath.reset();
        mTouchPath.addCircle(mX, mY, 30, Path.Direction.CW);
    }

    private void drawPath() {
        mErasePath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mErasePath, mErasePaint);
        // kill this so we don't double draw
        mTouchPath.reset();
        mErasePath.reset();
        mErasePath.moveTo(mX, mY);
        checkRevealed();
    }

    /**
     * Reveals the hidden text by erasing the scratch area.
     */
    public void reveal() {
        int[] bounds = getTextBounds(15f);
        int left = bounds[0];
        int top = bounds[1];
        int right = bounds[2];
        int bottom = bounds[3];

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(
                PorterDuff.Mode.CLEAR));

        mCanvas.drawRect(left, top, right, bottom, paint);
        checkRevealed();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                drawPath();
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    public int getColor() {
        return mErasePaint.getColor();
    }

    public void setRevealListener(RevealListener listener) {
        this.mRevealListener = listener;
    }

    public boolean isRevealed() {
        return mRevealPercent == 1;
    }

    private void checkRevealed() {

        if (!isRevealed() && mRevealListener != null) {
            int[] bounds = getTextBounds();
            int left = bounds[0];
            int top = bounds[1];
            int width = bounds[2] - left;
            int height = bounds[3] - top;

            // Do not create multiple calls to compare.
            if (mThreadCount > 1) {
                return;
            }
            mThreadCount++;
            new BitmapRevealTask().execute(left, top, width, height);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class BitmapRevealTask extends AsyncTask<Integer, Void, Float> {

        @Override
        protected Float doInBackground(Integer... params) {

            try {
                return BitmapUtils.getTransparentPixelPercent(mScratchBitmap);
            } finally {
                mThreadCount--;
            }
        }

        public void onPostExecute(Float percentRevealed) {
            // check if not revealed before.
            if (!isRevealed()) {
                float oldValue = mRevealPercent;
                mRevealPercent = percentRevealed;

                if (oldValue != percentRevealed) {
                    mRevealListener.onRevealPercentChangedListener(ScratchTextView.this, percentRevealed * 100);
                }
                // if now revealed.
                if (isRevealed()) {
                    mRevealListener.onRevealed(ScratchTextView.this);
                }
            }
        }
    }

    private static int[] getTextDimens(String text, Paint paint) {
        int end = text.length();
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, end, bounds);
        int width = bounds.left + bounds.width();
        int height = bounds.bottom + bounds.height();

        return new int[]{width, height};
    }

    private int[] getTextBounds() {
        return getTextBounds(1f);
    }

    @SuppressLint("RtlHardcoded")
    private int[] getTextBounds(float scale) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int vWidth = getWidth();
        int vHeight = getHeight();

        int centerX = vWidth / 2;
        int centerY = vHeight / 2;

        TextPaint paint = getPaint();

        String text = getText().toString();

        int[] dimens = getTextDimens(text, paint);
        int width = dimens[0];
        int height = dimens[1];

        int lines = getLineCount();
        height = height * lines;
        width = width / lines;

        int left = 0;
        int top = 0;

        if (height > vHeight) {
            height = vHeight - (paddingBottom + paddingTop);
        } else {
            height = (int) (height * scale);
        }

        if (width > vWidth) {
            width = vWidth - (paddingLeft + paddingRight);
        } else {
            width = (int) (width * scale);
        }
        int gravity = getGravity();

        //todo Gravity.START
        if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
            left = paddingLeft;
        }
        //todo Gravity.END
        else if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
            left = (vWidth - paddingRight) - width;
        } else if ((gravity & Gravity.CENTER_HORIZONTAL) == Gravity.CENTER_HORIZONTAL) {
            left = centerX - width / 2;
        }

        if ((gravity & Gravity.TOP) == Gravity.TOP) {
            top = paddingTop;
        } else if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            top = (vHeight - paddingBottom) - height;
        } else if ((gravity & Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL) {
            top = centerY - height / 2;
        }
        return new int[]{left, top, left + width, top + height};
    }

}