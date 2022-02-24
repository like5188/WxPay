package com.like.wxpay.sample

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.like.floweventbus.FlowEventBus
import com.like.floweventbus_annotations.BusObserver
import com.like.wxpay.WxPayUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WxPayUtils.init(this, "wxb4ba3c02aa476ea1")
        FlowEventBus.register(this)
    }

    fun pay(view: View) {
        WxPayUtils.pay(
            this,
            "{\"appid\":\"wxb4ba3c02aa476ea1\",\"partnerid\":\"1900006771\",\"package\":\"Sign=WXPay\",\"noncestr\":\"ffd8363d5149de96acfb21f861b6f2d7\",\"timestamp\":1546589467,\"prepayid\":\"wx04161107414587fcf981bea61635925982\",\"sign\":\"3618C1D08BC50D94F2952C3C3A7F7B3B\"}"
        )
    }

    @BusObserver([WxPayUtils.TAG_PAY_SUCCESS])
    fun onPaySuccess() {
        Toast.makeText(this, "微信支付成功", Toast.LENGTH_SHORT).show()
    }

    @BusObserver([WxPayUtils.TAG_PAY_FAILURE])
    fun onPayFailure() {
        Toast.makeText(this, "微信支付失败", Toast.LENGTH_SHORT).show()
    }
}