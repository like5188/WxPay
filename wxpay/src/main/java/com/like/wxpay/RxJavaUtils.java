package com.like.wxpay;

import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Schedulers.immediate(): 直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler。
 * Schedulers.newThread(): 总是启用新线程，并在新线程执行操作。
 * Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。行为模式和 newThread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率。不要把计算工作放在 io() 中，可以避免创建不必要的线程。
 * Schedulers.computation(): 计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算，即不会被 I/O 等操作限制性能的操作，例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。不要把 I/O 操作放在 computation() 中，否则 I/O 操作的等待时间会浪费 CPU。
 * 另外， Android 还有一个专用的 AndroidSchedulers.mainThread()，它指定的操作将在 Android 主线程运行。
 */
public class RxJavaUtils {

    /**
     * 延时执行某任务
     *
     * @param interval 延时，单位毫秒
     * @param consumer 回调，在UI线程执行
     */
    public static void timer(long interval, Consumer<Long> consumer) {
        Observable.timer(interval, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    /**
     * 周期性执行某任务（默认在computation线程）
     *
     * @param interval 延时，单位毫秒
     * @param consumer 回调，在UI线程执行
     */
    public static void interval(long interval, Consumer<Long> consumer) {
        Observable.interval(interval, TimeUnit.MILLISECONDS)// 隔一段时间产生一个数字，然后就结束，可以理解为延迟产生数字
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    /**
     * 延迟一段时间，然后以固定周期循环执行某一任务
     *
     * @param onSubscribe  固定周期执行的任务，在IO线程
     * @param consumer     任务返回的结果
     * @param initialDelay 延迟一段时间，毫秒
     * @param period       周期，毫秒
     * @param <T>
     */
    public static <T> void polling(final OnSubscribe<T> onSubscribe, final Consumer<T> consumer, final long initialDelay, final long period) {
        Observable.create(
                (ObservableOnSubscribe<T>) observableEmitter ->
                        Schedulers.newThread().createWorker()
                                .schedulePeriodically(() ->
                                                observableEmitter.onNext(onSubscribe.onSubscribeCall0()),
                                        initialDelay,
                                        period,
                                        TimeUnit.MILLISECONDS
                                )
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    /**
     * 遍历数组
     *
     * @param list
     * @param consumer
     * @param <T>
     */
    public static <T> void list(T[] list, Consumer<T> consumer) {
        Observable.fromArray(list).subscribe(consumer);
    }

    /**
     * 遍历Iterable接口迭代器
     *
     * @param list
     * @param consumer
     * @param <T>
     */
    public static <T> void list(Iterable<? extends T> list, Consumer<T> consumer) {
        Observable.fromIterable(list).subscribe(consumer);
    }

    /**
     * 指定线程执行，MAIN线程展示
     *
     * @param onSubscribe
     * @param <T>
     */
    public static <T> void runAndUpdate(final OnSubscribe<T> onSubscribe, Scheduler scheduler) {
        Observable.create(
                (ObservableOnSubscribe<T>) observableEmitter -> {
                    observableEmitter.onNext(onSubscribe.onSubscribeCall0());
                    observableEmitter.onComplete();
                }
        )
                .subscribeOn(scheduler) // 指定 subscribe() 发生在 scheduler 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(
                        onSubscribe::onNextCall,
                        throwable -> {
                            throwable.printStackTrace();
                            onSubscribe.onErrorCall(throwable);
                        }
                );

    }

    /**
     * computation线程执行，MAIN线程展示
     *
     * @param onSubscribe
     * @param <T>
     */
    public static <T> void runComputationAndUpdate(final OnSubscribe<T> onSubscribe) {
        runAndUpdate(onSubscribe, Schedulers.computation());
    }

    /**
     * IO线程执行，MAIN线程展示
     *
     * @param onSubscribe
     * @param <T>
     */
    public static <T> void runIoAndUpdate(final OnSubscribe<T> onSubscribe) {
        runAndUpdate(onSubscribe, Schedulers.io());
    }

    /**
     * 防抖动搜索（300毫秒限制）（当N个结点发生的时间太靠近（即发生的时间差小于设定的值T），debounce就会自动过滤掉前N-1个结点。）
     *
     * @param searchEditText
     * @param onSubscribe
     * @param <T>            执行onSubscribe.onSubscribeCall1()进行搜索后返回的数据类型
     */
    public static <T> void addTextChangedListener(TextView searchEditText, final OnSubscribe<T> onSubscribe) {
        RxTextView.textChanges(searchEditText)
                .debounce(300, TimeUnit.MILLISECONDS, Schedulers.io())
                //对用户输入的关键字进行过滤
                .filter(charSequence -> charSequence.toString().trim().length() > 0)
                // switchMap替代flatMap，有新的数据发来的时候，之前的就会取消掉。
                .switchMap(new Function<CharSequence, ObservableSource<T>>() {
                    @Override
                    public Observable<T> apply(CharSequence charSequence) {
                        return onSubscribe.onSubscribeCall1(charSequence);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSubscribe::onNextCall,
                        throwable -> {
                            throwable.printStackTrace();
                            onSubscribe.onErrorCall(throwable);
                        }
                );

    }

    /**
     * 防抖动按钮点击（指定时间间隔内只触发一次点击事件）
     *
     * @param interval      指定时间间隔(毫秒)
     * @param view
     * @param clickListener
     */
    public static void addOnClickListener(long interval, View view, final View.OnClickListener clickListener) {
        RxView.clicks(view)
                .throttleFirst(interval, TimeUnit.MILLISECONDS)
                .subscribe(object -> clickListener.onClick(view));
    }


    /**
     * 订阅回调
     *
     * @param <T>
     */
    public static class OnSubscribe<T> {
        /**
         * 执行任务
         *
         * @return
         */
        protected T onSubscribeCall0() {
            return null;
        }

        /**
         * 执行任务，用于搜索
         *
         * @param searchStr
         * @return
         */
        protected Observable<T> onSubscribeCall1(CharSequence searchStr) {
            return null;
        }

        /**
         * 更新UI（MAIN线程）
         *
         * @param t
         */
        protected void onNextCall(T t) {

        }

        /**
         * 错误时回调（MAIN线程）
         *
         * @param throwable
         */
        protected void onErrorCall(Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
