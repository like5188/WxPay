# WxPay

微信支付

## 使用方法：

1、引用

在Project的gradle中加入：
```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
在Module的gradle中加入：
```groovy
    dependencies {
        compile 'com.github.like5188:WxPay:2.1.5'
    }
```
2、初始化
  ```java
    WXPayUtils.getInstance(this).init(appid)
  ```
3、支付结果回调
  参照微信SDK Sample，在net.sourceforge.simcpux.wxapi包路径中实现WXPayEntryActivity类(包名或类名不一致会造成无法回调)，在WXPayEntryActivity类中实现onResp函数，支付完成后，微信APP会返回到商户APP并回调onResp函数，开发者需要在该函数中接收通知，判断返回错误码，如果支付成功则去后台查询支付结果再展示用户实际支付结果。注意一定不能以客户端返回作为用户支付的结果，应以服务器端的接收的支付通知或查询API返回的结果为准。代码示例如下：
  ```java
    publicvoidonResp(BaseRespresp){
        if(resp.getType()==ConstantsAPI.COMMAND_PAY_BY_WX){
            Log.d(TAG,"onPayFinish,errCode="+resp.errCode);
            AlertDialog.Builderbuilder=newAlertDialog.Builder(this);
            builder.setTitle(R.string.app_tip);
        }
    }
  ```
4、回调中errCode值列表：
    0  成功 展示成功页面
    -1 错误 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
    -2 用户取消 无需处理。发生场景：用户不支付了，点击取消，返回APP。
