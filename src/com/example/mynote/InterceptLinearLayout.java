package com.example.mynote;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class InterceptLinearLayout extends LinearLayout {
	private boolean intercept = false;

	public InterceptLinearLayout(Context context) {
		super(context);
	}

	public InterceptLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InterceptLinearLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setIntercept(boolean b) {
		intercept = b;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (intercept)
			return true;
		return super.onInterceptTouchEvent(ev);
	}
}


