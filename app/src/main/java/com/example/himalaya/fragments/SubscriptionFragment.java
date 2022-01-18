package com.example.himalaya.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseFragment;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public class SubscriptionFragment extends BaseFragment {
    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.fragment_subcription, container,false);//是否需要绑定到container里（false）
        return rootView;
    }

//    @Override
//    public void onRecommendListLoaded(List<Album> result) {
//
//    }
//
//    @Override
//    public void onLoaderMore(List<Album> result) {
//
//    }
//
//    @Override
//    public void onRefreshMore(List<Album> result) {
//
//    }
}
