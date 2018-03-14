package com.like.wxpay.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.like.wxpay.WXPayUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WXPayUtils.getInstance(this).init("wxb4ba3c02aa476ea1");
        WXPayUtils.getInstance(this).pay("{\"appid\":\"wxb4ba3c02aa476ea1\",\"partnerid\":\"1900006771\",\"package\":\"Sign=WXPay\",\"noncestr\":\"9ed4e2f1a00597bcd5d6a2c88094760d\",\"timestamp\":1521008462,\"prepayid\":\"wx201803141421023e376ecbde0653253070\",\"sign\":\"9E753150CC1FED6783CF3FBD10DC89A4\"}");
    }
}
