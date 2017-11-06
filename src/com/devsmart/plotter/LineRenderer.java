package com.devsmart.plotter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.List;

/**
 * Created by sgowen on 4/13/15.
 */
public final class LineRenderer implements DataRenderer {
    public static class XYPair {
        public float x;
        public float y;
        public String atomicSymbol;

        public XYPair(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public XYPair(float x, float y, String atomicSymbol) {
            this.x = x;
            this.y = y;
            this.atomicSymbol = atomicSymbol;
        }
    }

    private final List<XYPair> mLinesFromOriginList;
    private final Paint mPaint = new Paint();

    public LineRenderer(List<XYPair> linesFromOriginList, int color) {
        mLinesFromOriginList = linesFromOriginList;
        mPaint.setColor(color);
        mPaint.setStrokeWidth(1.0f);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(Canvas canvas, RectF viewPort, CoordinateSystem coordSystem) {
        float[] point = new float[2];
        float[] origin = {0, 0};
        coordSystem.mapPoints(origin);
        for (XYPair xyPair : mLinesFromOriginList) {
            point[0] = xyPair.x;
            point[1] = xyPair.y;
            coordSystem.mapPoints(point);
            canvas.drawLine(point[0], origin[1], point[0], point[1], mPaint);

            if (xyPair.atomicSymbol != null) {
                canvas.save();
                canvas.scale(1, -1);
                canvas.drawText(xyPair.atomicSymbol, point[0], -point[1], mPaint);
                canvas.restore();
            }
        }
    }

    @Override
    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }
}