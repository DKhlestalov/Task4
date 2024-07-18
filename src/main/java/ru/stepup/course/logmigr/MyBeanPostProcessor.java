package ru.stepup.course.logmigr;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class <?> beanClass = bean.getClass();
        if(beanClass.isAnnotationPresent(LogTransformation.class)){
            LogTransformation ann = (LogTransformation)beanClass.getAnnotation(LogTransformation.class);
            return Utils.logTransformation(bean, ann.logFileName());
        }
        return bean;
    }
}
