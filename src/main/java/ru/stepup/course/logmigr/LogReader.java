package ru.stepup.course.logmigr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.stepup.course.datamigr.ErrorSaverInt;
import ru.stepup.course.datamigr.ReadDataInt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
// класс для считывания данных из лог-фалов входов пользователей
@Component
@ConfigurationProperties(prefix = "reader")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class LogReader implements ReadDataInt<LogData> {

    private String inputCatalog;

    private static String getStrByPatt(String pattStr, String inStr){
        Pattern patt = Pattern.compile(pattStr);
        Matcher match = patt.matcher(inStr);
        if(match.find()){
            return match.group(1);
        }
        return "";
    }
    private static LogData parse (String input) {
        String login;
        String fio;
        String strDate;
        Date date;
        String typeApp;

        if (input == null) return null;

        login = getStrByPatt("^([A-z]\\w+)", input);
        fio = getStrByPatt("^[A-z]\\w+\\<([А-я ]+)\\>:", input);
        strDate = getStrByPatt("^[A-z]\\w+\\<[А-я ]+\\>:\\s?(\\d{2}\\.\\d{2}\\.\\d{4})", input);
        typeApp = getStrByPatt("^[A-z]\\w+" + "\\<[А-я ]+\\>:\\s?\\d{2}\\.\\d{2}\\.\\d{4}\\s+(\\w+)$", input);

        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd.MM.yyyy");
        try {
            date = format.parse(strDate);
        } catch (Exception e) {date = null;}

        return new LogData(null, null, login, fio, date, typeApp);
    }
    @Override
    public List<LogData> read(ErrorSaverInt errSaver) {
        List<LogData> retList = new ArrayList<>();
        File folder = new File(System.getProperty("user.dir") + inputCatalog);
        String line;
        Integer lineNum = 0;
        for (File file : folder.listFiles()) {
            if (!file.isFile()) continue;
            FileReader fr;
            try {
                fr = new FileReader(file, Charset.forName("utf-8"));
            } catch (Exception ex){
                errSaver.saveError("Ошибка при открытии файла " + file.getName()+ ": " + ex.getMessage());
                continue;
            }
            BufferedReader reader = new BufferedReader(fr);
            try {
                line = reader.readLine();
                if (line == null | !line.equals("LOGV2")) continue; // проверяем формат
                lineNum = 2;
                do {
                    line = reader.readLine();
                    if (line != null) {
                        LogData ld = parse(line);
                        ld.setFileName(file.getName());
                        ld.setLine(lineNum);
                        if (ld != null) retList.add(ld);
                    }
                    lineNum++;
                }
                while (line != null);
            }catch (Exception ex){
                errSaver.saveError("Ошибка при считывании данных из файла " + file.getName() + ": "+ex.getMessage());
            }
        }
        return retList;
    }

}
