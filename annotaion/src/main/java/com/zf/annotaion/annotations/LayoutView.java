package com.zf.annotaion.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)//该注解作用在...上
@Retention(RetentionPolicy.RUNTIME)//运行时通过反射获取注解的值
public @interface LayoutView {
    int value();
}
