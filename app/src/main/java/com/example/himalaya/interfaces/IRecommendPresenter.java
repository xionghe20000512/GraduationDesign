package com.example.himalaya.interfaces;

//主动发起的动作
public interface IRecommendPresenter {

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

    /**
     * 这个方法用于注册UI的回调
     * @param callback
     */
    void registerViewCallback(IRecommendViewCallback callback);

    /**
     * 取消UI的回调注册
     * @param callback
     */
    void unRegisterViewCallback(IRecommendViewCallback callback);

}
