package com.hanyh.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanyh on 2017/10/17.
 * 自定义流式布局
 * onMeasure方法中 如果布局的宽设置的是wrap_content那么getMode得到的是at_most但是会反复测量，
 * 先测量at_most得到目标的值，最后在exactly一次 只不过这个得到的值是at_most时算出的值，这也就是系统反复测量的原因
 * 高度的测量正好相反 如果布局设置的是wrap_content那么getMode得到的是at_most，如果布局设置的是match_parent
 * 那么getMode得到的是exactly 但是会反复测量 先测量at_most得到目标的值，最后在exactly一次 只不过这个得到的值是at_most时算出的值
 * 补充：高的测量是根据宽的测量模式而定：
 * 比如：宽是match_parent 高是 match_parent 那么测量过程是-》
 * 宽：EXACTLY——》EXACTLY——》EXACTLY——》EXACTLY
 * 高：EXACTLY——》EXACTLY——》EXACTLY——》EXACTLY
 *
 * 宽是match_parent 高是 wrap_content 那么测量过程是——》
 * 宽：EXACTLY——》EXACTLY——》EXACTLY——》EXACTLY
 * 高：EXACTLY——》AT_MOST——》EXACTLY——》AT_MOST
 *
 * 宽是wrap_content 高是 match_parent 那么测量过程是——》
 * 宽：AT_MOST——》EXACTLY——》AT_MOST——》EXACTLY
 * 高：AT_MOST——》EXACTLY——》AT_MOST——》EXACTLY
 *
 * 宽是wrap_content 高是 wrap_content 那么测量过程是——》
 * 宽：AT_MOST——》EXACTLY——》AT_MOST——》EXACTLY
 * 高：AT_MOST——》AT_MOST——》AT_MOST——》AT_MOST
 *
 * 注意如果不super.onMeasure(widthMeasureSpec,heightMeasureSpec)，不调用setMeasuredDimension方法会报错
 * MeasureSpec.getSize(widthMeasureSpec);如果不测量默认就是屏幕的宽和高
 */

public class FlowLayout extends ViewGroup {

    private List<List<View>> allViews = new ArrayList<>();
    private List<Integer> mLineHeights = new ArrayList<>();

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        //wrap_content 的宽和高自己算
        int width = 0;
        int height = 0;

        int lineWidth = 0;
        int lineHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int childHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
            //换行 这里没有考虑padding的问题 要想解决padding的问题需要在sizeWidth后面减去padding
            if (lineWidth + childWidth > sizeWidth) {

                width = Math.max(width, lineWidth);
                height += lineHeight;
                lineWidth = childWidth;
                lineHeight = childHeight;
            } else {

                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            //别漏了最后一个元素的宽高计算
            if (i == childCount - 1) {
                Log.i("test", " childCount: " + childCount);
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }

        Log.i("test", " sizeWidth: " + sizeWidth);
        Log.i("test", " sizeHeight: " + sizeHeight);
        // 这里没有考虑padding的问题 为了方便理解所以没有加 要想解决padding的问题需要在width后面加上padding 下面布局同理
        setMeasuredDimension(
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width
                , modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height);
    }


    @Override
    protected void onLayout(boolean change, int l, int t, int r, int b) {

        allViews.clear();
        int width = getWidth();
        int childCount = getChildCount();
        int lineWidth = 0;
        int lineHeight = 0;
        List<View> lineViews = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {

            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();

            //如果需要换行
            if (lineWidth + childWidth + params.leftMargin + params.rightMargin > width) {
                allViews.add(lineViews);
                mLineHeights.add(lineHeight);
                lineWidth = 0;
                lineHeight = childHeight + params.topMargin + params.bottomMargin;
                lineViews = new ArrayList<>();
            }

            lineWidth += childWidth + params.leftMargin + params.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + params.topMargin + params.bottomMargin);
            lineViews.add(child);
        }//for end
        allViews.add(lineViews);
        mLineHeights.add(lineHeight);

        //布局子view 遍历每一行view
        int lineNum = allViews.size();
        int left = 0;
        int top = 0;
        for (int i = 0; i < lineNum; i++) {
            lineViews = allViews.get(i);
            lineHeight = mLineHeights.get(i);

            for (int j = 0; j < lineViews.size(); j++) {

                View child = lineViews.get(j);
                MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
                int lc = left + params.leftMargin;
                int tc = top + params.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                child.layout(lc, tc, rc, bc);
                left += child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            }
            left = 0;
            top += lineHeight;

        }
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
