package com.cloudcreativity.peoplepass.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * scrollview 中的GridView
 */
public class ScrollViewInnerGridView extends GridView {

    public ScrollViewInnerGridView(Context context) {
        super(context);
    }

    public ScrollViewInnerGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollViewInnerGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);     }
}
