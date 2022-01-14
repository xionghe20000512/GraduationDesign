package com.example.himalaya.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseFragment;

public class RecommendFragment extends BaseFragment {
    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container){
        View rootView=layoutInflater.inflate(R.layout.fragment_recommend,container,false);//是否需要绑定到container里（false）
        return rootView;
    }
}
