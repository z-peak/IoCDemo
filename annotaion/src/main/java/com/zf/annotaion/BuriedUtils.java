package com.zf.annotaion;

import android.view.View;

import com.zf.annotaion.annotations.BuriedPoint;
import com.zf.annotaion.listener.BuriedPointInvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

public class BuriedUtils {

    public static void injectBuried(Object o) {
        Class<?> clazz = o.getClass();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            BuriedPoint annotation = method.getAnnotation(BuriedPoint.class);
            if (annotation != null) {
                BuriedPointInvocationHandler handler = new BuriedPointInvocationHandler(o);
                Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), handler);
                method.setAccessible(true);
            }
        }
    }

    public static void injectClickListener(View.OnClickListener onClickListener) {
        if (onClickListener==null)return;
        BuriedPointInvocationHandler handler = new BuriedPointInvocationHandler(onClickListener);
        onClickListener = (View.OnClickListener) Proxy.newProxyInstance(onClickListener.getClass().getClassLoader(), onClickListener.getClass().getInterfaces(), handler);
//        onClickListener.onClick();
    }
}
