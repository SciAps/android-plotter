package com.devsmart.plotter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public final class SimpleAxisRenderer implements AxisRenderer {
    private int numDivisions = 5;
    private boolean mDrawXAxis = true;
    public boolean mDrawYAxis = true;
    public Rect mPlotMargins = new Rect(20, 0, 0, 20);
    private final Paint mAxisLabelPaint = new Paint();
    private final Paint mAxisTickPaint = new Paint();
    private String mXAxisLabel = "Wavelength";
    private String mYAxisLabel = "Intensity";
    private final DisplayMetrics mDisplayMetrics;
    private float[] mYAxis;
    private float[] mXAxis;
    private float[] points = new float[4];
    private Rect mBounds = new Rect();
    private Rect mGraphArea = new Rect();
    private Matrix mMatrix = new Matrix();

    public SimpleAxisRenderer(Context context) {
        mDisplayMetrics = context.getResources().getDisplayMetrics();

        init();
    }

    public SimpleAxisRenderer(GraphView graphview) {
        mDisplayMetrics = graphview.getContext().getResources().getDisplayMetrics();

        init();
    }

    @Override
    public void setAxisColor(int color) {
        mAxisTickPaint.setColor(color);
    }

    @Override
    public void setLabelColor(int color) {
        mAxisLabelPaint.setColor(color);
    }

    @Override
    public void setYAxisLabel(String label) {
        mYAxisLabel = label;
    }

    @Override
    public void setXAxisLabel(String label) {
        mXAxisLabel = label;
    }

    @Override
    public void drawAxis(Canvas canvas, final int canvasWidth, final int canvasHeight, RectF viewPort, CoordinateSystem coordSystem) {
        measureGraphArea(canvasWidth, canvasHeight);

        mMatrix.setRectToRect(new RectF(0, 0, mGraphArea.width(), mGraphArea.height()), new RectF(mGraphArea), ScaleToFit.FILL);
        mMatrix.postScale(1, -1);
        mMatrix.postTranslate(0, mGraphArea.height());

        //Debug axis display
        //canvas.drawText(viewPort.toString(), 50, 50, mAxisTickPaint);

        if (mDrawXAxis) {
            //draw axis
            canvas.drawLines(mXAxis, mAxisTickPaint);

            //draw label
            mAxisLabelPaint.getTextBounds(mXAxisLabel, 0, mXAxisLabel.length(), mBounds);
            float y = canvasHeight - mPlotMargins.bottom + mBounds.bottom + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, mDisplayMetrics);
            canvas.drawText(mXAxisLabel, (mXAxis[2] - mXAxis[0]) / 2 - mBounds.width() / 2 + mXAxis[0], y, mAxisLabelPaint);

            //draw ticks
            final float dist = viewPort.width() / numDivisions;
            float xPoint = (float) (dist * Math.floor(viewPort.left / dist));
            while (xPoint < viewPort.right + dist) {
                points[0] = xPoint;
                points[1] = 0;
                points[2] = xPoint;
                points[3] = 0;
                coordSystem.mapPoints(points);
                mMatrix.mapPoints(points);
                points[1] = mXAxis[1];
                points[3] = mXAxis[1] - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mDisplayMetrics);

                if (points[0] >= mXAxis[0]) {
                    canvas.drawLines(points, mAxisTickPaint);

                    String label = getTickLabel(xPoint);
                    mAxisTickPaint.getTextBounds(label, 0, label.length(), mBounds);

                    canvas.drawText(label, points[0] - mBounds.width() / 2, points[1] + mBounds.height() + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mDisplayMetrics), mAxisTickPaint);
                }

                xPoint += dist;
            }
        }

        if (mDrawYAxis) {
            //draw Y axis
            canvas.drawLines(mYAxis, mAxisTickPaint);

            //draw label
            mAxisLabelPaint.getTextBounds(mYAxisLabel, 0, mYAxisLabel.length(), mBounds);
            canvas.save();
            points[0] = mPlotMargins.left;
            points[1] = (mYAxis[3] - mYAxis[1]) / 2 + mBounds.width() / 2;
            canvas.rotate(-90, points[0], points[1]);
            canvas.drawText(mYAxisLabel, points[0], points[1], mAxisLabelPaint);
            canvas.restore();

            final float dist = viewPort.height() / numDivisions;
            float yPoint = (float) (dist * Math.floor(viewPort.top / dist));
            while (yPoint < viewPort.bottom + dist) {
                points[0] = 0;
                points[1] = yPoint;
                points[2] = 0;
                points[3] = yPoint;
                coordSystem.mapPoints(points);
                mMatrix.mapPoints(points);
                points[0] = mYAxis[0];
                points[2] = mYAxis[0] + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mDisplayMetrics);

                if (points[1] <= mYAxis[3]) {
                    canvas.drawLines(points, mAxisTickPaint);

                    String label = getTickLabel(yPoint);
                    mAxisTickPaint.getTextBounds(label, 0, label.length(), mBounds);
                    canvas.save();
                    points[2] = points[0] - mBounds.height() / 2 - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mDisplayMetrics);
                    points[3] = points[1] + mBounds.width() / 2;
                    canvas.rotate(-90, points[2], points[3]);
                    canvas.drawText(label, points[2], points[3], mAxisTickPaint);
                    canvas.restore();
                }

                yPoint += dist;
            }
        }
    }

    @Override
    public Rect measureGraphArea(int screenWidth, int screenHeight) {
        calcBounds(screenWidth, screenHeight);

        mGraphArea.left = (int) Math.floor(mXAxis[0]);
        mGraphArea.right = (int) Math.ceil(mXAxis[2]);
        mGraphArea.top = (int) Math.floor(mYAxis[1]);
        mGraphArea.bottom = (int) Math.ceil(mYAxis[3]);

        return mGraphArea;
    }

    private void init() {
        mAxisLabelPaint.setColor(Color.BLACK);
        mAxisLabelPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, mDisplayMetrics));
        mAxisLabelPaint.setAntiAlias(true);

        mAxisTickPaint.setColor(Color.DKGRAY);
        mAxisTickPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, mDisplayMetrics));
        mAxisTickPaint.setAntiAlias(true);
    }

    private void calcBounds(final int canvasWidth, final int canvasHeight) {
        mAxisLabelPaint.getTextBounds("1", 0, 1, mBounds);
        float axisLabelHeight = mBounds.height();

        mAxisTickPaint.getTextBounds("1", 0, 1, mBounds);
        float tickLabelHeight = mBounds.height();

        float axisLabelBoundery = axisLabelHeight + tickLabelHeight + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, mDisplayMetrics);

        float height = canvasHeight - axisLabelBoundery - mPlotMargins.height();

        mYAxis = new float[]{axisLabelBoundery + mPlotMargins.left, mPlotMargins.top, axisLabelBoundery + mPlotMargins.left, mPlotMargins.top + height};

        mXAxis = new float[]{axisLabelBoundery + mPlotMargins.left, canvasHeight - axisLabelBoundery - mPlotMargins.bottom, canvasWidth - mPlotMargins.right, canvasHeight - axisLabelBoundery - mPlotMargins.bottom};
    }

    private String getTickLabel(float value) {
        return String.format("%g", value);
    }
}