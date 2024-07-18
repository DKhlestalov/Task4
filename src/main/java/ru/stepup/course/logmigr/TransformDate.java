package ru.stepup.course.logmigr;

import org.springframework.stereotype.Component;
import ru.stepup.course.datamigr.TransformDataInt;
import ru.stepup.course.datamigr.ErrorSaverInt;

import java.util.List;

@Component
@LogTransformation
public class TransformDate implements TransformDataInt<LogData> {
    @Override
    public void transform(List<LogData> lst, ErrorSaverInt errSaver) {
        for (int i = 0; i < lst.size(); i++){
            if(lst.get(i).getDate() == null){
                errSaver.saveError("Не указана дата, файл "+lst.get(i).getFileName()+" строка "+lst.get(i).getLine());
                lst.remove(i--);
            }
        }
    }
}
