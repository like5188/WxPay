package com.like.wxpay.sample.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.like.logger.Logger;
import com.like.toast.ToastUtilsKt;
import com.like.wxpay.WXPayUtils;
import com.like.wxpay.sample.R;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        WXPayUtils.getInstance(this).getWeixinAPI().handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        WXPayUtils.getInstance(this).getWeixinAPI().handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Logger.d("onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
                ToastUtilsKt.shortToastCenter(this, "支付成功");
            } else {
                ToastUtilsKt.shortToastCenter(this, "支付失败：" + String.valueOf(resp.errCode));
            }
        }
    }
}