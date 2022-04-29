package com.example.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.DetailActivity;
import com.example.himalaya.R;
import com.example.himalaya.adapters.AlbumListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.RecommendPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, UILoader.OnRetryClickListener, AlbumListAdapter.OnAlbumItemClickListener {

    private static final String TAG = "RecommendFragment" ;
    private View mRootView;
    private RecyclerView mRecommendRv;
    private AlbumListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader mUiLoader;
    private TwinklingRefreshLayout mTwinklingRefreshLayout;

    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container){

        mUiLoader= new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater,container);
            }
        };

        //获取到逻辑层的对象
        mRecommendPresenter=RecommendPresenter.getInstance();
        //先设置通知接口的注册（注册回调就是为了通知UI改变状态）
        mRecommendPresenter.registerViewCallback(this);
        //获取推荐列表
        mRecommendPresenter.getRecommendList();

        //解绑之前绑定的View，android里不允许？
        if (mUiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
        }

        mUiLoader.setOnRetryClickListener(this);

        //返回View，给界面显示
        return mUiLoader;
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        //View加载完成
        mRootView=layoutInflater.inflate(R.layout.fragment_recommend,container,false);//是否需要绑定到container里（false）

        //RecyclerView的使用
        //1.找到对应的控件
        mRecommendRv = mRootView.findViewById(R.id.recommend_list);
        mTwinklingRefreshLayout = mRootView.findViewById(R.id.over_scroll_view);//找到控件(推荐界面添加下拉回弹效果)
        mTwinklingRefreshLayout.setPureScrollModeOn();
        //2.设置布局管理器
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);//设为垂直方向
        mRecommendRv.setLayoutManager(linearLayoutManager);
        //使RecyclerView每一条目产生上下左右间距
        mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration(){
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
                outRect.top= UIUtil.dip2px(view.getContext(),5);//把px转dp
                outRect.bottom= UIUtil.dip2px(view.getContext(),5);
                outRect.left= UIUtil.dip2px(view.getContext(),5);
                outRect.right= UIUtil.dip2px(view.getContext(),5);
            }
        });
        //3.设置适配器
        mRecommendListAdapter=new AlbumListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);
        //设置点击事件
        mRecommendListAdapter.setAlbumItemClickListener(this);
        return mRootView;
    }

    @Override
    public void onRecommendListLoaded(List<Album> result){
        LogUtil.d(TAG,"onRecommendListLoaded");
        //获取到推荐内容的时候，这个方法就会被调用（成功了）
        //数据回来以后，就是更新UI
        //把数据设置给适配器，并且更新UI
        mRecommendListAdapter.setData(result);
        mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    //网络不好时调用此方法，显示相应的页面
    @Override
    public void onNetworkError() {
        LogUtil.d(TAG,"onNetworkError");
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    //内容为空时调用此方法，显示相应界面
    @Override
    public void onEmpty() {
        LogUtil.d(TAG,"onEmpty");
        mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    //加载中调用此方法，显示相应界面
    @Override
    public void onLoading() {
        LogUtil.d(TAG,"onLoading");
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消接口的注册，以免内存泄漏
        if (mRecommendPresenter!=null) {
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        //表示网络不佳的时候用户点击重试
        //重新获取数据即可
        if (mRecommendPresenter != null) {
            mRecommendPresenter.getRecommendList();
        }
    }

    //adapter里调用
    @Override
    public void onItemClick(int position, Album album) {
        //完成界面的跳转

        //设置跳转的目标专辑
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);

        //item被点击了,跳转到详情界面
        Intent intent=new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }
}
