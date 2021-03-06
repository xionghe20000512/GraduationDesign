package com.example.himalaya.utils;

public class Constants {

    //获取推荐列表的专辑数量
    public static int COUNT_RECOMMEND=10;

    //默认列表请求数量(搜索结果,detail)
    public static int COUNT_DEFAULT=20;

    //热词的数量
    public static int COUNT_HOT_WORD=10;

    //数据库相关的常量
    public static final String DB_NAME = "ximalaya.db";
    //数据库的版本
    public static final int DB_VERSION_CODE = 1;
    //订阅的表名，图片，title，描述，播放量，节目数量，作者名称（详情界面），专辑id
    public static final String SUB_TB_NAME = "tb_subscription";
    public static final String SUB_ID = "_id";
    public static final String SUB_COVER_URL = "cover";
    public static final String SUB_TITLE = "title";
    public static final String SUB_DESCRIPTION = "description";
    public static final String SUB_PLAY_COUNT = "playCount";
    public static final String SUB_TRACKS_COUNT = "tracksCount";
    public static final String SUB_AUTHOR_NAME = "authorName";
    public static final String SUB_ALBUM_ID = "albumId";

    //订阅上限
    public static final int MAX_SUB_COUNT = 5;

    //历史记录表名
    public static final String HISTORY_TB_NAME = "tb_history";
    public static final String HISTORY_ID = "_id";
    public static final String HISTORY_TRACK_ID = "historyTrackId";//这个是track里的id
    public static final String HISTORY_TITLE = "historyTitle";
    public static final String HISTORY_PLAY_COUNT = "historyPlayCount";
    public static final String HISTORY_DURATION = "historyDuration";
    public static final String HISTORY_UPDATE_TIME = "historyUpdateTime";
    public static final String HISTORY_COVER = "historyCover";
    public static final String HISTORY_AUTHOR = "historyAuthor";

    //最大历史记录数
    public static final int MAX_HISTORY_COUNT = 15;


}
