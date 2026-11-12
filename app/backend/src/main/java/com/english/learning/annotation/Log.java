package com.english.learning.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    String value() default "";

    OperationType type() default OperationType.OTHER;

    boolean saveParams() default true;

    enum OperationType {
        LOGIN("登录"),
        LOGOUT("登出"),
        CREATE("新增"),
        UPDATE("修改"),
        DELETE("删除"),
        QUERY("查询"),
        IMPORT("导入"),
        EXPORT("导出"),
        OTHER("其他");

        private final String description;

        OperationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return this.description;
        }
    }
}
