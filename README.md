#### 最新版本

模块|WxPay
---|---
最新版本|[![Download](https://jitpack.io/v/like5188/WxPay.svg)](https://jitpack.io/#like5188/WxPay)

## 功能介绍

1、微信支付封装

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
        implementation 'com.github.like5188:WxPay:版本号'
        // 引用LiveDataBus库，用于接收返回结果
        implementation 'com.github.like5188.LiveDataBus:livedatabus:1.2.2'
        kapt 'com.github.like5188.LiveDataBus:livedatabus_compiler:1.2.2'
    }
```

2、在Application中进行一次初始化。
```java
    WXPayUtils.getInstance(this).init(appid)
```

3、支付
```java
    WXPayUtils.getInstance(this).pay(payParams)
```

4、接收返回结果
```java
    在任意一个类中注册
    LiveDataBus.register(this, this);
```
        然后用下面三个方法接收支付宝返回的结果
```java
    // 支付成功
    @RxBusSubscribe(WXPayUtils.TAG_PAY_SUCCESS)
    public void onPaySuccess() {
    }
```
```java
    // 支付失败
    @RxBusSubscribe(WXPayUtils.TAG_PAY_FAILURE)
    public void onPayFailure() {
    }
```

5、Proguard
```java
    # LiveDataBus
    -keep class * extends com.like.livedatabus.Bridge
    -keep class com.like.livedatabus_annotations.**{*;}
```