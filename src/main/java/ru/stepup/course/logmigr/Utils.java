package ru.stepup.course.logmigr;

import java.lang.reflect.Proxy;

public class Utils {
    public static <T> T logTransformation(T objectIncome, String path){
        return (T) Proxy.newProxyInstance(
                objectIncome.getClass().getClassLoader(),
                objectIncome.getClass().getInterfaces(),
                new MakeTransformLog(objectIncome, path)
        );
    }

}
