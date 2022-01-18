package com.example.himalaya.presenters;

import androidx.annotation.Nullable;

import com.example.himalaya.interfaces.IRecommendPresenter;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//实现的是IRecommendPresenter接口里面的方法
public class RecommendPresenter implements IRecommendPresenter {

    private static final String TAG="RecommendPresenter";

    private List<IRecommendViewCallback> mCallbacks=new ArrayList<>();

    private RecommendPresenter(){

    }

    private static RecommendPresenter sInstance = null;

    /**
     * 获取单例对象
     *
     * 懒汉式，线程安全
     * 是否 Lazy 初始化：是
     * 是否多线程安全：是
     * 实现难度：易
     * 描述：这种方式具备很好的 lazy loading，能够在多线程中很好的工作，但是，效率很低，99% 情况下不需要同步。
     * 优点：第一次调用才初始化，避免内存浪费。
     * 缺点：必须加锁 synchronized 才能保证单例，但加锁会影响效率。
     * getInstance() 的性能对应用程序不是很关键（该方法使用不太频繁）。
     * @return
     */
    public static RecommendPresenter getInstance(){
        if(sInstance==null){
            synchronized (RecommendPresenter.class){
                if(sInstance==null){
                    sInstance=new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    //获取推荐内容，（猜你喜欢）
    /*
    3.10.6 SDK接入文档
     */
    @Override
    public void getRecommendList() {
        //获取推荐内容
        //封装参数
        updateLoading();
        Map<String, String> map = new HashMap<>();
        //表示数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT+"");
        //喜马拉雅获取推荐内容
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                LogUtil.d(TAG,"thread name --> "+Thread.currentThread().getName());
                //数据获取成功
                if(gussLikeAlbumList!=null){
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    //数据回来以后就要更新UI界面的内容
                    //upRecommendUI(albumlist);
                    handlerRecommendResult(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                //数据获取出错（网络错误）
                LogUtil.d(TAG, "error --> "+i);
                LogUtil.d(TAG, "errorMsg --> "+s);
                handlerError();
            }
        });
    }

    private void handlerError() {
        if (mCallbacks!=null) {
            for (IRecommendViewCallback callback : mCallbacks) {
                callback.onNetworkError();
            }
        }
    }

    private void handlerRecommendResult(List<Album> albumList) {
        if (albumList!=null) {
            if (albumList.size()==0) {
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onEmpty();
                }
            }else{
                //通知UI更新
                //遍历mCallbacks集合里面每一个回调
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onRecommendListLoaded(albumList);//更新UI
                }
            }
        }
    }

    private void updateLoading(){
        for (IRecommendViewCallback callback : mCallbacks) {
            callback.onLoading();
        }
    }

    @Override
    public void pullRefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        //contains是判断mCallbacks集合里时候包含界面对象
        //不包含就在mCallbacks里面添加回调
        if (mCallbacks!=null&&!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callback) {
        if(mCallbacks!=null){
            mCallbacks.remove(mCallbacks);
        }
    }
}
