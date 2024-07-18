package ru.stepup.course.logmigr;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.stepup.course.datamigr.ErrorSaverInt;

import java.io.FileWriter;
import java.io.IOException;
// класс для сохранения ошибок в файл лога
@Component
@ConfigurationProperties(prefix = "log-error")
@Setter
@Getter
public class LogError implements ErrorSaverInt {
    private String path;
    @Override
    public void saveError(String error) {
        try {
            FileWriter writer = new FileWriter(System.getProperty("user.dir") + path, true);
            writer.write(error+"\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Не удалось вывести сообщение в лог ошибок");
        }

    }
}
