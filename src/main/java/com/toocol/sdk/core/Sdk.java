package com.toocol.sdk.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ZhaoZhe (joezane.cn@gmail.com)
 * @date 2022/4/13 14:08
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sdk {
    /**
     * 映射Http请求 /{sdk-name}/{operation} 中的 sdk-name
     *
     * @return sdk name
     */
    String name();
}
