package com.example.himalaya.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;

import androidx.appcompat.widget.AppCompatImageView;

import android.util.AttributeSet;

//将封面图片改成圆角
public class RoundRectImageView  extends AppCompatImageView {

    private float roundRatio=0.1f;
    private Path path;

    public RoundRectImageView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }
    @Override
    protected void onDraw(Canvas canvas){
        if(path==null){
            path=new Path();
            path.addRoundRect(new RectF(0,0,getWidth(),getHeight()),roundRatio * getWidth(),roundRatio * getHeight(),Path.Direction.CW);
        }
        canvas.save();
        canvas.clipPath(path);
        super.onDraw(canvas);
        canvas.restore();
    }
}
