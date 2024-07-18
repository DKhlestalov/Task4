package ru.stepup.course.logmigr;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
// аннотация для журналирования преобразований
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface LogTransformation {
    String logFileName() default "LogTransformation.log";
}
