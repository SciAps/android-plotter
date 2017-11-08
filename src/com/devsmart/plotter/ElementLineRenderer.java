package com.devsmart.plotter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.List;

public class ElementLineRenderer implements DataRenderer {
    public static class XYPair {
        public float x;
        public float y;
        public String atomicSymbol;

        public XYPair(float x, float y, String atomicSymbol) {
            this.x = x;
            this.y = y;
            this.atomicSymbol = atomicSymbol;
        }
    }

    private final List<XYPair> mLinesFromOriginList;
    private final Paint mPaint = new Paint();
    private int mColor;
    private int mYPosition;
    private String mEmissionType;

    public ElementLineRenderer(List<XYPair> linesFromOriginList, String emissionType, int color, int yPosition) {
        mLinesFromOriginList = linesFromOriginList;
        mColor = color;
        mPaint.setColor(color);
        mPaint.setStrokeWidth(1.0f);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mYPosition = yPosition;
        mEmissionType = emissionType;
    }

    @Override
    public void draw(Canvas canvas, RectF viewPort, CoordinateSystem coordSystem) {
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(1.0f);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

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

                mPaint.setTextSize(20);
                mPaint.setStyle(Paint.Style.FILL);

                int shiftPixelLeft = 10;
                if (xyPair.atomicSymbol.length() == 1) {
                    shiftPixelLeft = 5;
                }
                canvas.drawText(xyPair.atomicSymbol, point[0]-shiftPixelLeft, -point[1], mPaint);

                canvas.restore();
            }
        }

        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(canvas.getWidth()-15-55, canvas.getHeight()-mYPosition, canvas.getWidth()-55, canvas.getHeight()-15-mYPosition, mPaint);
        canvas.save();
        canvas.scale(1, -1);
        mPaint.setTextSize(20);
        canvas.drawText(mEmissionType, canvas.getWidth()-50, -(canvas.getHeight()-15-mYPosition), mPaint);
        canvas.restore();
    }

    @Override
    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }
}
