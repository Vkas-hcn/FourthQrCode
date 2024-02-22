/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.best.zoom.dou.wb.fourthqrcode.zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.best.zoom.dou.wb.fourthqrcode.R;
import com.best.zoom.dou.wb.fourthqrcode.zxing.camera.CameraManager;

import java.util.Collection;
import java.util.HashSet;


public final class ViewfinderView extends View {

    private static final long ANIMATION_DELAY = 100L;
    private static final int OPAQUE = 0xFF;

    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int resultPointColor;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;

    public ViewfinderView(Context context) {
        this(context, null);
    }

    public ViewfinderView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);

    }

    public ViewfinderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        possibleResultPoints = new HashSet<>(5);

        scanLight = BitmapFactory.decodeResource(resources,
                R.drawable.scan_light);

        initInnerRect(context, attrs);
    }

    private void initInnerRect(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ViewfinderView);
        float innerMarginTop = ta.getDimension(R.styleable.ViewfinderView_inner_margintop, -1);
        if (innerMarginTop != -1) {
            CameraManager.FRAME_MARGINTOP = (int) innerMarginTop;
        }
        CameraManager.FRAME_WIDTH = (int) ta.getDimension(R.styleable.ViewfinderView_inner_width, 200);
        CameraManager.FRAME_HEIGHT = (int) ta.getDimension(R.styleable.ViewfinderView_inner_height, 200);

        innercornercolor = ta.getColor(R.styleable.ViewfinderView_inner_corner_color, Color.parseColor("#45DDDD"));
        innercornerlength = (int) ta.getDimension(R.styleable.ViewfinderView_inner_corner_length, 65);
        innercornerwidth = (int) ta.getDimension(R.styleable.ViewfinderView_inner_corner_width, 15);

        Drawable drawable = ta.getDrawable(R.styleable.ViewfinderView_inner_scan_bitmap);
        if (drawable != null) {
        }

        scanLight = BitmapFactory.decodeResource(getResources(), ta.getResourceId(R.styleable.ViewfinderView_inner_scan_bitmap, R.drawable.scan_light));
        SCAN_VELOCITY = ta.getInt(R.styleable.ViewfinderView_inner_scan_speed, 5);

        isCircle = ta.getBoolean(R.styleable.ViewfinderView_inner_scan_iscircle, true);

        ta.recycle();
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {

            drawFrameBounds(canvas, frame);

            drawScanLight(canvas, frame);

            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<ResultPoint>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);

                if (isCircle) {
                    for (ResultPoint point : currentPossible) {
                        canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
                    }
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);

                if (isCircle) {
                    for (ResultPoint point : currentLast) {
                        canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
                    }
                }
            }

            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    private int scanLineTop;
    private int SCAN_VELOCITY;
    private Bitmap scanLight;
    private boolean isCircle;


    private void drawScanLight(Canvas canvas, Rect frame) {

        if (scanLineTop == 0) {
            scanLineTop = frame.top;
        }

        if (scanLineTop >= frame.bottom - 30) {
            scanLineTop = frame.top;
        } else {
            scanLineTop += SCAN_VELOCITY;
        }
        Rect scanRect = new Rect(frame.left, scanLineTop, frame.right,
                scanLineTop + 30);
        canvas.drawBitmap(scanLight, null, scanRect, paint);
    }


    private int innercornercolor;
    private int innercornerlength;
    private int innercornerwidth;

    private void drawFrameBounds(Canvas canvas, Rect frame) {

        paint.setColor(Color.TRANSPARENT);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        float radius = 20;
        RectF rectF = new RectF(frame.left, frame.top, frame.right, frame.bottom);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        paint.setColor(innercornercolor);
        paint.setStyle(Paint.Style.FILL);

    }


    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }




}
