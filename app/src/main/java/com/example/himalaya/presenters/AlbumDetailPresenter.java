package com.example.himalaya.presenters;

import androidx.annotation.Nullable;

import com.example.himalaya.data.XimalayaApi;
import com.example.himalaya.interfaces.IAlbumDetailPresenter;
import com.example.himalaya.interfaces.IAlbumDetailViewCallback;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private static final String TAG = "AlbumDetailPresenter";
    private List<IAlbumDetailViewCallback> mCallbacks=new ArrayList<>();
    private List<Track> mTracks = new ArrayList<>();

    private Album mTargetAlbum=null;
    //当前的专辑id
    private int mCurrentAlbumId = -1;
    //当前页
    private int mCurrentPageIndex = 0;

    /**
     * 单例模式
     */
    private AlbumDetailPresenter(){

    }

    private static AlbumDetailPresenter sInstance = null;

    public static AlbumDetailPresenter getInstance(){
        if (sInstance==null) {
            synchronized(AlbumDetailPresenter.class){
                if (sInstance==null) {
                    sInstance=new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pullRefreshMore() {

    }

    @Override
    public void loadMore() {
        //去加载更多内容
        mCurrentPageIndex++;
        //传入true表示结果会追加到列表的后方
        doLoaded(true);
    }

    //做加载
    private void doLoaded(final boolean isLoaderMore){
        //获取数据
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    LogUtil.d(TAG,"track size -- > "+tracks.size());
                    if (isLoaderMore) {
                        //如果是加载更多（isLoaderMore）添加专辑,那么就加到列表后面
                        mTracks.addAll(tracks);
                        int size = tracks.size();
                        handlerLoaderMoreResult(size);
                    }else{
                        //不是加载更多
                        //加到列表前面
                        mTracks.addAll(0,tracks);
                    }
                    handlerAlbumDetailResult(mTracks);
                }
            }

            //失败时显示网络错误
            @Override
            public void onError(int errorCode, String errorMsg) {
                if (isLoaderMore) {
                    mCurrentPageIndex--;
                }
                LogUtil.d(TAG,"errorCode --> "+errorCode);
                LogUtil.d(TAG,"errorMsg --> "+errorMsg);
                handlerError(errorCode,errorMsg);
            }
        },mCurrentAlbumId,mCurrentPageIndex);
    }

    /**
     * 处理加载更多结果
     * @param size
     */
    private void handlerLoaderMoreResult(int size) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onLoaderMoreFinished(size);
        }
    }

    //拿到专辑详情
    @Override
    public void getAlbumDetail(int albumId, int page) {
        mTracks.clear();//第一次进来
        this.mCurrentAlbumId = albumId;
        this.mCurrentPageIndex = page;
        //根据页码和专辑ID获取到列表
        doLoaded(false);
    }

    /**
     * 如果发生错误，通知UI
     * @param errorCode
     * @param errorMsg
     */
    private void handlerError(int errorCode, String errorMsg) {
        for(IAlbumDetailViewCallback callback:mCallbacks){
            callback.onNetworkError(errorCode,errorMsg);
        }
    }

    //处理结果
    private void handlerAlbumDetailResult(List<Track> tracks) {
        //把专辑详情加到UI里
        for (IAlbumDetailViewCallback mCallback : mCallbacks) {
            mCallback.onDetailListLoaded(tracks);
        }
    }

    @Override
    public void registerViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        if (!mCallbacks.contains(detailViewCallback)) {
            mCallbacks.add(detailViewCallback);
            if (mTargetAlbum!=null) {
                //如果当前Album的数据不为空就更新界面
                detailViewCallback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unRegisterViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        mCallbacks.remove(detailViewCallback);

    }

    /**
     * 设置跳转的目标专辑
     */
    public void setTargetAlbum(Album targetAlbum){
        this.mTargetAlbum=targetAlbum;
    }

}
