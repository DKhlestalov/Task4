package ru.stepup.course.logmigr;

import org.springframework.stereotype.Component;
import ru.stepup.course.datamigr.TransformDataInt;
import ru.stepup.course.datamigr.ErrorSaverInt;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Component
@LogTransformation
public class TransformFio implements TransformDataInt<LogData> {
    @Override
    public void transform(List <LogData>lst, ErrorSaverInt errSaver) {
        Pattern patt = Pattern.compile("(^|\\s)([а-я])");
        for(LogData l : lst){
            Matcher match = patt.matcher(l.getFio());
            boolean find = match.find();

            if (find) {
                StringBuffer result = new StringBuffer();
                String letter;
                do {
                    letter = match.group(1)+match.group(2).toUpperCase();
                    match.appendReplacement(result, letter);
                    find = match.find();
                } while (find);
                match.appendTail(result);
                l.setFio(result.toString());
            }
        }
    }
}
