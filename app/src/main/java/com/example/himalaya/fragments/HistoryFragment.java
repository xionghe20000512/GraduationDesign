package com.example.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.PlayerActivity;
import com.example.himalaya.R;
import com.example.himalaya.adapters.TrackListAdapter;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.IHistoryCallback;
import com.example.himalaya.presenters.HistoryPresenter;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.ConfirmCheckBoxDialog;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class HistoryFragment extends BaseFragment implements IHistoryCallback,TrackListAdapter.ItemClickListener,TrackListAdapter.ItemLongClickListener,ConfirmCheckBoxDialog.OnDialogActionClickListener {

    private static final String TAG = "HistoryFragment";
    private UILoader mUiLoader;
    private TrackListAdapter mTrackListAdapter;
    private HistoryPresenter mHistoryPresenter;
    private Track mCurrentClickHistoryItem = null;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container){
        //主框架
        FrameLayout rootView=(FrameLayout) layoutInflater.inflate(R.layout.fragment_history,container,false);//是否需要绑定到container里（false）
        if (mUiLoader == null) {
            mUiLoader = new UILoader(BaseApplication.getAppContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }

                //复写内容为空的界面的方法
                @Override
                protected View getEmptyView(){
                    //先找到UI控件
                    View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view,this ,false);
                    TextView tips = emptyView.findViewById(R.id.empty_view_tips_tv);
                    //修改文字
                    tips.setText(R.string.no_history_tips_text);
                    return emptyView;
                }
            };
        }else{
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
        }
        //HistoryPresenter
        mHistoryPresenter = HistoryPresenter.getHistoryPresenter();
        mHistoryPresenter.registerViewCallback(this);
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        mHistoryPresenter.listHistories();
        rootView.addView(mUiLoader);
        return rootView;
    }

    private View createSuccessView(ViewGroup container) {
        View successView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_history,container,false);
        //设置回弹效果
        TwinklingRefreshLayout refreshLayout = successView.findViewById(R.id.over_scroll_view);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadmore(false);
        //recyclerView
        RecyclerView historyList = successView.findViewById(R.id.history_list);
        historyList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        //设置上下间距
        historyList.addItemDecoration(new RecyclerView.ItemDecoration(){
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
                outRect.top= UIUtil.dip2px(view.getContext(),5);//把px转dp
                outRect.bottom= UIUtil.dip2px(view.getContext(),5);
                outRect.left= UIUtil.dip2px(view.getContext(),5);
                outRect.right= UIUtil.dip2px(view.getContext(),5);
            }
        });

        //设置适配器
        mTrackListAdapter = new TrackListAdapter();
        mTrackListAdapter.setItemClickListener(this);
        mTrackListAdapter.setItemLongClickListener(this);
        historyList.setAdapter(mTrackListAdapter);
        return successView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHistoryPresenter != null) {
            mHistoryPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onHistoriesLoaded(List<Track> tracks) {
        if (tracks == null || tracks.size() == 0) {
            //为空展示空界面
            mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
        }else{
            //更新数据
            mTrackListAdapter.setData(tracks);
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }

    }

    @Override
    public void onItemClick(List<Track> mDetailData, int position) {
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(mDetailData,position);
        //跳转到播放器界面
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        startActivity(intent);
        LogUtil.d(TAG,"success");
    }

    @Override
    public void onItemLongClick(Track track) {
        this.mCurrentClickHistoryItem = track;
        //删除历史
        //Toast.makeText(getActivity(),"历史记录长按--"+track.getTrackTitle(),Toast.LENGTH_SHORT).show();
        ConfirmCheckBoxDialog dialog = new ConfirmCheckBoxDialog(getActivity());
        dialog.setOnDialogActionClickListener(this);
        dialog.show();
    }

    @Override
    public void onCancelHst() {
        //不用做(“否”按钮)
    }

    @Override
    public void onDelHst(boolean isCheck) {
        //删除历史
        if (mHistoryPresenter != null && mCurrentClickHistoryItem != null) {
            if (!isCheck) {
                //未选中CheckBox
                mHistoryPresenter.delHistory(mCurrentClickHistoryItem);
            }else{
                //选中
                mHistoryPresenter.cleanHistories();
            }
        }
    }
}
