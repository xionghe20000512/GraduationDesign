package com.example.himalaya.base;

import android.app.Application;
import android.content.Context;

import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.DeviceInfoProviderDefault;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDeviceInfoProvider;

//implements IIdentifierListener
public class BaseApplication extends Application  {
    String oaid;

    //P3
    @Override
    public void onCreate() {
        super.onCreate();

        CommonRequest mXimalaya = CommonRequest.getInstanse();
        if (DTransferConstants.isRelease) {
            String mAppSecret = "8646d66d6abe2efd14f2891f9fd1c8af";
            mXimalaya.setAppkey("9f9ef8f10bebeaa83e71e62f935bede8");
            mXimalaya.setPackid("com.app.test.android");
//            mXimalaya.init(this,mAppSecret, (IDeviceInfoProvider) getApplicationInfo());
            mXimalaya.init(this,mAppSecret,getDeviceInfoProvider(this));
        } else {
//                String mAppSecret = "0a09d7093bff3d4947a5c4da0125972e";
//                mXimalaya.setAppkey("f4d8f65918d9878e1702d49a8cdf0183");
//                mXimalaya.setPackid("com.ximalaya.qunfeng");
//                mXimalaya.init(this, mAppSecret, getDeviceInfoProvider(this));
            String mAppSecret = "ff31ae8153185db13b5f5393cae962c4";
            mXimalaya.setAppkey("be022ee6e9f19df55c4a6eb836b7b0b9");
            mXimalaya.setPackid("android.test");
            mXimalaya.init(this,mAppSecret,getDeviceInfoProvider(this));
        }

        //初始化Log
        LogUtil.init(this.getPackageName(), false);//获取到包名，是否发布

    }

    public IDeviceInfoProvider getDeviceInfoProvider(Context context) {
        return new DeviceInfoProviderDefault(context) {
            @Override
            public String oaid() {
                return oaid;
            }
        };
    }

}
