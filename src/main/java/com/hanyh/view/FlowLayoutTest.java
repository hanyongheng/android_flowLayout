package com.hanyh.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Created by hanyh on 2017/10/17.
 * 自定义流式布局
 * onMeasure方法中 如果布局设置的是wrap_content那么getMode得到的是at_most但是会反复测量，
 * 先测量at_most得到目标的值，最后在exactly一次 只不过这个得到的值是at_most时算出的值，这也就是系统反复测量的原因
 * 注意如果不super.onMeasure(widthMeasureSpec,heightMeasureSpec)，不调用setMeasuredDimension方法会报错
 * MeasureSpec.getSize(widthMeasureSpec);如果不测量默认就是屏幕的宽和高
 */

public class FlowLayoutTest extends ViewGroup {

    public FlowLayoutTest(Context context) {
        this(context, null);
    }

    public FlowLayoutTest(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayoutTest(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

//        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int width=500;
        int height=200;
        if (modeWidth == MeasureSpec.EXACTLY) {
            setMeasuredDimension(sizeWidth,sizeHeight);
            Log.i("tags", "EXACTLY sizeWidth: " + sizeWidth);
        }
        if (modeWidth == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width,height);
            Log.i("tags", "AT_MOST sizeWidth: " + sizeWidth);
        }


        if (modeHeight==MeasureSpec.EXACTLY){
            setMeasuredDimension(sizeWidth,sizeHeight);
            Log.i("tags", "EXACTLY sizeHeight: " + sizeHeight);
        }
        if (modeHeight==MeasureSpec.AT_MOST){

            Log.i("tags", "AT_MOST sizeHeight: " + sizeHeight);
            setMeasuredDimension(width,height);
        }

    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }


    /***
     * 与自定义viewGroup对应的layoutParams
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
