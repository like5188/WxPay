package com.like.wxpay

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.json.JSONException
import org.json.JSONObject

object WxPayUtils {
    private val TAG = WxPayUtils::class.java.simpleName

    /**
     * 支付成功
     */
    const val TAG_PAY_SUCCESS = "TAG_PAY_SUCCESS"

    /**
     * 支付失败
     */
    const val TAG_PAY_FAILURE = "TAG_PAY_FAILURE"

    private lateinit var appId: String
    lateinit var wxApi: IWXAPI

    fun init(context: Context, appId: String) {
        this.appId = appId
        wxApi = WXAPIFactory.createWXAPI(context.applicationContext, appId, true)
        Log.d(TAG, "微信appId($appId)注册：${wxApi.registerApp(appId)}")
    }

    /**
     * 支付
     *
     * @param payParams 支付参数，json格式
     */
    fun pay(context: Context, payParams: String) {
        if (appId.isEmpty()) {
            throw UnsupportedOperationException("支付之前必须先调用init()方法进行初始化")
        }
        if (!wxApi.isWXAppInstalled) {
            // 提醒用户没有安装微信
            Toast.makeText(context, "您还没有安装微信", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            JSONObject(payParams).apply {
                val req = PayReq()
                req.appId = appId
                req.partnerId = this.optString("partnerid")
                req.prepayId = this.optString("prepayid")
                req.nonceStr = this.optString("noncestr")
                req.timeStamp = this.optString("timestamp")
                req.packageValue = this.optString("package")
                req.sign = this.optString("sign")
                req.extData = this.optString("extData")
                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                if (req.checkArgs()) {// 检查订单信息
                    Log.d(TAG, "调用微信支付：${wxApi.sendReq(req)}")
                } else {
                    Toast.makeText(context, "订单信息错误", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(context, "订单信息错误", Toast.LENGTH_SHORT).show()
        }
    }

}