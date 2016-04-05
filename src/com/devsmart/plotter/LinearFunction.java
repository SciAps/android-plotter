package com.devsmart.plotter;

public class LinearFunction implements AxisFunction {
    private float mA = 1;
    private float mM = 0;

    public LinearFunction() {

    }

    public LinearFunction(float a, float m) {
        mA = a;
        mM = m;
    }

    @Override
    public float value(float x) {
        return mA * x + mM;
    }

    @Override
    public AxisFunction inverse() {
        return new LinearFunction(1 / mA, -mM / mA);
    }

    @Override
    public void interpolate(float[] x, float[] y) {
        float top = y[1] - y[0];
        float bottom = x[1] - x[0];

        mA = top / bottom;
        mM = -mA * x[0];
    }

    @Override
    public AxisFunction copy() {
        return new LinearFunction(mA, mM);
    }
}