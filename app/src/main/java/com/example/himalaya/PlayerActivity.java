package com.example.himalaya;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.himalaya.adapters.PlayerTrackPagerAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.PlayListPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {

    private static final String TAG = "PlayerActivity";
    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm:ss");
    private TextView mTotalDuration;
    private TextView mCurrentPosition;
    private SeekBar mDurationBar;
    private int mCurrentProgress = 0;
    private boolean mIsUserTouchProgressBar = false;
    private ImageView mPlayNextBtn;
    private ImageView mPlayPreBtn;
    private TextView mTrackTitleTv;
    private String mTrackTitleText;
    private ViewPager mTrackPageView;
    private PlayerTrackPagerAdapter mTrackPagerAdapter;
    private boolean mIsUserSlidePager = false;
    private ImageView mPlayModeSwitchBtn;
    private ImageView mPlayListBtn;
    private PlayListPopWindow mPlayListPopWindow;
    private ValueAnimator mEnterBgAnimator;
    private ValueAnimator mOutBgAnimator;
    public final int BG_ANIMATION_DURATION = 300;

    private XmPlayListControl.PlayMode mCurrentMode = PLAY_MODEL_LIST;

    private static Map<XmPlayListControl.PlayMode,XmPlayListControl.PlayMode> sPlayModeRule = new HashMap<>();

    //处理播放模式的切换
    //1，默认：PLAY_MODEL_LIST
    //2,列表循环：PLAY_MODEL_LIST_LOOP
    //3,随机播放：PLAY_MODEL_RANDOM
    //4,单曲循环：PLAY_MODEL_SINGLE_LOOP
    static{
        sPlayModeRule.put(PLAY_MODEL_LIST,PLAY_MODEL_LIST_LOOP);
        sPlayModeRule.put(PLAY_MODEL_LIST_LOOP,PLAY_MODEL_RANDOM);
        sPlayModeRule.put(PLAY_MODEL_RANDOM,PLAY_MODEL_SINGLE_LOOP);
        sPlayModeRule.put(PLAY_MODEL_SINGLE_LOOP,PLAY_MODEL_LIST);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //找到控件
        initView();

        mPlayerPresenter=PlayerPresenter.getPlayerPresenter();

        mPlayerPresenter.registerViewCallback(this);

        //给控件设置相关事件
        initEvent();
        //初始化动画
        initBgAnimation();

    }

    private void initBgAnimation() {
        //进入的渐变效果
        mEnterBgAnimator = ValueAnimator.ofFloat(1.0f,0.8f);
        mEnterBgAnimator.setDuration(BG_ANIMATION_DURATION);//单位毫秒
        //1.0到0.8会有一个梯度，每次都会回调如下方法
        mEnterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)  animation.getAnimatedValue();
                //处理背景，有点透明度
                updateBgAlpha(value);
            }
        });
        //退出的渐变效果
        mOutBgAnimator = ValueAnimator.ofFloat(0.8f,1.0f);
        mOutBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mOutBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)  animation.getAnimatedValue();
                //处理背景，有点透明度
                updateBgAlpha(value);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //释放资源
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter=null;
        }
    }

    /**
     * 给控件设置相关的事件
     */
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果现在的状态是正在播放，那么就暂停
                if (mPlayerPresenter.isPlay()) {
                    mPlayerPresenter.pause();
                }else{
                    //如果现在的状态是非播放的，那么就让播放器播放(点进去直接播放)
                    mPlayerPresenter.play();
                }
            }
        });

        mDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                //记录是否是用户的手拖拽的
                if (isFromUser) {
                    mCurrentProgress = progress;
                }
            }

            //触摸下去调用
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar = true;
            }

            //手离开时调用
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar = false;
                mPlayerPresenter.seekTo(mCurrentProgress);
            }
        });

        mPlayPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放上一首
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playPre();
                }
            }
        });

        mPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放下一首
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playNext();
                }
            }
        });

        //实现标题，图片，音乐联动切换
        mTrackPageView.addOnPageChangeListener(this);

        mTrackPageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action){
                    case MotionEvent.ACTION_DOWN://手指的初次触摸
                        mIsUserSlidePager = true;
                        break;
                }
                return false;
            }
        });

        mPlayModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPlayMode();
            }
        });

        //播放列表设置PopWindow
        mPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //展示播放列表
                mPlayListPopWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
                //点击后直接开始动画
                mEnterBgAnimator.start();
            }
        });
        mPlayListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            //点击外部消失
            @Override
            public void onDismiss() {
                //点击后直接开始动画
                mOutBgAnimator.start();
            }
        });

        //播放列表里点击了item播放
        mPlayListPopWindow.setPlayListItemClickListener(new PlayListPopWindow.PlayListItemClickListener() {
            //这是PlayListPopWindow里的方法
            @Override
            public void onItemClick(int position) {
                //说明播放列表里的Item被点击了
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(position);//点击播放
                }
            }
        });

        mPlayListPopWindow.setPlayListActionListener(new PlayListPopWindow.PlayListActionListener() {
            @Override
            public void onPlayModeClick() {
                //切换播放模式
                switchPlayMode();
            }

            @Override
            public void onOrderClick() {
                //点击了切换顺序和逆序
                //Toast.makeText(PlayerActivity.this,"切换列表顺序", Toast.LENGTH_SHORT).show();
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.reversePlayList();//presenter实现相关逻辑
                }
            }
        });
    }

    private void switchPlayMode() {
        //根据当前的model获取到下一个model
        XmPlayListControl.PlayMode playMode = sPlayModeRule.get(mCurrentMode);
        //修改播放模式
        if (mPlayerPresenter != null) {
            mPlayerPresenter.switchPlayMode(playMode);//PlayerPresenter的switchPlayMode
        }
    }

    public void updateBgAlpha(float alpha){
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }

    /**
     * 根据当前的状态更新播放模式图标
     * 1，默认：PLAY_MODEL_LIST
     * 2,列表循环：PLAY_MODEL_LIST_LOOP
     * 3,随机播放：PLAY_MODEL_RANDOM
     * 4,单曲循环：PLAY_MODEL_SINGLE_LOOP
     */
    private void updatePlayModeBtnImg() {
        int resId = R.drawable.selector_play_model_list_order;
        switch(mCurrentMode){
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_play_model_list_order;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_play_model_random;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_play_model_list_order_looper;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_play_model_single_loop;
                break;
        }
        mPlayModeSwitchBtn.setImageResource(resId);
    }

    /**
     * 找到各个控件
     */
    private void initView() {
        //找到播放按钮
        mControlBtn = this.findViewById(R.id.play_or_pause_btn);
        //找到进度（持续时间）
        mTotalDuration = this.findViewById(R.id.track_duration);
        //找到进度（当前时间）
        mCurrentPosition = this.findViewById(R.id.current_position);
        //找到进度条控件
        mDurationBar = this.findViewById(R.id.track_seek_bar);
        //找到下一首控件
        mPlayNextBtn = this.findViewById(R.id.play_next);
        //找到上一首控件
        mPlayPreBtn = this.findViewById(R.id.play_pre);
        //找到标题控件
        mTrackTitleTv = this.findViewById(R.id.track_title);
        if (!TextUtils.isEmpty(mTrackTitleText)) {
            mTrackTitleTv.setText(mTrackTitleText);//获取到标题
        }
        //找到ViewPager控件
        mTrackPageView = this.findViewById(R.id.track_pager_view);
        //创建适配器
        mTrackPagerAdapter = new PlayerTrackPagerAdapter();
        //设置适配器
        mTrackPageView.setAdapter(mTrackPagerAdapter);
        //找到切换播放模式的按钮
        mPlayModeSwitchBtn = this.findViewById(R.id.player_mode_switch_btn);
        //播放列表
        mPlayListBtn = this.findViewById(R.id.player_list);
        mPlayListPopWindow = new PlayListPopWindow();
    }

    //在presenter里调用
    @Override
    public void onPlayStart() {
        //开始播放，修改UI成暂停的按钮
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_pause);
        }
    }

    @Override
    public void onPlayPause() {
        //暂停播放，修改UI成播放的按钮
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayStop() {
        //停止播放，修改UI成播放的按钮
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void nextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {
//        LogUtil.d(TAG,"list --> "+ list);
        //把数据设置到适配器里(这是把数据给了details页面)
        if (mTrackPagerAdapter != null) {
            mTrackPagerAdapter.setData(list);
        }
        //数据回来以后，也要给播放列表(这是把数据给了播放列表：popWindow)
        if (mPlayListPopWindow != null) {
            mPlayListPopWindow.setListData(list);
        }
    }

    //保证退出播放界面之后再点进来播放模式不改变
    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
        //更新播放模式，并且修改UI
        mCurrentMode=playMode;

        //更新Pop的播放模式
        mPlayListPopWindow.updatePlayMode(mCurrentMode);

        updatePlayModeBtnImg();
    }

    @Override
    public void onProgressChange(int currentDuration, int total) {
        mDurationBar.setMax(total);
        //更新播放进度，更新进度条
        String totalDuration;
        String currentPosition;
        if (total>1000*60*60) {
            totalDuration = mHourFormat.format(total);
            currentPosition = mHourFormat.format(currentDuration);
        }else{
            totalDuration = mMinFormat.format(total);
            currentPosition = mMinFormat.format(currentDuration);
        }
        //更新总时间
        if (mTotalDuration != null) {
            mTotalDuration.setText(totalDuration);
        }
        //更新当前时间
        if (mCurrentPosition != null) {
            mCurrentPosition.setText(currentPosition);
        }
        //更新进度
        //计算当前进度
        if (!mIsUserTouchProgressBar) {
            mDurationBar.setProgress(currentDuration);//progress就是当前的进度
        }

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdLoaded() {

    }

    @Override
    public void onTrackUpdate(Track track,int playIndex) {
        if (track == null) {
            LogUtil.d(TAG,"onTrackUpdate-->track null");
            return;
        }
        this.mTrackTitleText=track.getTrackTitle();
        if (mTrackTitleTv != null) {
            //设置当前节目的标题
            mTrackTitleTv.setText(mTrackTitleText);
        }
        //当节目改变的时候，我们就获取到当前播放器中的位置
        //当前的节目改变以后，要修改页面的图片
        if (mTrackPageView != null) {
            mTrackPageView.setCurrentItem(playIndex,true);//切换的时候有动画（true）
        }

        //修改播放列表里的播放位置
        if (mPlayListPopWindow != null) {
            mPlayListPopWindow.setCurrentPlayPosition(playIndex);
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {
        //反转了就更新
        mPlayListPopWindow.updateOrderIcon(isReverse);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        LogUtil.d(TAG,"position -- > "+ position);
        //当页面选中的时候，就去切换播放的内容
        if (mPlayerPresenter != null&&mIsUserSlidePager) {
            mPlayerPresenter.playByIndex(position);
        }
        mIsUserSlidePager =false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
