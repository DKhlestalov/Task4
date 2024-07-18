package ru.stepup.course.logmigr;

import lombok.AllArgsConstructor;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
// прокси для журналирования данных до и после вызова методов трансфрмации
@AllArgsConstructor
public class MakeTransformLog implements InvocationHandler {
    private Object bean;
    private String fileName;

    private void outParams(String head, String body){
        try{
            // по хорошему "\\src\\main\\resources\\out\\" надо вынести в файл .properties, чтоб подменить в тестах
            FileWriter writer = new FileWriter(System.getProperty("user.dir") + "\\src\\main\\resources\\out\\" + fileName, true);
            if(head != null){
                writer.write(head+":\n");
            }
            if(body != null){
                writer.write(body+"\n");
            }
            writer.close();
        } catch (IOException ex){
            System.out.println("Ошибка при журналировании параметров трансформации: " + ex.getMessage());
        }
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        Date date = new Date();
        outParams(date+" "+bean.getClass().getName()+" "+method.getName(), "\tbefore: " + Arrays.toString(args));
        Object val = method.invoke(bean, args);
        outParams(null, "\tafter: " + Arrays.toString(args));
        return val;
    }
}
