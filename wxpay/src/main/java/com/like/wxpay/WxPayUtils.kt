package com.like.wxpay

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import org.json.JSONException
import org.json.JSONObject
import kotlin.jvm.functions.FunctionN

class WxPayUtils private constructor(private val mContext: Context) {
    companion object : SingletonHolder<WxPayUtils>(object : FunctionN<WxPayUtils> {
        override val arity: Int = 1 // number of arguments that must be passed to constructor

        override fun invoke(vararg args: Any?): WxPayUtils {
            return WxPayUtils(args[0] as Context)
        }
    }) {
        private val TAG = WxPayUtils::class.java.simpleName
        /**
         * 支付成功
         */
        const val TAG_PAY_SUCCESS = "TAG_PAY_SUCCESS"
        /**
         * 支付失败
         */
        const val TAG_PAY_FAILURE = "TAG_PAY_FAILURE"
        var sAppId: String = ""
    }

    private val mWxApi: IWXAPI by lazy { ApiFactory.createWxApi(mContext.applicationContext, sAppId) }

    fun init(appId: String) {
        sAppId = appId
        Log.d(TAG, "微信appId($appId)注册：${mWxApi.registerApp(appId)}")
    }

    /**
     * 支付
     *
     * @param payParams 支付参数，json格式
     */
    fun pay(payParams: String) {
        if (sAppId.isEmpty()) {
            throw UnsupportedOperationException("支付之前必须先调用init()方法进行初始化")
        }
        if (!mWxApi.isWXAppInstalled) {
            // 提醒用户没有安装微信
            Toast.makeText(mContext, "您还没有安装微信", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(mContext, "获取订单中...", Toast.LENGTH_SHORT).show()
        try {
            JSONObject(payParams).apply {
                val req = PayReq()
                req.appId = sAppId
                req.partnerId = this.optString("partnerid")
                req.prepayId = this.optString("prepayid")
                req.nonceStr = this.optString("noncestr")
                req.timeStamp = this.optString("timestamp")
                req.packageValue = this.optString("package")
                req.sign = this.optString("sign")
                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                if (req.checkArgs()) {// 检查订单信息
                    Log.d(TAG, "调用微信支付：${mWxApi.sendReq(req)}")
                } else {
                    Toast.makeText(mContext, "订单信息错误", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(mContext, "订单信息错误", Toast.LENGTH_SHORT).show()
        }
    }

}