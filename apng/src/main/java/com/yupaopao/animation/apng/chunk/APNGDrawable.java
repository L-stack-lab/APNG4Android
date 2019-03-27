package com.yupaopao.animation.apng.chunk;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: 作用描述
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public class APNGDrawable extends Drawable implements Animatable {
    private static final String TAG = APNGDrawable.class.getSimpleName();
    private final Handler animationHandler;
    private final Handler uiHandler;
    private final Paint paint = new Paint();
    private ApngDecoder apngDecoder;
    private int frameIndex = 0;

    private boolean running;
    private Bitmap currentBitmap;

    private Runnable renderTask = new Runnable() {
        @Override
        public void run() {
            Frame frame = apngDecoder.getFrame(frameIndex);
            if (frame == null) {
                Log.e(TAG, "Cannot find frame at index:" + frameIndex);
            } else {
                currentBitmap = frame.toBitmap();
            }
            animationHandler.postDelayed(this, 100);
            uiHandler.post(invalidateRunnable);
        }
    };

    private Runnable invalidateRunnable = new Runnable() {
        @Override
        public void run() {
            invalidateSelf();
        }
    };

    public APNGDrawable(final InputStream inputStream) {
        paint.setAntiAlias(true);
        HandlerThread handlerThread = new HandlerThread("apng");
        handlerThread.start();
        animationHandler = new Handler(handlerThread.getLooper());
        animationHandler.post(new Runnable() {
            @Override
            public void run() {
                apngDecoder = new ApngDecoder(inputStream);
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        uiHandler = new Handler();
    }


    @Override
    public void start() {
        if (!isRunning()) {
            running = true;
            animationHandler.removeCallbacks(renderTask);
            uiHandler.removeCallbacks(invalidateRunnable);
            animationHandler.post(renderTask);
        }
    }

    @Override
    public void stop() {
        running = false;
        animationHandler.removeCallbacksAndMessages(null);
        uiHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void draw(Canvas canvas) {
        if (currentBitmap != null && !currentBitmap.isRecycled()) {
            canvas.drawBitmap(currentBitmap, 0, 0, paint);
        }
        frameIndex++;
        if (frameIndex > 10) {
            frameIndex = 0;
        }
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}