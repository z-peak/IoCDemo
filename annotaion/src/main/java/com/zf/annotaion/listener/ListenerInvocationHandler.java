package com.zf.annotaion.listener;

import android.app.Activity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 拦截Activity中的某些方法
 */
public class ListenerInvocationHandler implements InvocationHandler {

    private Object target;

    private Map<String,Method> methods = new HashMap<>();

    public ListenerInvocationHandler(Object target) {
        this.target = target;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (target!=null){
            String methodName = method.getName();
            //如果method方法在Map中，表明是需要拦截的方法
            method=  methods.get(methodName);
            if (method!=null){
                return method.invoke(target,args);
            }
        }
        return null;
    }

    /**
     * 添加需要拦截的方法
     * @param methodName
     * @param method
     */
    public void addMethod(String methodName,Method method){
        methods.put(methodName,method);
    }
}
