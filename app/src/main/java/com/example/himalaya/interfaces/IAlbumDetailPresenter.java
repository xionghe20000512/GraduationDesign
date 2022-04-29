package com.example.himalaya.interfaces;

import com.example.himalaya.base.IBasePresenter;


//public interface IBasePresenter<T> {
//
//    void registerViewCallback(T t);
//
//    void unRegisterViewCallback(T t);
//
//}                                                             ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

public interface IAlbumDetailPresenter extends IBasePresenter<IAlbumDetailViewCallback> {
    /**
     * 下拉刷新更多内容
     */
    void pullRefreshMore();

    /**
     * 上滑加载更多
     */
    void loadMore();

    /**
     * 获取专辑详情
     * @param albumId
     * @param page
     */
    void getAlbumDetail(int albumId,int page);

}
