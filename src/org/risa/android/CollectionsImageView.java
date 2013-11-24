package org.risa.android;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CollectionsImageView extends ImageView {
    public CollectionsImageView(Context context) {
        super(context);
    }

    public CollectionsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CollectionsImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }
}