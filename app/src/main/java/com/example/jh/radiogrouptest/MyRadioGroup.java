package com.example.jh.radiogrouptest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：jinhui on 2017/4/28
 * 邮箱：1004260403@qq.com
 */

public class MyRadioGroup extends LinearLayout {

    private static final String TAG = "MyRadioGroup";

    private CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    // 加上这样的监听接口可以实现单选radiobutton
    private PassThroughHierarchyChangeListener mPassThroughListener;

    private boolean mProtectFromCheckedChange = false;
    private int mCheckedId = -1;

    public MyRadioGroup(Context context) {
        super(context);
        init();
    }

    public MyRadioGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mChildOnCheckedChangeListener = new CheckedStateTracker();
        mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(mPassThroughListener);
    }

    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        // the user listener is delegated to our pass-through listener
        mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    // 检查状态改变的类
    private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // prevents from infinite recursion
            if (mProtectFromCheckedChange) {
                return;
            }
            mProtectFromCheckedChange = true;
            if (mCheckedId != -1) {
                setCheckedStateForView(mCheckedId, false);
            }
            mProtectFromCheckedChange = false;

            int id = buttonView.getId();
            setCheckedId(id);
        }
    }

    private void setCheckedId(int id) {
        Log.e(TAG, "setCheckedId方法被执行");
        mCheckedId = id;
        if(mCheckedId == R.id.rdoBtn1){
            Toast.makeText(getContext(), "安全模式", Toast.LENGTH_SHORT).show();
        }
        if(mCheckedId == R.id.rdoBtn2){
            Toast.makeText(getContext(), "省电模式", Toast.LENGTH_SHORT).show();
        }
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mCheckedId);
        }
    }

    // set方法
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }
    public interface OnCheckedChangeListener {
        public void onCheckedChanged(MyRadioGroup group, int checkedId);
    }

    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);

        if (checkedView != null && checkedView instanceof RadioButton) {
            ((RadioButton) checkedView).setChecked(checked);
        }
    }


    // ——————————
    private class PassThroughHierarchyChangeListener implements OnHierarchyChangeListener {
        private OnHierarchyChangeListener mOnHierarchyChangeListener;

        /**
         * {@inheritDoc}
         */
        @SuppressLint("NewApi")
        public void onChildViewAdded(View parent, View child) {
            if (parent == MyRadioGroup.this) {
                List<RadioButton> btns = getAllRadioButton(child);
                if(btns != null && btns.size() > 0){
                    for(RadioButton btn : btns){
                        int id = btn.getId();
                        // generates an id if it's missing
                        if (id == View.NO_ID && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            id = View.generateViewId();
                            btn.setId(id);
                        }
                        btn.setOnCheckedChangeListener(
                                mChildOnCheckedChangeListener);
                    }
                }
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void onChildViewRemoved(View parent, View child) {
            if (parent == MyRadioGroup.this) {
                List<RadioButton> btns = getAllRadioButton(child);
                if(btns != null && btns.size() > 0){
                    for(RadioButton btn : btns){
                        btn.setOnCheckedChangeListener(null);
                    }
                }
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }

    // 得到所有radioButton
    private List<RadioButton> getAllRadioButton(View child){
        List<RadioButton> btns = new ArrayList<RadioButton>();
        if (child instanceof RadioButton) {
            btns.add((RadioButton) child);
        }else if(child instanceof ViewGroup){
            int counts = ((ViewGroup) child).getChildCount();
            for(int i = 0; i < counts; i++){
                btns.addAll(getAllRadioButton(((ViewGroup) child).getChildAt(i)));
            }
        }
        return btns;
    }
}
