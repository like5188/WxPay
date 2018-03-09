package com.like.wxpay;

import android.content.Context;

import com.like.logger.Logger;
import com.like.toast.ToastUtilsKt;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class WXPayUtils {
    private Context mContext;
    private String mAppId;
    private IWXAPI mWeixinAPI;

    private volatile static WXPayUtils sInstance;

    private WXPayUtils(Context context) {
        mContext = context;
        mWeixinAPI = WXAPIFactory.createWXAPI(mContext, null);
    }

    public static WXPayUtils getInstance(Context context) {
        if (sInstance == null) {
            synchronized (WXPayUtils.class) {
                if (sInstance == null) {
                    sInstance = new WXPayUtils(context);
                }
            }
        }
        return sInstance;
    }

    public void init(String appId) {
        mAppId = appId;
        mWeixinAPI.registerApp(mAppId);
    }

    /**
     * 支付
     *
     * @param payParams 支付参数，json格式
     */
    public void pay(String payParams) {
        if (!mWeixinAPI.isWXAppInstalled()) {
            // 提醒用户没有安装微信
            ToastUtilsKt.shortToastCenter(mContext, "您还没有安装微信");
            return;
        }
        Logger.d("获取订单中...");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(payParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            PayParams params = new PayParams().parse(jsonObject);
            PayReq req = new PayReq();
            req.appId = mAppId;
            req.partnerId = params.partnerid;
            req.prepayId = params.prepayid;
            req.nonceStr = params.noncestr;
            req.timeStamp = params.timestamp;
            req.packageValue = params.packagevalue;
            req.sign = params.sign;
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            Logger.d("正常调起支付");
            mWeixinAPI.sendReq(req);
        } else {
            Logger.e("订单返回错误");
        }
    }

    class PayParams {
        String partnerid;
        String prepayid;
        String noncestr;
        String timestamp;
        String packagevalue;
        String sign;

        PayParams parse(JSONObject jsonObject) {
            if (jsonObject == null) {
                return null;
            }
            PayParams params = new PayParams();
            try {
                params.partnerid = jsonObject.getString("partnerid");
                params.prepayid = jsonObject.getString("prepayid");
                params.noncestr = jsonObject.getString("noncestr");
                params.timestamp = jsonObject.getString("timestamp");
                params.packagevalue = jsonObject.getString("package");
                params.sign = jsonObject.getString("sign");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return params;
        }

    }

}
