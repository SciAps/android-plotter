package com.devsmart.plotter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.sciaps.androidcommon.utils.PeekableIterator;

public class LineGraphDataRenderer implements DataRenderer {
    protected final Paint mPointPaint = new Paint();
    protected Series mSeries;

    public LineGraphDataRenderer(Series series, int color) {
        mSeries = series;
        mPointPaint.setColor(color);
        mPointPaint.setStrokeWidth(2.0f);
    }

    RectF mPixel = new RectF();
    RectF mPixelBin = new RectF();

    public void draw(Canvas canvas, RectF viewPort, CoordinateSystem coordSystem) {
        final float xBinWidth = viewPort.width() / canvas.getWidth();
        mPixelBin.left = viewPort.left - xBinWidth;
        mPixelBin.right = viewPort.left;
        mPixelBin.bottom = Float.POSITIVE_INFINITY;
        mPixelBin.top = Float.NEGATIVE_INFINITY;

        float[] lastpoint = new float[]{Float.NaN, Float.NaN};

        PeekableIterator<float[]> it = new PeekableIterator<float[]>(mSeries.createIterator());

        while (it.hasNext()) {
            float[] point = it.next();
            lastpoint[0] = point[0];
            lastpoint[1] = point[1];
            if (it.peek()[0] > viewPort.left) {
                break;
            }
        }

        coordSystem.mapPoints(lastpoint);

        boolean findOneMore = false;
        while (it.hasNext()) {
            mPixelBin.offset(xBinWidth, 0);
            mPixelBin.bottom = Float.POSITIVE_INFINITY;
            mPixelBin.top = Float.NEGATIVE_INFINITY;

            if (fillPixelBin(mPixelBin, it)) {
                //draw pixel
                coordSystem.mapRect(mPixel, mPixelBin);
                canvas.drawLine(lastpoint[0], lastpoint[1], mPixel.left, mPixel.top, mPointPaint);
                lastpoint[0] = mPixel.left;
                lastpoint[1] = mPixel.top;
                if (findOneMore) {
                    break;
                }
            }
            if (it.peek() != null && it.peek()[0] > viewPort.right) {
                findOneMore = true;
            }
        }
    }

    @Override
    public void setPaintColor(int color) {
        mPointPaint.setColor(color);
    }

    private boolean fillPixelBin(RectF pixelBin, PeekableIterator<float[]> it) {
        boolean retval = false;
        float[] point;
        while (it.hasNext()) {
            point = it.peek();
            if (point[0] > pixelBin.right) {
                break;
            }

            if (point[0] >= pixelBin.left) {
                pixelBin.bottom = Math.min(pixelBin.bottom, point[1]);
                pixelBin.top = Math.max(pixelBin.top, point[1]);
                retval = true;
            }
            it.next();
        }
        return retval;
    }
}