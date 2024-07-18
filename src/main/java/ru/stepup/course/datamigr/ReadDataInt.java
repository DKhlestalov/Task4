package ru.stepup.course.datamigr;

import java.util.List;
// интерфейс для считывания входящих данных
public interface ReadDataInt<T> {
   List<T> read(ErrorSaverInt errSaver);
}
