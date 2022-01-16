package com.example.himalaya.fragments;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.adapters.RecommendListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.IRecommendPresenter;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.presenters.RecommendPresenter;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback {

    private static final String TAG = "RecommendFragment" ;
    private View mRootView;
    private  RecyclerView mRecommendRv;
    private RecommendListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container){
        //View加载完成
        mRootView=layoutInflater.inflate(R.layout.fragment_recommend,container,false);//是否需要绑定到container里（false）

        //RecyclerView的使用
        //1.找到对应的控件
        mRecommendRv = mRootView.findViewById(R.id.recommend_list);
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
        mRecommendListAdapter=new RecommendListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);

        //获取到逻辑层的对象
        mRecommendPresenter=RecommendPresenter.getInstance();
        //先设置通知接口的注册
        mRecommendPresenter.registerViewCallback(this);

        //获取推荐列表
        mRecommendPresenter.getRecommendList();


        //返回View，给界面显示
        return mRootView;
    }

    @Override
    public void onRecommendListLoaded(List<Album> result){
        //获取到推荐内容的时候，这个方法就会被调用（成功了）
        //数据回来以后，就是更新UI
        //把数据设置给适配器，并且更新UI
        mRecommendListAdapter.setData(result);
    }

    @Override
    public void onLoaderMore(List<Album> result){

    }
    @Override
    public void onRefreshMore(List<Album> result){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消接口的注册，以免内存泄漏
        if (mRecommendPresenter!=null) {
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }
}
