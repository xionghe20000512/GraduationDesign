package com.example.himalaya.views;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;

/**
 * 加载所有状态的界面（加载中，网络错误，加载成功，数据为空）
 */
public abstract class UILoader extends FrameLayout {

    private View mLoadingView;
    private View mSuccessView;
    private View mNetworkErrorView;
    private View mEmptyView;
    private OnRetryClickListener mOnRetryClickListener=null;

    //枚举类
    public enum UIStatus{
        LOADING,SUCCESS,NETWORK_ERROR,EMPTY,NONE
    }

    public UIStatus mCurrentStatus=UIStatus.NONE;

    public UILoader(@NonNull Context context) {
        this(context,null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //
        init();
    }

    public void updateStatus(UIStatus status){
        mCurrentStatus=status;
        //更新UI
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                switchUIByCurrentStatus();
            }
        });
    }

    /**
     * 初始化UI
     */
    private void init() {
        switchUIByCurrentStatus();
    }

    //设置状态
    private void switchUIByCurrentStatus() {
        //加载中
        if (mLoadingView == null) {
            mLoadingView=getLoadingVIew();
            addView(mLoadingView);
        }
        //根据状态设置是否可见
        mLoadingView.setVisibility(mCurrentStatus==UIStatus.LOADING?VISIBLE:GONE);

        //加载成功
        if (mSuccessView== null) {
            mSuccessView=getSuccessView(this);
            addView(mSuccessView);
        }
        //根据状态设置是否可见
        mSuccessView.setVisibility(mCurrentStatus==UIStatus.SUCCESS?VISIBLE:GONE);

        //网络错误页面
        if (mNetworkErrorView== null) {
            mNetworkErrorView=getNetWorkErrorView();
            addView(mNetworkErrorView);
        }
        //根据状态设置是否可见
        mNetworkErrorView.setVisibility(mCurrentStatus==UIStatus.NETWORK_ERROR?VISIBLE:GONE);

        //数据为空界面
        if (mEmptyView== null) {
            mEmptyView=getEmptyView();
            addView(mEmptyView);
        }
        //根据状态设置是否可见
        mEmptyView.setVisibility(mCurrentStatus==UIStatus.EMPTY?VISIBLE:GONE);
    }

    protected View getEmptyView(){
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view,this ,false);
    }

    protected View getNetWorkErrorView(){
        View networkErrorVIew=LayoutInflater.from(getContext()).inflate(R.layout.fragment_error_view,this ,false);
        //点击重试（设置监听事件）
        networkErrorVIew.findViewById(R.id.network_error_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新获取数据
                if (mOnRetryClickListener != null) {
                    mOnRetryClickListener.onRetryClick();
                }
            }
        });

        return networkErrorVIew;
    }

    //不知道加载成功具体显示什么界面，所以设置成抽象类
    //所以不定的内容就交给RecommendFragment去实现
    protected abstract View getSuccessView(ViewGroup container);

    private View getLoadingVIew() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading_view,this ,false);
    }

    public void setOnRetryClickListener(OnRetryClickListener listener){
        this.mOnRetryClickListener=listener;
    }

    public interface OnRetryClickListener{
        void onRetryClick();
    }
}
