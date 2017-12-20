package com.devsmart.plotter;

/**
 * Created by timnguyen on 12/20/17.
 */

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.List;

public final class LegendRenderer implements DataRenderer {
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

    private final Paint mPaint = new Paint();
    private int mColor;
    private int mYPosition;
    private String mTitle;

    private final int LEGEND_PADDING_LEFT = 55;
    private final int LEGEND_DIMENSION = 15;
    private final int TEXT_PADDING_LEFT = 50;

    public LegendRenderer(String title, int color, int yPosition) {
        mColor = color;
        mPaint.setColor(color);
        mPaint.setStrokeWidth(1.0f);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mYPosition = yPosition;
        mTitle = title;
    }

    @Override
    public void draw(Canvas canvas, RectF viewPort, CoordinateSystem coordSystem) {
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(1.0f);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        float[] origin = {0, 0};
        coordSystem.mapPoints(origin);

        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(canvas.getWidth()-LEGEND_DIMENSION-LEGEND_PADDING_LEFT, canvas.getHeight()-mYPosition, canvas.getWidth()-LEGEND_PADDING_LEFT, canvas.getHeight()-LEGEND_DIMENSION-mYPosition, mPaint);
        canvas.save();
        canvas.scale(1, -1);
        mPaint.setTextSize(20);
        canvas.drawText(mTitle, canvas.getWidth()-TEXT_PADDING_LEFT, -(canvas.getHeight()-LEGEND_DIMENSION-mYPosition), mPaint);
        canvas.restore();
    }

    @Override
    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }
}
