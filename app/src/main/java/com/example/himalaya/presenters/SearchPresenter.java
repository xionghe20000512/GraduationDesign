package com.example.himalaya.presenters;

import androidx.annotation.Nullable;

import com.example.himalaya.data.XimalayaApi;
import com.example.himalaya.interfaces.ISearchCallback;
import com.example.himalaya.interfaces.ISearchPresenter;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private List<Album> mSearchResult = new ArrayList<>();

    private static final String TAG = "SearchPresenter";
    //当前搜索关键字
    private String mCurrentKeyword = null;
    private XimalayaApi mXimalayaApi;
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;

    private SearchPresenter(){
        mXimalayaApi = XimalayaApi.getXimalayaApi();
    }
    private static SearchPresenter sSearchPresenter = null;
    public static SearchPresenter getSearchPresenter(){
        if (sSearchPresenter == null) {
            synchronized (SearchPresenter.class){
                if (sSearchPresenter == null) {
                    sSearchPresenter = new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }

    private List<ISearchCallback> mCallback = new ArrayList<>();

    @Override
    public void doSearch(String keyword) {
        mCurrentPage = DEFAULT_PAGE;
        mSearchResult.clear();//每次做搜索时都要清空，否则会把重复的都加进该集合
        //用于重新搜索
        //当网络不好的时候，用户会点击重新搜索
        this.mCurrentKeyword = keyword;
        search(keyword);
    }

    //搜索方法
    private void search(String keyword) {
        mXimalayaApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                mSearchResult.addAll(albums);//添加给集合
                if (albums != null) {
                    LogUtil.d(TAG,"albums size --> "+albums.size());
                    if (mIsLoadMore) {
                        //如果是加载更多
                        for (ISearchCallback iSearchCallback : mCallback) {
                            iSearchCallback.onLoadMoreResult(mSearchResult,albums.size()!=0);//全加载完了,显示没有更多或继续加载
                        }
                        mIsLoadMore = false;
                    }else{
                        //否则
                        for (ISearchCallback iSearchCallback : mCallback) {
                            iSearchCallback.onSearchResultLoaded(mSearchResult);
                        }
                    }
                }else{
                    LogUtil.d(TAG,"album is null...");
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG,"errorCode --> "+errorCode);
                LogUtil.d(TAG,"errorMsg --> "+errorMsg);
                for (ISearchCallback iSearchCallback : mCallback) {
                    if (mIsLoadMore) {
                        iSearchCallback.onLoadMoreResult(mSearchResult,false);
                        mCurrentPage--;
                        mIsLoadMore = false;
                    }else{
                        iSearchCallback.onError(errorCode,errorMsg);
                    }
                }
            }
        });
    }

    @Override
    public void reSearch() {
        //根据当前keyword重新搜索
        search(mCurrentKeyword);
    }

    private boolean mIsLoadMore = false;

    @Override
    public void loadMore() {
        //判断有没有必要进行加载更多
        if (mSearchResult.size()< Constants.COUNT_DEFAULT) {
            //没有更多（size<一页个数）
            for (ISearchCallback iSearchCallback : mCallback) {
                iSearchCallback.onLoadMoreResult(mSearchResult,false);
            }
        }else{
            //有更多
            mIsLoadMore = true;
            mCurrentPage++;
            search(mCurrentKeyword);
        }
    }

    //获取热词
    @Override
    public void getHotWord() {
        mXimalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();
                    LogUtil.d(TAG,"hotWords size --> "+hotWords.size());
                    for (ISearchCallback iSearchCallback : mCallback) {
                        iSearchCallback.onHotWordLoaded(hotWords);
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG,"getHotWord errorCode --> "+errorCode);
                LogUtil.d(TAG,"getHotWord errorMsg --> "+errorMsg);
            }
        });
    }

    //根据关键字获取联想词
    @Override
    public void getRecommendWord(String keyword) {
        mXimalayaApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(@Nullable SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    LogUtil.d(TAG,"keyWordList size --> "+keyWordList.size());
                    for (ISearchCallback iSearchCallback : mCallback) {
                        iSearchCallback.onRecommendWordLoaded(keyWordList);
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG,"getRecommendWord errorCode --> "+errorCode);
                LogUtil.d(TAG,"getRecommendWord errorMsg --> "+errorMsg);
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {

        if (mCallback!=null&&!mCallback.contains(iSearchCallback)) {
            mCallback.add(iSearchCallback);
        }

    }

    @Override
    public void unRegisterViewCallback(ISearchCallback iSearchCallback) {
        if (mCallback != null) {
            mCallback.remove(iSearchCallback);
        }
    }
}
