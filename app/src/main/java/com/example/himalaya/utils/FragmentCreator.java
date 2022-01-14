package com.example.himalaya.utils;

import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.fragments.HistoryFragment;
import com.example.himalaya.fragments.RecommendFragment;
import com.example.himalaya.fragments.SubscriptionFragment;

import java.util.HashMap;
import java.util.Map;

public class FragmentCreator {
    public final static int INDEX_RECOMMEND=0;
    public final static int INDEX_SUBSCRIPTION=1;
    public final static int INDEX_HISTORY=2;

    public final static int PAGE_COUNT=3;

    private static Map<Integer, BaseFragment> sCache=new HashMap<>();//设置缓存是为了避免重复创建，如果创建了就直接从缓存里面拿

    public static BaseFragment getFragment(int index){
        BaseFragment baseFragment=sCache.get(index);
        if(baseFragment!=null){
            return baseFragment;
        }//如果缓存不为null，就直接返回，不用重复创建
        switch(index){
            case INDEX_RECOMMEND:
                baseFragment=new RecommendFragment();
                break;
            case INDEX_SUBSCRIPTION:
                baseFragment=new SubscriptionFragment();
                break;
            case INDEX_HISTORY:
                baseFragment=new HistoryFragment();
                break;
        }
        sCache.put(index,baseFragment);//存入缓存
        return baseFragment;
    }

}
