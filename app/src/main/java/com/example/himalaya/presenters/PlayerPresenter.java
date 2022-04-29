package com.example.himalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.himalaya.DetailActivity;
import com.example.himalaya.PlayerActivity;
import com.example.himalaya.data.XimalayaApi;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.interfaces.IPlayerPresenter;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;


public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private  List<IPlayerCallback> mIPlayerCallbacks=new ArrayList<>();

    private static final String TAG = "PlayerPresenter";
    private XmPlayerManager mPlayerManager;
    private Track mCurrentTrack;
    public static final int DEFAULT_PLAY_LIST = 0;
    private int mCurrentIndex = DEFAULT_PLAY_LIST;
    private final SharedPreferences mPlayModSp;
    private XmPlayListControl.PlayMode mCurrentPlayMode = PLAY_MODEL_LIST;
    private boolean mIsReverse = false;
    private HistoryPresenter mHistoryPresenter;


    public static final int PLAY_MODEL_LIST_INT = 0;
    public static final int PLAY_MODEL_LIST_LOOP_INT = 1;
    public static final int PLAY_MODEL_RANDOM_INT = 2;
    public static final int PLAY_MODEL_SINGLE_LOOP_INT = 3;

    //sp's key and name
    public static final String PLAY_MODE_SP_NAME = "PlayMode";
    public static final String PLAY_MODE_SP_KEY = "CurrentPlayMode";
    private int mCurrentProgressPosition = 0;
    private int mProgressDuration = 0;

    //BaseApplication
    //实例化播放器
    private PlayerPresenter(){
        mPlayerManager= XmPlayerManager.getInstance(BaseApplication.getAppContext());

        //广告相关接口
        mPlayerManager.addAdsStatusListener(this);

        //注册播放器状态相关的接口
        mPlayerManager.addPlayerStatusListener(this);

        //需要记录当前的播放模式
        mPlayModSp = BaseApplication.getAppContext().getSharedPreferences("PlayMode", Context.MODE_PRIVATE);

    }

    private static PlayerPresenter sPlayerPresenter;

    public static PlayerPresenter getPlayerPresenter(){
        if (sPlayerPresenter==null) {
            synchronized (PlayerPresenter.class){
                if(sPlayerPresenter==null){
                    sPlayerPresenter=new PlayerPresenter();
                }
            }
        }
        return sPlayerPresenter;
    }

    private boolean isPlayListSet=false;
    public void setPlayList(List<Track> list, int playIndex){
        //设置播放器列表
        if (mPlayerManager!=null) {
            mPlayerManager.setPlayList(list,playIndex);
            isPlayListSet=true;
            mCurrentTrack = list.get(playIndex);
            mCurrentIndex = playIndex;
        }else{
            LogUtil.d(TAG,"mPlayerManager is null");
        }
    }

    //////////////////////////////////
    ////Player、Detail Activity调用////
    //////////////////////////////////
    @Override
    public void play() {
        if (isPlayListSet) {
            mPlayerManager.play();//XmPlayerManager下自带的play
        }
    }

    @Override
    public void pause() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();//XmPlayerManager下自带的pause
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        //播放上一首
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        //播放下一首
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }

    /**
     * 判断是否有播放列表
     * @return
     */
    public boolean hasPlayList(){
        return isPlayListSet;//是否设置好播放列表
    }

    //PlayerActivity调用
    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        //更换播放模式
        if (mPlayerManager != null) {
            mCurrentPlayMode = mode;
            mPlayerManager.setPlayMode(mode);
            //通知UI更新播放模式
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onPlayModeChange(mode);//PlayerActivity
            }

            //保存到sp里(记录退出界面之前的播放模式)
            SharedPreferences.Editor edit = mPlayModSp.edit();
            edit.putInt(PLAY_MODE_SP_KEY,getIntByPlayMode(mode));
            edit.commit();
        }
    }

    //把播放模式转换成int表示
    private int getIntByPlayMode(XmPlayListControl.PlayMode mode){
        switch(mode){
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
        }
        return PLAY_MODEL_LIST_INT;
    }

    //把int型表示的播放模式转换回来
    private XmPlayListControl.PlayMode getModeByInt(int index){
        switch(index){
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP;
            case PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_RANDOM_INT:
                return PLAY_MODEL_RANDOM;
            case PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST;

        }
        return PLAY_MODEL_LIST;
    }

    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onListLoaded(playList);//获取到播放列表，返回UI层
            }
        }
    }

    @Override
    public void playByIndex(int index) {
        //切换播放器到第index的位置进行播放
        if (mPlayerManager != null) {
            mPlayerManager.play(index);
        }
    }

    @Override
    public void seekTo(int progress) {
        //更新播放器的进度,点哪跳哪
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlay() {
        //返回当前是否正在播放
        return mPlayerManager.isPlaying();
    }

    @Override
    public void reversePlayList() {
        //把播放列表反转
        List<Track> playList = mPlayerManager.getPlayList();
        Collections.reverse(playList);
        mIsReverse = !mIsReverse;

        //第一个参数是播放列表，第二个参数是播放的下标
        //新的下标=总的内容个数-1-当前下标
        mCurrentIndex = playList.size()-1-mCurrentIndex;
        mPlayerManager.setPlayList(playList,mCurrentIndex);
        //更新UI
        mCurrentTrack =(Track) mPlayerManager.getCurrSound();
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onListLoaded(playList);//加载列表
            iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
            iPlayerCallback.updateListOrder(mIsReverse);//activity里实现
        }
    }

    //这个是专辑id，根据专辑id播放第一首
    @Override
    public void playByAlbumId(long id) {
        //1、获取到专辑的列表内容
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                //2、把专辑内容设置给播放器
                List<Track> tracks = trackList.getTracks();
                if (trackList != null&&tracks.size()>0) {
                    mPlayerManager.setPlayList(tracks,DEFAULT_PLAY_LIST);
                    isPlayListSet=true;
                    mCurrentTrack = tracks.get(DEFAULT_PLAY_LIST);
                    mCurrentIndex = DEFAULT_PLAY_LIST;
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG,"errorCode-->"+i);
                LogUtil.d(TAG,"errorMsg-->"+s);
                Toast.makeText(BaseApplication.getAppContext(),"请求数据错误...",Toast.LENGTH_SHORT).show();
            }
        },(int)id,1);

        //3、播放...
    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        if (!mIPlayerCallbacks.contains(iPlayerCallback)) {
            mIPlayerCallbacks.add(iPlayerCallback);
        }
        //更新之前先让UI的pager有数据
        getPlayList();
        //通知当前的节目(记录从主界面跳到播放器时的播放状态和播放进度)
        iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
        iPlayerCallback.onProgressChange(mCurrentProgressPosition,mProgressDuration);
        //更新状态
        handlePlayStatus(iPlayerCallback);
        //从sp里拿
        int modeIndex = mPlayModSp.getInt(PLAY_MODE_SP_KEY,PLAY_MODEL_LIST_INT);
        mCurrentPlayMode = getModeByInt(modeIndex);
        iPlayerCallback.onPlayModeChange(mCurrentPlayMode);
    }

    private void handlePlayStatus(IPlayerCallback iPlayerCallback) {
        int playerStatue = mPlayerManager.getPlayerStatus();
        //根据状态调用接口的方法
        if (PlayerConstants.STATE_STARTED == playerStatue) {
            iPlayerCallback.onPlayStart();
        }else{
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback) {
        mIPlayerCallbacks.remove(iPlayerCallback);
    }

    //===================================
    //         广告相关的回调方法
    //===================================

    //开始获取广告物料
    @Override
    public void onStartGetAdsInfo() {
        LogUtil.d(TAG,"onStartGetAdsInfo");
    }

    //获取广告物料成功
    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG,"onGetAdsInfo");
    }

    //广告开始缓冲
    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG,"onAdsStartBuffering");
    }

    //广告结束缓冲
    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG,"onAdsStopBuffering");
    }

    //开始播放广告
    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG,"onStartPlayAds");
    }

    //广告播放完毕
    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG,"onCompletePlayAds");
    }

    //播放广告错误
    //                    ↓错误类型  ↓额外信息
    @Override
    public void onError(int what, int extra) {
        LogUtil.d(TAG,"onError what -> "+what+" extra -> "+extra);
    }

    //===================================
    //         播放器相关的回调
    //===================================

    //开始播放
    @Override
    public void onPlayStart() {
        LogUtil.d(TAG,"onPlayStart");
        for(IPlayerCallback iPlayerCallback:mIPlayerCallbacks){
            iPlayerCallback.onPlayStart();//activity里实现
        }
    }

    //暂停播放
    @Override
    public void onPlayPause() {
        LogUtil.d(TAG,"onPlayPause");
        for(IPlayerCallback iPlayerCallback:mIPlayerCallbacks){
            iPlayerCallback.onPlayPause();//activity里实现
        }
    }

    //停止播放
    @Override
    public void onPlayStop() {
        LogUtil.d(TAG,"onPlayStop");
        for(IPlayerCallback iPlayerCallback:mIPlayerCallbacks){
            iPlayerCallback.onPlayStop();//activity里实现
        }
    }

    //播放完成
    @Override
    public void onSoundPlayComplete() {
        LogUtil.d(TAG,"onSoundPlayComplete");
    }

    //播放器准备完毕（准备完毕后可以播放）
    @Override
    public void onSoundPrepared() {
        LogUtil.d(TAG,"onSoundPrepared");
        mPlayerManager.setPlayMode(mCurrentPlayMode);
        if (mPlayerManager.getPlayerStatus()== PlayerConstants.STATE_PREPARED) {
            //播放器准备完毕，可以播放了
            mPlayerManager.play();
        }
    }

    //切歌
    //lastModel:上一首（可能为null）
    //curModel:下一首
    //通过model中的kind字段来判断是track，radio和schedule；
    //上一首播放时间通过lastPlayedMills字段来获取
    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        //获取当前歌曲的标题
        LogUtil.d(TAG,"onSoundSwitch");
        if (lastModel != null) {
            LogUtil.d(TAG,"lastModel "+lastModel.getKind());
        }
        if (curModel != null) {
            LogUtil.d(TAG,"onSoundSwitch "+curModel.getKind());
        }
        //curModel代表的是当前播放的内容
        //通过getKind()方法来获取他是什么类型的
        //track表示是track类型
        //第一种写法：不推荐
