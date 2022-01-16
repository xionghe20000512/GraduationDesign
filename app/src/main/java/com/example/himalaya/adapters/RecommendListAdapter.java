package com.example.himalaya.adapters;

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
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;

import java.util.ArrayList;
import java.util.List;

public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.InnerHolder> {

    private List<Album> mData = new ArrayList<>();

    @NonNull
    @Override
    public  InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //这里是找到View
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);//把父控件传进去，并且不绑定，仅供参考

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position){
        //这里是设置数据
        holder.itemView.setTag(position);
        holder.setData(mData.get(position));
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

            //获取封面图片
            Picasso.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(albumCoverTv);

            //设置主标题内容
            albumTitleTv.setText(album.getAlbumTitle());
            //设置描述（子标题）内容
            albumDesTv.setText(album.getAlbumIntro());
            //设置播放量
            albumPlayCountTv.setText(album.getPlayCount()+"");
            //设置专辑内容数量（集数）
            albumContentCountTv.setText(album.getIncludeTrackCount()+"");
        }
    }
}
