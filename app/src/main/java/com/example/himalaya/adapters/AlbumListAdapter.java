package com.example.himalaya.adapters;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * 这是原来的RecommendListAdapter
 *
 *
 */
public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHolder> {

    private static final String TAG="RecommendListAdapter";

    private List<Album> mData = new ArrayList<>();
    private OnAlbumItemClickListener mItemClickListener=null;
    private OnAlbumItemLongClickListener mLongClickListener = null;

    @NonNull
    @Override
    public  InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //这里是找到View
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);//把父控件传进去，并且不绑定，仅供参考

        return new InnerHolder(itemView);
    }

    //点击的位置
    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder,int position){
        //这里是设置数据
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener!=null) {
                    int clickPosition=(int) v.getTag();
                    mItemClickListener.onItemClick(clickPosition,mData.get(clickPosition));
                }
                Log.d(TAG,"holder.itemView was click --> "+v.getTag());
            }
        });
        holder.setData(mData.get(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mLongClickListener != null) {
                    int clickPosition=(int) v.getTag();
                    mLongClickListener.onItemLongClick(mData.get(clickPosition));
                }
                //true表示消费掉该事件
                //意思是长按之后不会点击跳转到详情界面
                return true;
            }
        });
    }

    @Override
    public int getItemCount(){
        //返回要显示的个数（如果不为空）
        if(mData!=null){
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Album> albumList){
        //如果不为空，先把原来的数据清除掉，再新添加全部专辑
        if(mData!=null){
            mData.clear();
            mData.addAll(albumList);
        }
        //更新UI
        /**
         * Android中，通常在列表中添加、删除或者是修改数据，notifyDataSetChanged()可以在适配器绑定的数组后，不用重新刷新Activity，
         * 通知Activity的--适配器--更新列表的数据即可。常用的列表指的是listview、recycleview。
         *
         * 必须要强调的就是notifyDataSetChanged必须是列表和适配器都初始化好了之后。
         *
         * notifyDataSetChanged()会记住你划到的位置，重新加载数据的时候不会改变位置，只是改变了数据
         */
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder{
        public InnerHolder(View itemView){
            super(itemView);
        }

        public void setData(Album album) {
            //找到各个控件，设置数据
            //专辑封面
            ImageView albumCoverTv=itemView.findViewById(R.id.album_cover);
            //主标题
            TextView albumTitleTv=itemView.findViewById(R.id.album_title_tv);
            //描述（子标题）
            TextView albumDesTv=itemView.findViewById(R.id.album_description_tv);
            //播放数量
            TextView albumPlayCountTv=itemView.findViewById(R.id.album_play_count);
            //专辑内容数量（集数)
            TextView albumContentCountTv=itemView.findViewById(R.id.album_content_size);

            //设置主标题内容
            albumTitleTv.setText(album.getAlbumTitle());
            //设置描述（子标题）内容
            albumDesTv.setText(album.getAlbumIntro());
            //设置播放量
            albumPlayCountTv.setText(album.getPlayCount()+"");
            //设置专辑内容数量（集数）
            albumContentCountTv.setText(album.getIncludeTrackCount()+"");

            //获取封面图片
            String coverUrlLarge = album.getCoverUrlLarge();
            if (!TextUtils.isEmpty(coverUrlLarge)) {
                //图片不为空才设置
                Picasso.with(itemView.getContext()).load(coverUrlLarge).into(albumCoverTv);
            }else{
                //图片为空就设置为默认图片
                albumCoverTv.setImageResource(R.mipmap.icon_blank);
            }

        }
    }

    public void setAlbumItemClickListener(OnAlbumItemClickListener listener){
        this.mItemClickListener=listener;
    }

    //由RecommendFragment实现，RecommendListAdapter调用
    //对应的activity实现，adapter调用
    public interface OnAlbumItemClickListener {
        void onItemClick(int position, Album album);
    }

    public void setOnAlbumItemLongClickListener(OnAlbumItemLongClickListener listener){
        this.mLongClickListener = listener;
    }

    /**
     * item长按的接口（长按取消订阅）
     */
    public interface OnAlbumItemLongClickListener{
        void onItemLongClick(Album album);
    }

}
