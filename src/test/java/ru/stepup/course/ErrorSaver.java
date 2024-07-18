package ru.stepup.course;

import lombok.Getter;
import ru.stepup.course.datamigr.ErrorSaverInt;
// Для получения ошибок вместо сохранения в лог
public class ErrorSaver implements ErrorSaverInt {
    String error = "";
    @Override
    public void saveError(String error) {
       this.error = error;
    }
    public void clearError(){error = "";}
}
