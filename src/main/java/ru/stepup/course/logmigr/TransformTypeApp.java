package ru.stepup.course.logmigr;

import org.springframework.stereotype.Component;
import ru.stepup.course.datamigr.TransformDataInt;
import ru.stepup.course.datamigr.ErrorSaverInt;

import java.util.List;
@Component
@LogTransformation
public class TransformTypeApp implements TransformDataInt<LogData> {
    @Override
    public void transform(List<LogData> lst, ErrorSaverInt errSaver) {
        for (LogData ld : lst) {
            String typeApp = ld.getTypeApp();
            if (typeApp == null || (!typeApp.equals("web") & !typeApp.equals("mobile"))) {
                ld.setTypeApp("other");
            } else ld.setTypeApp(typeApp);
        }
    }
}
