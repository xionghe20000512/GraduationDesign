package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryDaoCallback {

    /**
     * 添加历史的结果
     * @param isSuccess
     */
    void onHistoryAdd(boolean isSuccess);

    /**
     * 删除历史的结果
     * @param isSuccess
     */
    void onHistoryDel(boolean isSuccess);

    /**
     * 历史数据加载的结果
     * @param tracks
     */
    void onHistoriesLoaded(List<Track> tracks);

    /**
     * 清除历史记录结果
     * @param isSuccess
     */
    void onHistoriesClean(boolean isSuccess);
}

