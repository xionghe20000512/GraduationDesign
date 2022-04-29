package com.example.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.InnerHolder> {

    private List<Track> mDetailData=new ArrayList<>();
    //格式化时间
    private SimpleDateFormat mUpdateDateFormat=new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mDurationFormat=new SimpleDateFormat("mm:ss");
    private ItemClickListener mItemClickListener=null;
    private ItemLongClickListener mItemLongClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //找到控件
        View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail,parent,false);
        return new InnerHolder(itemView);
    }

    //把获取到的数据放到UI上
    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        //找到控件
        View itemView=holder.itemView;
        //顺序id
        TextView orderTv=itemView.findViewById(R.id.order_text);
        //标题
        TextView titleTv=itemView.findViewById(R.id.detail_item_title);
        //播放次数
        TextView playCountTv=itemView.findViewById(R.id.detail_item_play_count);
        //播放时长
        TextView durationTv=itemView.findViewById(R.id.detail_item_duration);
        //更新日期
        TextView updateDateTv=itemView.findViewById(R.id.detail_item_update_time);

        //设置数据
        final Track track=mDetailData.get(position);
        orderTv.setText((position+1)+"");//列表序号，现在是从一开始，不然从零开始
        titleTv.setText(track.getTrackTitle());
        playCountTv.setText(track.getPlayCount()+"");

        int durationMil = track.getDuration()*1000;
        String duration=mDurationFormat.format(durationMil);//格式化时间
        durationTv.setText(duration);

        String updateTimeText=mUpdateDateFormat.format(track.getUpdatedAt());//格式化日期
        updateDateTv.setText(updateTimeText);

        //设置item的点击事件,在adapter里点的
        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(), "you clicked "+position+" item", Toast.LENGTH_SHORT).show();
                if (mItemClickListener != null) {
                    //参数需要有列表和位置
                    mItemClickListener.onItemClick(mDetailData,position);
                }
            }
        });

        //设置长按事件
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemLongClickListener != null) {
                    mItemLongClickListener.onItemLongClick(track);
                }
                //true表示消费掉该事件
                //意思是长按之后不会点击跳转到详情界面
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDetailData.size();
    }

    public void setData(List<Track> tracks) {
        //清除原来的数据
        mDetailData.clear();
        //添加新的数据
        mDetailData.addAll(tracks);
        //更新UI
        notifyDataSetChanged();

    }

    public class InnerHolder extends RecyclerView.ViewHolder{
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    public void setItemClickListener(ItemClickListener listener){
        this.mItemClickListener=listener;
    }

    //由DetailActivity实现，DetailListAdapter调用
    public interface ItemClickListener{
        //暴露接口给activity,重写方法
        void onItemClick(List<Track> mDetailData, int position);
    }

    public void setItemLongClickListener(ItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }

    //长按
    //activity实现，adapter调用
    public interface ItemLongClickListener{
        //暴露接口给activity,重写方法
        void onItemLongClick(Track track);
    }
}
