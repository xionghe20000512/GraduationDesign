package com.example.himalaya;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="MainActivity";
    //获取分类
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, String> map = new HashMap<>();
        CommonRequest.getCategories(map, new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(@Nullable CategoryList categoryList) {
                List<Category> categories=categoryList.getCategories();
                if(categories !=null){
                    int size=categories.size();
                    Log.d(TAG,"categories size --<"+size);
                    for(Category category:categories){
                        Log.d(TAG,"category-->"+category);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.e(TAG,"error code -- "+i+"error msg -->"+s);
            }
        });
    }
}
