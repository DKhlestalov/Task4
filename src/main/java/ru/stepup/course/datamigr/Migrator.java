package ru.stepup.course.datamigr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
// класс для проведения миграции данных
@Component
public class Migrator <T> {
    @Autowired
    ReadDataInt<T> dataReader;
    @Autowired
    SaveDataInt<T> dataWriter;
    @Autowired
    List<TransformDataInt> transforms;
    @Autowired
    ErrorSaverInt errSaver;

    public void migrate(){
        List<T> data = dataReader.read(errSaver);
        for(TransformDataInt t: transforms){
            t.transform(data, errSaver);
        }

        dataWriter.save(data, errSaver);
    }
}
