package com.example.ja.diudiu.base;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by JA on 2015/7/14.
 */
public abstract class BasePopupWindow extends PopupWindow {
    protected View mContentView;
    protected OnSubmitClickListener mOnSubmitClickListener;

    public BasePopupWindow() {
        super();
    }

    public BasePopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public BasePopupWindow(Context context) {
        super(context);
    }

    public BasePopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BasePopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BasePopupWindow(View contentView) {
        super(contentView);
    }

    public BasePopupWindow(int width, int height) {
        super(width, height);
    }

    public BasePopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }

    public BasePopupWindow(View contentView, int width, int height) {
        super(contentView, width, height, true);
        mContentView = contentView;
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        setOutsideTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        initViews();
        initEvents();
        init();
    }

    public abstract void initEvents();

    public abstract void initViews();
    public abstract void init();
    public  View findViewById(int id) {
        return mContentView.findViewById(id);
    }

    /**
     * ÃÌº”»∑»œº‡Ã˝
     */
    public void setOnSubmitClickListener(OnSubmitClickListener l) {
        mOnSubmitClickListener = l;
    }

    public interface OnSubmitClickListener {
        void onClick();
    }
}
