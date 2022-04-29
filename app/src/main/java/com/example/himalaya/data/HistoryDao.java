package com.example.himalaya.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IHistoryDao;
import com.example.himalaya.interfaces.IHistoryDaoCallback;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class HistoryDao implements IHistoryDao {

    private static final String TAG = "HistoryDao";
    private final XimalayaDBHelper mDbHelper;
    private IHistoryDaoCallback mCallback = null;
    private Object mLock = new Object();

    public HistoryDao(){
        mDbHelper = new XimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallback(IHistoryDaoCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void addHistory(Track track) {
        synchronized (mLock){
            SQLiteDatabase db = null;
            boolean isSuccess = false;
            try{
                db = mDbHelper.getWritableDatabase();

                //先删除，删除以后再添加
                int delResult = db.delete(Constants.HISTORY_TB_NAME,Constants.HISTORY_TRACK_ID+"=?",new String[]{track.getDataId()+""});//string类型的trackID来代替第二个参数中的问号
                LogUtil.d(TAG,"delResult --> "+delResult);

                db.beginTransaction();
                ContentValues values = new ContentValues();

                //封装数据
                values.put(Constants.HISTORY_TRACK_ID,track.getDataId());
                values.put(Constants.HISTORY_TITLE,track.getTrackTitle());
                values.put(Constants.HISTORY_PLAY_COUNT,track.getPlayCount());
                values.put(Constants.HISTORY_DURATION,track.getDuration());
                values.put(Constants.HISTORY_UPDATE_TIME,track.getUpdatedAt());
                values.put(Constants.HISTORY_AUTHOR,track.getAnnouncer().getNickname());
                values.put(Constants.HISTORY_COVER,track.getCoverUrlLarge());
                //插入数据
                db.insert(Constants.HISTORY_TB_NAME,null,values);
                db.setTransactionSuccessful();
                isSuccess = true;
            }catch (Exception e){
                isSuccess = false;
                e.printStackTrace();
            }finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onHistoryAdd(isSuccess);
                }
            }
        }
    }

    @Override
    public void delHistory(Track track) {
        synchronized (mLock){
            SQLiteDatabase db = null;
            boolean isDelSuccess = false;
            try {
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();//开始事务
                //删除
                /**
                 * table表名
                 *
                 * whereClause为条件例如：_ID = ?
                 *
                 * whereArgs为whereClause的值！
                 */
                int delete = db.delete(Constants.HISTORY_TB_NAME,Constants.HISTORY_TRACK_ID+"=?",new String[]{track.getDataId()+""});//string类型的trackID来代替第二个参数中的问号
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
                    mCallback.onHistoryDel(isDelSuccess);//通知UI结果
                }
            }
        }
    }

    @Override
    public void clearHistory() {
        synchronized (mLock){
            SQLiteDatabase db = null;
            boolean isDelSuccess = false;
            try {
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();//开始事务
                //删除全部历史记录
                db.delete(Constants.HISTORY_TB_NAME,null,null);
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
                    mCallback.onHistoriesClean(isDelSuccess);//通知UI结果
                }
            }
        }

    }

    @Override
    public void listHistories() {
        synchronized (mLock){
            //从数据表中查出所有的历史记录
            SQLiteDatabase db = null;
            List<Track> histories = new ArrayList<>();
            try{
                db = mDbHelper.getReadableDatabase();
                db.beginTransaction();
                //(降序显示，最先添加的放在最上面显示)
                Cursor cursor = db.query(Constants.HISTORY_TB_NAME,null,null,null,null,null,"_id desc");
                while (cursor.moveToNext()) {
                    Track track = new Track();
                    //获取trackID
                    int trackId = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_TRACK_ID));
                    track.setDataId(trackId);
                    //获取title
                    String title = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_TITLE));
                    track.setTrackTitle(title);
                    //获取播放量
                    int playCount = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_PLAY_COUNT));
                    track.setPlayCount(playCount);
                    //获取时长
                    int duration = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_DURATION));
                    track.setDuration(duration);
                    //获取更新时间
                    long updateTime = cursor.getLong(cursor.getColumnIndex(Constants.HISTORY_UPDATE_TIME));
                    track.setUpdatedAt(updateTime);
                    //获取作者
                    String author = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_AUTHOR));
                    Announcer announcer = new Announcer();
                    announcer.setNickname(author);
                    track.setAnnouncer(announcer);
                    //获取图片
                    String cover = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_COVER));
                    track.setCoverUrlLarge(cover);
                    track.setCoverUrlSmall(cover);
                    track.setCoverUrlMiddle(cover);


                    histories.add(track);
                }
                db.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                //通知出去
                if (mCallback != null) {
                    mCallback.onHistoriesLoaded(histories);
                }
            }
        }
    }
}
