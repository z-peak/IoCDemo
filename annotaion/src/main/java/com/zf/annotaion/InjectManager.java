package com.zf.annotaion;

import android.app.Activity;
import android.view.View;

import com.zf.annotaion.annotations.BuriedPoint;
import com.zf.annotaion.annotations.EventBase;
import com.zf.annotaion.annotations.LayoutView;
import com.zf.annotaion.annotations.BindView;
import com.zf.annotaion.listener.BuriedPointInvocationHandler;
import com.zf.annotaion.listener.ListenerInvocationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Objects;

public class InjectManager {
    public static void inject(Activity activity){
        bindLayout(activity);
        bindViews(activity);
        bindEvent(activity);
    }

    /**
     * 给Activity注入布局
     * @param activity
     */
    private static void bindLayout(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        //获取注解
        LayoutView layoutView = clazz.getAnnotation(LayoutView.class);
        if (layoutView!=null){
            int layoutId = layoutView.value();
            try {
                Method method = clazz.getMethod("setContentView", int.class);
                method.invoke(activity,layoutId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 给Activity布局的View赋值
     * @param activity
     */
    private static void bindViews(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        //获取类中的所有属性
        Field[] fields = clazz.getDeclaredFields();
        for (Field field: fields) {
            //获取属性的注解
            BindView bindView = field.getAnnotation(BindView.class);
            if (bindView!=null){//含有InjectView注解的属性进行下面操作
                //获取view id
                int viewId = bindView.value();
                try {
                    Method method = clazz.getMethod("findViewById", int.class);
                    //获得view
                    Object view = method.invoke(activity, viewId);
                    //取消Java访问权限检查
                    field.setAccessible(true);
                    //对Activity中的属性进行复制
                    field.set(activity,view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 事件注入
     * @param activity
     */
    private static void bindEvent(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        //获取当前类的所有方法
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            //获取方法的所有注解（可能不止一个）
            Annotation[] annotations = method.getAnnotations();
            //遍历所有的注解
            for (Annotation annotation:annotations) {//OnClick注解
                if (annotation==null)return;
                //获取注解类型
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (annotationType!=null){
                    //获取OnClick注解上的EventBase注解
                    EventBase eventBase= annotationType.getAnnotation(EventBase.class);
                    if (eventBase==null)return;
                    String callBackListener = eventBase.callBackListener();//onClick
                    String listenerSetter = eventBase.listenerSetter();//setOnClickListener
                    Class<?> listenerType = eventBase.listenerType();// View.OnClickListener.class

                    try {
                        Method declaredMethod = annotationType.getDeclaredMethod("value");
                        int[] viewIds = (int[]) declaredMethod.invoke(annotation);

                        ListenerInvocationHandler listenerInvocationHandler = new ListenerInvocationHandler(activity);
                        listenerInvocationHandler.addMethod(callBackListener,method);
                        Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, listenerInvocationHandler);

                        for (int id:viewIds) {
                            //获取当前View
                           View view= activity.findViewById(id);
                           //得到View的setOnClickListener方法
                           Method onClickListenerMethod = view.getClass().getMethod(listenerSetter,listenerType);
                           //执行setOnClickListener方法
                             onClickListenerMethod.invoke(view,listener);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }


}
