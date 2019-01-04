package com.like.wxpay.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.like.wxpay.WxPayUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WxPayUtils.getInstance(this).init("wxb4ba3c02aa476ea1")
    }

    fun pay(view: View) {
        WxPayUtils.getInstance(this).pay("{\"appid\":\"wxb4ba3c02aa476ea1\",\"partnerid\":\"1900006771\",\"package\":\"Sign=WXPay\",\"noncestr\":\"ffd8363d5149de96acfb21f861b6f2d7\",\"timestamp\":1546589467,\"prepayid\":\"wx04161107414587fcf981bea61635925982\",\"sign\":\"3618C1D08BC50D94F2952C3C3A7F7B3B\"}")
    }
}