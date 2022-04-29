package com.example.himalaya.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.ISubDao;
import com.example.himalaya.interfaces.ISubDaoCallback;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionDao implements ISubDao {
    private static final String TAG = "SubscriptionDao";
    XimalayaDBHelper mXimalayaDBHelper;
    private ISubDaoCallback mCallback = null;

    //单例
    private static final SubscriptionDao ourInstance = new SubscriptionDao();

    public static SubscriptionDao getInstance(){
        return ourInstance;
    }
    private SubscriptionDao(){
        mXimalayaDBHelper = new XimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallback(ISubDaoCallback callback) {
        this.mCallback=callback;
    }

    @Override
    public void addAlbum(Album album) {
        SQLiteDatabase db = null;
        boolean isAddSuccess = false;
        try {
            db = mXimalayaDBHelper.getWritableDatabase();
            db.beginTransaction();//开始事务
            ContentValues contentValues = new ContentValues();
            //封装数据
            contentValues.put(Constants.SUB_COVER_URL,album.getCoverUrlLarge());
            contentValues.put(Constants.SUB_TITLE,album.getAlbumTitle());
            contentValues.put(Constants.SUB_DESCRIPTION,album.getAlbumIntro());
            contentValues.put(Constants.SUB_PLAY_COUNT,album.getPlayCount());
            contentValues.put(Constants.SUB_TRACKS_COUNT,album.getIncludeTrackCount());
            contentValues.put(Constants.SUB_AUTHOR_NAME,album.getAnnouncer().getNickname());
            contentValues.put(Constants.SUB_ALBUM_ID,album.getId());
            //插入数据
            db.insert(Constants.SUB_TB_NAME,null,contentValues);
            db.setTransactionSuccessful();
            isAddSuccess = true;
        }catch(Exception e){
            e.printStackTrace();
            isAddSuccess = false;
        }finally {
            if (db != null) {
                db.endTransaction();//结束事务
                db.close();//关闭数据库
            }
            if (mCallback != null) {
                mCallback.onAddResult(isAddSuccess);//通知UI添加成功
            }
        }
    }

    @Override
    public void delAlbum(Album album) {
        SQLiteDatabase db = null;
        boolean isDelSuccess = false;
        try {
            db = mXimalayaDBHelper.getWritableDatabase();
            db.beginTransaction();//开始事务
            //ContentValues contentValues = new ContentValues();
            //删除
            int delete = db.delete(Constants.SUB_TB_NAME,Constants.SUB_ALBUM_ID+"=?",new String[]{album.getId()+""});
            LogUtil.d(TAG,"delete --> "+delete);
            db.setTransactionSuccessful();
            isDelSuccess = true;
        }catch(Exception e){
            e.printStackTrace();
            isDelSuccess = false;
        }finally {
            if (db != null) {
                db.endTransaction();//结束事务
                db.close();//关闭数据库
            }
            if (mCallback != null) {
                mCallback.onDeleteResult(isDelSuccess);//通知UI结果（退订成功）
            }
        }
    }

    @Override
    public void listAlbums() {
        SQLiteDatabase db = null;
        List<Album> result = new ArrayList<>();
        try {
            db = mXimalayaDBHelper.getReadableDatabase();
            db.beginTransaction();
            //全部查询出来(降序显示，最先添加的放在最上面显示)
            Cursor query = db.query(Constants.SUB_TB_NAME,null,null,null,null,null,"_id desc");
            //封装数据
            while (query.moveToNext()) {
                Album album = new Album();
                //封面图片
                String coverUrl = query.getString(query.getColumnIndex(Constants.SUB_COVER_URL));
                album.setCoverUrlLarge(coverUrl);
                //获取标题
                String title = query.getString(query.getColumnIndex(Constants.SUB_TITLE));
                album.setAlbumTitle(title);
                //获取描述
                String description = query.getString(query.getColumnIndex(Constants.SUB_DESCRIPTION));
                album.setAlbumIntro(description);
                //获取内容个数
                int tracksCount = query.getInt(query.getColumnIndex(Constants.SUB_TRACKS_COUNT));
                album.setIncludeTrackCount(tracksCount);
                //获取播放量
                int playCount = query.getInt(query.getColumnIndex(Constants.SUB_PLAY_COUNT));
                album.setPlayCount(playCount);
                //获取专辑id
                int albumId = query.getInt(query.getColumnIndex(Constants.SUB_ALBUM_ID));
                album.setId(albumId);
                //获取昵称
                String authorName = query.getString(query.getColumnIndex(Constants.SUB_AUTHOR_NAME));
                Announcer announcer = new Announcer();
                announcer.setNickname(authorName);//获取的是昵称
                album.setAnnouncer(announcer);

                result.add(album);
            }
            query.close();
            db.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (db != null) {
                db.endTransaction();//结束事务
                db.close();//关闭数据库
            }
            //把数据通知出去
            if (mCallback != null) {
                mCallback.onSubListLoaded(result);//通知UI加载订阅
            }
        }
    }
}
