package com.example.himalaya.base;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public abstract class BaseFragment extends Fragment {

    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView=onSubViewLoaded(inflater, container);
        return mRootView;
    }

    protected abstract View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container);

//    public abstract void onRecommendListLoaded(List<Album> result);
//
//    public abstract void onLoaderMore(List<Album> result);
//
//    public abstract void onRefreshMore(List<Album> result);
}
