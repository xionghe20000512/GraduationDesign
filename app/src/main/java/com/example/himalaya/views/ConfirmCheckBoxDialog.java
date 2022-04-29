package com.example.himalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.himalaya.R;

public class ConfirmCheckBoxDialog extends Dialog {

    private View mCancel;
    private View mDel;
    private OnDialogActionClickListener mClickListener = null;
    private CheckBox mCheckBox;

    public ConfirmCheckBoxDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmCheckBoxDialog(@NonNull Context context, int themeResId) {

        //true的意思是点击空白处能取消
        this(context, true,null);
    }

    protected ConfirmCheckBoxDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    //设置内容
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_box_confirm);
        //找到控件
        initView();
        //设置点击事件
        initListener();
    }

    private void initListener() {
        mDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    //记录CheckBox的值
                    boolean checked = mCheckBox.isChecked();
                    //删除历史记录
                    mClickListener.onDelHst(checked);
                    dismiss();//让dialog消失
                }
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    //取消
                    mClickListener.onCancelHst();
                    dismiss();//让dialog消失
                }
            }
        });
    }

    private void initView() {
        //找到“否”按钮
        mCancel = this.findViewById(R.id.dialog_check_box_cancel);
        //找到“是”按钮
        mDel = this.findViewById(R.id.dialog_check_box_confirm);
        //找到checkbox
        mCheckBox = this.findViewById(R.id.dialog_check_box);
    }

    public void setOnDialogActionClickListener(OnDialogActionClickListener listener){
        this.mClickListener = listener;
    }

    //本类调用，fragment实现
    public interface OnDialogActionClickListener{

        void onCancelHst();

        void onDelHst(boolean isCheck);
    }
}