//        if ("track".equals(curModel.getKind())) {
//            Track currentTrack=(Track)curModel;
//            LogUtil.d(TAG,"title --> "+currentTrack.getTrackTitle());
//        }
        //第二种写法
        mCurrentIndex = mPlayerManager.getCurrentIndex();
        if (curModel instanceof Track) {
            Track currentTrack=(Track) curModel;
            mCurrentTrack = currentTrack;
            //保存播放记录
            mHistoryPresenter = HistoryPresenter.getHistoryPresenter();
            mHistoryPresenter.addHistory(currentTrack);
            //LogUtil.d(TAG,"title--> "+currentTrack.getTrackTitle());
            //更新UI
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
            }
        }
    }

    //IXmPlayerStatusListener.class

    //开始缓冲
    @Override
    public void onBufferingStart() {
        LogUtil.d(TAG,"onBufferingStart");
    }

    //结束缓冲
    @Override
    public void onBufferingStop() {
        LogUtil.d(TAG,"onBufferingStop");
    }

    //缓冲进度回调
    @Override
    public void onBufferProgress(int progress) {
        LogUtil.d(TAG,"onBufferProgress --> "+progress);
    }

    //播放进度回调
    //                              ↓当前进度     ↓持续时间
    @Override
    public void onPlayProgress(int currPos, int duration) {
        //记录从主界面跳到播放器时的播放进度
        this.mCurrentProgressPosition = currPos;
        this.mProgressDuration = duration;
        //单位是毫秒
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onProgressChange(currPos,duration);  //PlayerActivity
        }
        LogUtil.d(TAG,"onPlayProgress"+ currPos + " duration --> " + duration);
    }

    //播放器错误
    @Override
    public boolean onError(XmPlayerException e) {
        LogUtil.d(TAG,"onError e --> "+e);
        return false;
    }
}
