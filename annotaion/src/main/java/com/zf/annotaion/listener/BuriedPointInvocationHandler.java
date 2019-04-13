package com.zf.annotaion.listener;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class BuriedPointInvocationHandler implements InvocationHandler {
    private Object target;

    public BuriedPointInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.e("===============", "proxy before");
        Object result = method.invoke(target, args);
        Log.e("==============", "proxy after");
        return result;
    }
}
