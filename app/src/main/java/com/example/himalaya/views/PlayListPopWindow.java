package com.example.himalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.adapters.PlayListAdapter;
import com.example.himalaya.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;

public class PlayListPopWindow extends PopupWindow {

    private final View mPopView;
    private View mCloseBtn;
    private RecyclerView mTracksList;
    private PlayListAdapter mPlayListAdapter;
    private TextView mPlayModeTV;
    private ImageView mPlayModeIv;
    private View mPlayModeContainer;
    private PlayListActionListener mPlayModeClickListener = null;
    private View mOrderBtnContainer;
    private ImageView mOrderIcon;
    private TextView mOrderText;

    public PlayListPopWindow(){
        //设置宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        /**
         * 点击外部区域关闭PopWindow
         * 这里要注意：设置setOutsideTouchable之前，先要设置：setBackgroundDrawable
         * 否则点击外部无法关闭pop
         */
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        //载入View
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list,null);
        //设置内容
        setContentView(mPopView);
        //设置窗口进入和退出的动画
        setAnimationStyle(R.style.pop_animation);
        //初始化控件
        initView();
        //设置事件
        initEvent();
    }

    private void initEvent() {
        //点击关闭按钮以后，窗口消失
        //动画在上面已经设置好了
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PlayListPopWindow.this.dismiss()也行
                dismiss();
            }
        });

        mPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放模式
                if (mPlayModeClickListener != null) {
                    mPlayModeClickListener.onPlayModeClick();
                }
            }
        });

        //切换播放列表展示顺序
        mOrderBtnContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放列表展示顺序
                mPlayModeClickListener.onOrderClick();
            }
        });

    }

    private void initView() {
        //找到关闭控件
        mCloseBtn = mPopView.findViewById(R.id.play_list_close_btn);
        //找到RecyclerView控件
        mTracksList = mPopView.findViewById(R.id.play_list_rv);

        //设置布局管理器(这样才能使用RecyclerView)
        LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        mTracksList.setLayoutManager(layoutManager);
        //设置适配器(PlayListAdapter)   搭配使用
        mPlayListAdapter = new PlayListAdapter();
        mTracksList.setAdapter(mPlayListAdapter);

        //播放模式相关(播放列表左上角)
        mPlayModeTV = mPopView.findViewById(R.id.play_list_play_mode_tv);
        mPlayModeIv = mPopView.findViewById(R.id.play_list_play_mode_iv);
        mPlayModeContainer = mPopView.findViewById(R.id.play_list_play_mode_container);

        //播放列表展示顺序
        mOrderBtnContainer = mPopView.findViewById(R.id.play_list_order_container);
        mOrderIcon = mPopView.findViewById(R.id.play_list_order_iv);
        mOrderText = mPopView.findViewById(R.id.play_list_order_tv);
    }

    /**
     * 给适配器设置数据
     * @param data
     */
    public void setListData(List<Track> data){
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setData(data);
        }
    }

    //PopWindow里item与当前播放节目一致
    public void setCurrentPlayPosition(int position){
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setCurrentPlayPosition(position);
            //当点开PopUpWindow时，将当前正在播放的item放到可视区域
            mTracksList.scrollToPosition(position);
        }
    }

    //activity里，在initEvent()里调用
    public void setPlayListItemClickListener(PlayListItemClickListener listener){
        mPlayListAdapter.setOnItemClickListener(listener);
    }

    /**
     * 更新播放列表的播放模式（activity里调用）
     * @param mCurrentMode
     */
    public void updatePlayMode(XmPlayListControl.PlayMode mCurrentMode) {
        updatePlayModeBtnImg(mCurrentMode);
    }

    /**
     * 更新切换播放列表展示顺序（按钮和文字的更新）
     * @param isOrder
     */
    public void updateOrderIcon(boolean isOrder){

        //修改图标
        mOrderIcon.setImageResource(!isOrder?R.drawable.selector_play_model_list_order:R.drawable.selector_play_model_list_reverse);
        //修改文字
        mOrderText.setText(BaseApplication.getAppContext().getResources().getString(!isOrder?R.string.order_text:R.string.reverse_text));

    }

    /**
     * 根据当前的状态更新播放模式图标（更新播放列表的播放模式）
     * 1，默认：PLAY_MODEL_LIST
     * 2,列表循环：PLAY_MODEL_LIST_LOOP
     * 3,随机播放：PLAY_MODEL_RANDOM
     * 4,单曲循环：PLAY_MODEL_SINGLE_LOOP
     */
    private void updatePlayModeBtnImg(XmPlayListControl.PlayMode playMode) {
        int resId = R.drawable.selector_play_model_list_order;
        int textId = R.string.play_mode_order_text;
        switch(playMode){
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_play_model_list_order;
                textId = R.string.play_mode_order_text;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_play_model_random;
                textId = R.string.play_mode_random_text;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_play_model_list_order_looper;
                textId = R.string.play_mode_list_play_text;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_play_model_single_loop;
                textId = R.string.play_mode_single_play_text;
                break;
        }
        mPlayModeIv.setImageResource(resId);
        mPlayModeTV.setText(textId);
    }

    //adapter调用
    public interface PlayListItemClickListener{//暴露接口给activity
        //在PlayerActivity里实现
        void onItemClick(int position);
    }

    public void setPlayListActionListener(PlayListActionListener playModeListener){
        mPlayModeClickListener = playModeListener;
    }

    //activity里重写
    public interface PlayListActionListener{
        //PlayListPopWindow里调用
        void onPlayModeClick();//播放模式

        void onOrderClick();//播放列表展示顺序
    }

}
