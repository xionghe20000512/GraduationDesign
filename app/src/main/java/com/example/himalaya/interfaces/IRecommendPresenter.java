package com.example.himalaya.interfaces;

import com.example.himalaya.base.IBasePresenter;

//主动发起的动作
public interface IRecommendPresenter extends IBasePresenter<IRecommendViewCallback> {

    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 下拉刷新更多内容
     */
    void pullRefreshMore();

    /**
     * 上滑加载更多
     */
    void loadMore();

}
