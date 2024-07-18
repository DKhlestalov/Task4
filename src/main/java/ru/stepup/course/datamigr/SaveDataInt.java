package ru.stepup.course.datamigr;

import java.util.List;
// интерфейс для сохранения мигрированных данных
public interface SaveDataInt<T>{
    void save(List<T> data, ErrorSaverInt errSaver);
}
