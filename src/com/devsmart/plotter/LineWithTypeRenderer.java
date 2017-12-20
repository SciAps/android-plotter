package com.devsmart.plotter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.List;

public final class LineWithTypeRenderer implements DataRenderer {
    public static class XYPair {
        public float x;
        public float y;
        public String value;
        public String typeOfLine;
        public boolean isViewportHeight;
        public int mColor;

        public XYPair(float x, float y, String value) {
            this.x = x;
            this.y = y;
            this.value = value;
        }
    }

    private final List<XYPair> mLinesFromOriginList;
    private final Paint mPaint = new Paint();

    public LineWithTypeRenderer(List<XYPair> linesFromOriginList) {
        mLinesFromOriginList = linesFromOriginList;
        mPaint.setStrokeWidth(1.0f);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(Canvas canvas, RectF viewPort, CoordinateSystem coordSystem) {
        mPaint.setStrokeWidth(1.0f);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        float[] point = new float[2];
        float[] origin = {0, 0};
        coordSystem.mapPoints(origin);
        for (XYPair xyPair : mLinesFromOriginList) {

            mPaint.setColor(xyPair.mColor);
            point[0] = xyPair.x;

            coordSystem.mapPoints(point);
            canvas.drawLine(point[0], origin[1], point[0], canvas.getHeight()- 50f, mPaint);

            if (xyPair.value != null) {
                canvas.save();
                canvas.scale(1, -1);

                mPaint.setTextSize(20);
                mPaint.setStyle(Paint.Style.FILL);

                int shiftPixelLeft = 10;
                if (xyPair.value.length() == 1) {
                    shiftPixelLeft = 5;
                }
                canvas.drawText(xyPair.value + " (" + xyPair.typeOfLine + ")", point[0]-shiftPixelLeft, -(canvas.getHeight()- 50f), mPaint);

                canvas.restore();
            }
        }
    }

    @Override
    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }
}
