package com.amaya.module;

import java.lang.annotation.*;

/**
 * @Author: Guyuemang
 * 2025/4/21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {
    String name();
    Category category();
}