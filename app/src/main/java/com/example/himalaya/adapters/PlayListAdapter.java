package com.example.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.views.PlayListPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.InnerHolder> {

    private List<Track> mData = new ArrayList<>();
    private int playingIndex = 0;
    private PlayListPopWindow.PlayListItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public PlayListAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //设置RecyclerView的Item
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list,parent,false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListAdapter.InnerHolder holder,final int position) {

        //设置列表点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(position);
                }
            }
        });

        //（绑定）设置数据
        Track track = mData.get(position);

        TextView trackTitleTv = holder.itemView.findViewById(R.id.track_title_tv);

        //如果列表中有item与当前正在播放的节目一致，那么设置字体颜色为red，否则不变
        trackTitleTv.setTextColor(BaseApplication.getAppContext().getResources().getColor(playingIndex == position?R.color.red:R.color.play_list_text_color));

        trackTitleTv.setText(track.getTrackTitle());

        //找到播放状态的图表
        View playingIconView = holder.itemView.findViewById(R.id.play_icon_iv);
        //如果列表中有item与当前正在播放的节目一致，那么展示该播放图标，否则设置为隐藏
        playingIconView.setVisibility(playingIndex == position?View.VISIBLE:View.GONE);
    }

    //返回的数据个数
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Track> data) {
        //设置播放数据，更新列表
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setCurrentPlayPosition(int position) {
        playingIndex = position;
        notifyDataSetChanged();
    }

    //PlayListPopWindow里调用
    public void setOnItemClickListener(PlayListPopWindow.PlayListItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public class InnerHolder extends RecyclerView.ViewHolder{
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
