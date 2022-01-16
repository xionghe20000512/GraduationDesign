package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

//告知推荐UI
public interface IRecommendViewCallback {
    /**
     * 获取推荐内容的结果
     * @param result
     */
    void onRecommendListLoaded(List<Album> result);

    /**
     * 加载更多
     * @param result
     */
    void onLoaderMore(List<Album> result);

    /**
     * 上滑加载更多
     * @param result
     */
    void onRefreshMore(List<Album> result);


}
