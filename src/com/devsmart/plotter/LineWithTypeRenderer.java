package com.devsmart.plotter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.List;

public final class LineWithTypeRenderer implements DataRenderer {
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
    private String mLineType;

    private final int LEGEND_PADDING_LEFT = 55;
    private final int LEGEND_DIMENSION = 15;
    private final int TEXT_PADDING_LEFT = 50;

    public LineWithTypeRenderer(List<XYPair> linesFromOriginList, String lineType, int color, int yPosition) {
        mLinesFromOriginList = linesFromOriginList;
        mColor = color;
        mPaint.setColor(color);
        mPaint.setStrokeWidth(1.0f);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mYPosition = yPosition;
        mLineType = lineType;
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
        canvas.drawRect(canvas.getWidth()-LEGEND_DIMENSION-LEGEND_PADDING_LEFT, canvas.getHeight()-mYPosition, canvas.getWidth()-LEGEND_PADDING_LEFT, canvas.getHeight()-LEGEND_DIMENSION-mYPosition, mPaint);
        canvas.save();
        canvas.scale(1, -1);
        mPaint.setTextSize(20);
        canvas.drawText(mLineType, canvas.getWidth()-TEXT_PADDING_LEFT, -(canvas.getHeight()-LEGEND_DIMENSION-mYPosition), mPaint);
        canvas.restore();
    }

    @Override
    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }
}
