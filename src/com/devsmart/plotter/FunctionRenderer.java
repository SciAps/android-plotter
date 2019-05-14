package com.devsmart.plotter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class FunctionRenderer implements DataRenderer
{
    public interface GraphFunction
    {
        double value(double x);
    }

    private         double[]      mSampleLocations;
    private         GraphFunction mFunction;
    private         double        mSampleRate = 2;
    protected final Paint         mPointPaint = new Paint();
    float[]  mPoints    = new float[6];
    float[]  mLastPoint = new float[2];
    double[] mYMinMax   = new double[2];

    public FunctionRenderer(GraphFunction f, double[] sampleLocations, int color)
    {
        mFunction = f;
        mSampleLocations = sampleLocations;
        mPointPaint.setColor(color);
        mPointPaint.setStrokeWidth(2.0f);
        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.STROKE);
    }

    private void drawFixedSample(Canvas canvas, RectF viewPort, CoordinateSystem coordSystem)
    {
        //reset min max
        mYMinMax[0] = Double.MAX_VALUE;
        mYMinMax[1] = Double.MIN_VALUE;

        final double pixelWidth = viewPort.width() / (double) canvas.getWidth();
        final double stepWidth = Math.min(pixelWidth, 1.0 / mSampleRate);

        Path p = new Path();

        mPoints[0] = viewPort.left;
        mPoints[1] = (float) mFunction.value(viewPort.left);
        coordSystem.mapPoints(mPoints);
        p.moveTo(mPoints[0], mPoints[1]);

        double startPix = viewPort.left;
        for (double x = startPix; x < viewPort.right; x += stepWidth)
        {
            final double y = mFunction.value(x);
            mYMinMax[0] = Math.min(mYMinMax[0], y);
            mYMinMax[1] = Math.max(mYMinMax[1], y);

            if (x >= startPix + pixelWidth)
            {

                //min
                mPoints[0] = (float) startPix;
                mPoints[1] = (float) mYMinMax[0];

                //max
                mPoints[2] = (float) startPix;
                mPoints[3] = (float) mYMinMax[1];

                coordSystem.mapPoints(mPoints);

                p.lineTo(mPoints[0], mPoints[1]);
                p.lineTo(mPoints[2], mPoints[3]);

                //reset min max
                mYMinMax[0] = Double.MAX_VALUE;
                mYMinMax[1] = Double.MIN_VALUE;

                startPix = x;
            }
        }

        mPoints[0] = viewPort.right;
        mPoints[1] = (float) mFunction.value(viewPort.right);
        coordSystem.mapPoints(mPoints);
        p.lineTo(mPoints[0], mPoints[1]);

        canvas.drawPath(p, mPointPaint);
    }

    private class MinMax
    {
        double min;
        double max;

        public MinMax()
        {
            reset();
        }

        public void reset()
        {
            min = Double.MAX_VALUE;
            max = Double.MIN_VALUE;
        }

        public void add(float value)
        {
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
    }

    private void drawAtSampleLocations(Canvas canvas, RectF viewPort, CoordinateSystem coordSystem)
    {

        Paint pointPaint = new Paint();
        pointPaint.setColor(Color.RED);

        int sampleindex = 0;
        for (int i = 0; i < mSampleLocations.length; i++)
        {
            if (mSampleLocations[i] >= viewPort.left)
            {
                sampleindex = i;
                if (i > 0)
                {
                    sampleindex = i - 1;
                }
                break;
            }
        }

        //reset min max
        mYMinMax[0] = Double.MAX_VALUE;
        mYMinMax[1] = Double.MIN_VALUE;
        MinMax xminmax = new MinMax();
        MinMax yminmax = new MinMax();

        final double pixelWidth = viewPort.width() / (double) canvas.getWidth();

        Path p = new Path();
        Path p2 = new Path();

        mPoints[0] = (float) mSampleLocations[sampleindex];
        mPoints[1] = (float) mFunction.value(mPoints[0]);
        xminmax.add(mPoints[0]);
        yminmax.add(mPoints[1]);

        coordSystem.mapPoints(mPoints);
        p.lineTo(mPoints[0], mPoints[1]);
        p2.moveTo(mPoints[0], mPoints[1]);
        mLastPoint[0] = mPoints[0];
        mLastPoint[1] = mPoints[1];

        double startPix = mSampleLocations[sampleindex];
        double x = 0;
        while (true)
        {
            if (sampleindex >= mSampleLocations.length - 1 || (x = mSampleLocations[sampleindex++ + 1]) > viewPort.right)
            {
                break;
            }

            final double y = mFunction.value(x);

            mPoints[0] = (float) x;
            mPoints[1] = (float) y;
            coordSystem.mapPoints(mPoints);

            canvas.drawCircle(mPoints[0], mPoints[1], 3.0f, pointPaint);

            p.lineTo((mLastPoint[0] + mPoints[0]) / 2, mLastPoint[1]);
            p.lineTo((mLastPoint[0] + mPoints[0]) / 2, mPoints[1]);

            mLastPoint[0] = mPoints[0];
            mLastPoint[1] = mPoints[1];
        }

        p.lineTo(canvas.getWidth(), mLastPoint[1]);

        mPoints[0] = viewPort.right;
        mPoints[1] = (float) mFunction.value(viewPort.right);
        xminmax.add(mPoints[0]);
        yminmax.add(mPoints[1]);
        coordSystem.mapPoints(mPoints);

        p.lineTo(canvas.getWidth(), 0);

        p.close();

        canvas.drawPath(p, mPointPaint);

        //reset min max
        mYMinMax[0] = Double.MAX_VALUE;
        mYMinMax[1] = Double.MIN_VALUE;
        final double stepWidth = Math.min(pixelWidth, 1.0 / mSampleRate);
        startPix = viewPort.left;
        for (x = startPix; x < viewPort.right; x += stepWidth)
        {
            final double y = mFunction.value(x);
            mYMinMax[0] = Math.min(mYMinMax[0], y);
            mYMinMax[1] = Math.max(mYMinMax[1], y);

            if (x >= startPix + pixelWidth)
            {

                //min
                mPoints[0] = (float) startPix;
                mPoints[1] = (float) mYMinMax[0];

                //max
                mPoints[2] = (float) startPix;
                mPoints[3] = (float) mYMinMax[1];

                coordSystem.mapPoints(mPoints);

                p2.lineTo(mPoints[0], mPoints[1]);
                p2.lineTo(mPoints[2], mPoints[3]);

                //reset min max
                mYMinMax[0] = Double.MAX_VALUE;
                mYMinMax[1] = Double.MIN_VALUE;

                startPix = x;
            }
        }

        Paint p2Paint = new Paint();
        p2Paint.setStyle(Paint.Style.STROKE);
        p2Paint.setColor(Color.WHITE);
        p2Paint.setStrokeWidth(2.0f);
        p2Paint.setAntiAlias(true);
        canvas.drawPath(p2, p2Paint);
    }

    @Override
    public void draw(Canvas canvas, RectF viewPort, CoordinateSystem coordSystem)
    {
        if (mSampleLocations == null)
        {
            drawFixedSample(canvas, viewPort, coordSystem);
        }
        else
        {
            drawAtSampleLocations(canvas, viewPort, coordSystem);
        }
    }

    @Override
    public void setPaintColor(int color)
    {
        mPointPaint.setColor(color);
    }
}