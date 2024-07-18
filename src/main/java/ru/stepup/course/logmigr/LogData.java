package ru.stepup.course.logmigr;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
// класс для хранения данных о входах пользователей
@Setter
@Getter
@AllArgsConstructor
public class LogData {
    private String fileName;
    private Integer line;

    private String login;
    private String fio;
    private Date date;
    private String typeApp;

    @Override
    public String toString() {
        return "LogData{" +
                "fileName='" + fileName + '\'' +
                ", line=" + line +
                ", login='" + login + '\'' +
                ", fio='" + fio + '\'' +
                ", date=" + date +
                ", typeApp='" + typeApp + '\'' +
                '}';
    }
}
