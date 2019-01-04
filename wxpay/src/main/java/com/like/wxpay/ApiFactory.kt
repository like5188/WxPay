package com.like.wxpay

import android.content.Context
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

object ApiFactory {
    private var mWxApi: IWXAPI? = null

    fun createWxApi(context: Context, appId: String): IWXAPI {
        val instance1 = mWxApi
        if (instance1 != null) {
            return instance1
        }

        return synchronized(this) {
            val instance2 = mWxApi
            if (instance2 != null) {
                instance2
            } else {
                val created = WXAPIFactory.createWXAPI(context.applicationContext, appId, true)
                mWxApi = created
                created
            }
        }
    }

}