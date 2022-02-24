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
        // 引用 FlowEventBus 库，用于接收返回结果
        implementation 'com.github.like5188.FlowEventBus:floweventbus:0.0.3'
        implementation 'com.github.like5188.FlowEventBus:floweventbus_annotations:0.0.3'
        kapt 'com.github.like5188.FlowEventBus:floweventbus_compiler:0.0.3'
        implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    }
```

2、在Application中进行一次初始化。
```java
    WXPayUtils.init(context, appid)
```

3、支付
```java
    WXPayUtils.pay(context, payParams)
```

4、接收返回结果
```java
    在任意一个类中注册
        FlowEventBus.register(this)
```
        然后用下面三个方法接收支付宝返回的结果
```java
    // 支付成功
    @BusObserver([WxPayUtils.TAG_PAY_SUCCESS])
    fun onPaySuccess() {
        Toast.makeText(this, "微信支付成功", Toast.LENGTH_SHORT).show()
    }
```
```java
    // 支付失败
    @BusObserver([WxPayUtils.TAG_PAY_FAILURE])
    fun onPayFailure() {
        Toast.makeText(this, "微信支付失败", Toast.LENGTH_SHORT).show()
    }
```