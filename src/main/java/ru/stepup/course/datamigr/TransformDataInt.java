package ru.stepup.course.datamigr;

import java.util.List;
// интерфейс для преобразования данных
public interface TransformDataInt<T>{
    void transform(List<T> lst, ErrorSaverInt errSaver);
}
