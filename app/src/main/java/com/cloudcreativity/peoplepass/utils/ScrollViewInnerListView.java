package com.cloudcreativity.peoplepass.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * scrollview 中的 ListView
 */
public class ScrollViewInnerListView extends ListView {
    public ScrollViewInnerListView(Context context) {
        super(context);
    }

    public ScrollViewInnerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollViewInnerListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);     }
}
