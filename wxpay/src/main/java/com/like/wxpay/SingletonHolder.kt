package com.like.wxpay

import kotlin.jvm.functions.FunctionN

/**
 * 为R这个类创建单例模式。
 * 一般用于构造函数中包含参数的情况，因为object创建的单例不能包含构造函数。
 * 使用的时候让伴生对象继承此类即可。
 */
/*
class SingletonHolderTest private constructor(val name: String, val age: Int) {
    companion object : SingletonHolder<SingletonHolderTest>(object : FunctionN<SingletonHolderTest> {
        override val arity: Int = 2 // number of arguments that must be passed to constructor

        override fun invoke(vararg args: Any?): SingletonHolderTest {
            return SingletonHolderTest(args[0] as String, args[1] as Int)
        }
    })
}
 */
open class SingletonHolder<out R>(initializer: FunctionN<R>) {
    private var initializer: FunctionN<R>? = initializer
    @Volatile
    private var instance: R? = null

    fun getInstance(vararg args: Any?): R {
        val instance1 = instance
        if (instance1 != null) {
            return instance1
        }

        return synchronized(this) {
            val instance2 = instance
            if (instance2 != null) {
                instance2
            } else {
                val created = initializer!!.invoke(*args)
                instance = created
                initializer = null
                created
            }
        }
    }
}