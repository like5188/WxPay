package com.like.wxpay;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Toast提示工具类
 */
public class ToastUtils {
    private static Toast toast;// 弹出一个新的toast会代替之前的toast

    // 不允许直接构造此类，也不允许反射构造此类
    private ToastUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 显示短时间的提示(居中显示)
     *
     * @param context
     * @param obj
     */
    public static void showShortCenter(Context context, Object obj) {
        show(context, obj, Toast.LENGTH_SHORT, Gravity.CENTER, 0);
    }

    /**
     * 显示长时间的提示(居中显示)
     *
     * @param context
     * @param obj
     */
    public static void showLongCenter(Context context, Object obj) {
        show(context, obj, Toast.LENGTH_LONG, Gravity.CENTER, 0);
    }

    /**
     * 显示短时间的提示(底部显示)
     *
     * @param context
     * @param obj
     */
    public static void showShort(Context context, Object obj) {
        show(context, obj, Toast.LENGTH_SHORT, Gravity.BOTTOM, 0);
    }

    /**
     * 显示长时间的提示(底部显示)
     *
     * @param context
     * @param obj
     */
    public static void showLong(Context context, Object obj) {
        show(context, obj, Toast.LENGTH_LONG, Gravity.BOTTOM, 0);
    }

    /**
     * 显示自定义时长、位置、图片的提示
     *
     * @param context
     * @param obj       toast显示的内容
     * @param duration  toast显示的时长，毫秒
     * @param gravity   toast的位置
     * @param iconResId 图片资源id，如果不需要图片，就设置为<=0
     */
    public static void show(Context context, Object obj, int duration, int gravity, int iconResId) {
        if (context == null) {
            return;
        }
        String content = obj == null ? "null" : obj.toString();
        Observable.timer(0, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())// 隔一段时间产生一个数字，然后就结束，可以理解为延迟产生数字
                .subscribe(aLong -> {
                    if (toast == null) {
                        toast = Toast.makeText(context.getApplicationContext(), content, duration);
                    } else {
                        toast.setText(content);
                        toast.setDuration(duration);
                    }
                    toast.setGravity(gravity, 0, 0);
                    if (iconResId > 0) {
                        LinearLayout toastLayout = (LinearLayout) toast.getView();
                        ImageView imageView = new ImageView(context);
                        imageView.setImageResource(iconResId);
                        toastLayout.addView(imageView, 0);// 0 图片在文字的上方 ， 1 图片在文字的下方
                    }
                    toast.show();
                });
    }

}
