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
        Logger.d("微信appId(" + mAppId + ")注册：" + mWeixinAPI.registerApp(mAppId));
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
        ToastUtilsKt.shortToastCenter(mContext, "获取订单中...");
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
            Logger.d("检查订单信息：" + req.checkArgs());
            Logger.d("调微信支付：" + mWeixinAPI.sendReq(req));
        } else {
            Logger.e("订单信息错误");
            ToastUtilsKt.shortToastCenter(mContext, "订单信息错误");
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
            params.partnerid = jsonObject.optString("partnerid");
            params.prepayid = jsonObject.optString("prepayid");
            params.noncestr = jsonObject.optString("noncestr");
            params.timestamp = jsonObject.optString("timestamp");
            params.packagevalue = jsonObject.optString("package");
            params.sign = jsonObject.optString("sign");
            Logger.d(params);
            return params;
        }

        @Override
        public String toString() {
            return "PayParams{" +
                    "partnerid='" + partnerid + '\'' +
                    ", prepayid='" + prepayid + '\'' +
                    ", noncestr='" + noncestr + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    ", packagevalue='" + packagevalue + '\'' +
                    ", sign='" + sign + '\'' +
                    '}';
        }
    }

}
