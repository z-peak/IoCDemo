package com.zf.annotaion.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)//作用在注解之上
@Retention(RetentionPolicy.RUNTIME)
public @interface EventBase {

    //1、setOnxxxListener()
    String listenerSetter();

    //2、监听的对象
    Class<?> listenerType();

    //3、回调方法 onClick(View view)
    String callBackFunction();
}
