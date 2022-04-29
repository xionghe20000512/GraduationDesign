package com.example.himalaya.data;

import com.example.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

/**
 * model层获取数据
 * 把所有跟获取数据相关的操作都整合到这里
 */

public class XimalayaApi {

    //单例
    private XimalayaApi(){

    }

    private static XimalayaApi sXimalayaApi;

    public static XimalayaApi getXimalayaApi(){
        if (sXimalayaApi==null) {
            synchronized (XimalayaApi.class){
                if (sXimalayaApi==null) {
                    sXimalayaApi=new XimalayaApi();
                }
            }
        }
        return sXimalayaApi;
    }

    //喜马拉雅SDK接入文档提供的写法

    //recommendPresenter
    /**
     * 获取推荐的内容
     * @param callback 请求结果的回调接口
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callback){
        Map<String, String> map = new HashMap<>();
        //表示数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMEND+"");
        CommonRequest.getGuessLikeAlbum(map,callback);
    }

    //AlbumDetailPresenter
    /**
     * 根据专辑的id获取专辑内容
     * @param callback 获取专辑详情的回调接口
     * @param albumId 专辑id
     * @param pageIndex 页码
     */
    public void getAlbumDetail(IDataCallBack<TrackList> callback,long albumId,int pageIndex){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId+"");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, pageIndex+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT+"");
        CommonRequest.getTracks(map,callback);
    }

    //searchPresenter
    /**
     * 根据关键字进行搜索
     * @param keyword
     */
    public void searchByKeyword(String keyword,int page,IDataCallBack<SearchAlbumList> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        map.put(DTransferConstants.PAGE, page+"");
        map.put(DTransferConstants.PAGE_SIZE,Constants.COUNT_DEFAULT+"");
        CommonRequest.getSearchedAlbums(map, callback);
    }

    //searchPresenter
    /**
     * 获取推荐的热词
     * @param callback
     */
    public void getHotWords(IDataCallBack<HotWordList> callback){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TOP, Constants.COUNT_HOT_WORD+"");//top-->int-->获取前top长度的热搜词。（1<=top<=20：目前top只支持最多20个）
        CommonRequest.getHotWords(map,callback);
    }

    /**
     * 根据关键字获取联想词
     * @param keyword 关键字
     * @param callback 回调
     */
    public void getSuggestWord(String keyword,IDataCallBack<SuggestWords> callback){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);//keyword-->String-->搜索查询词参数
        CommonRequest.getSuggestWord(map, callback);
    }
}
