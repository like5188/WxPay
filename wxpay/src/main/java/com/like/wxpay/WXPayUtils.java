package com.like.wxpay;

import android.app.Activity;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class WXPayUtils {
    private Activity mActivity;
    private IWXAPI mWeixinAPI;

    private volatile static WXPayUtils sInstance;

    private WXPayUtils(Activity activity) {
        mActivity = activity;
        mWeixinAPI = WXAPIFactory.createWXAPI(mActivity, WXConstants.APP_ID, false);
        mWeixinAPI.registerApp(WXConstants.APP_ID);
    }

    public static WXPayUtils getInstance(Activity activity) {
        if (sInstance == null) {
            synchronized (WXPayUtils.class) {
                if (sInstance == null) {
                    sInstance = new WXPayUtils(activity);
                }
            }
        }
        return sInstance;
    }

    /**
     * 支付
     *
     * @param payParams 支付参数，json格式
     */
    public void pay(String payParams) {
        if (!mWeixinAPI.isWXAppInstalled()) {
            // 提醒用户没有安装微信
            ToastUtils.showShortCenter(mActivity, "您还没有安装微信");
            return;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(payParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            PayParams params = new PayParams().parse(jsonObject);
            PayReq req = new PayReq();
            req.appId = WXConstants.APP_ID;
            req.partnerId = params.partnerid;
            req.prepayId = params.prepayid;
            req.nonceStr = params.noncestr;
            req.timeStamp = params.timestamp;
            req.packageValue = params.packagevalue;
            req.sign = params.sign;
            // ToastUtils.showShortCenter(mActivity, "正常调起支付");
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            mWeixinAPI.sendReq(req);
        }
    }

    class PayParams {
        public String partnerid;
        public String prepayid;
        public String noncestr;
        public String timestamp;
        public String packagevalue;
        public String sign;

        public PayParams parse(JSONObject jsonObject) {
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
