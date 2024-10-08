package com.sky.annocation;


import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识某个方法需要进行功能字段填充处理
 */
@Target(ElementType.METHOD) //这个注解放在method上
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {

    OperationType value();
}
