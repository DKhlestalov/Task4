package ru.stepup.course.logmigr;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.stepup.course.datamigr.ErrorSaverInt;
import ru.stepup.course.datamigr.SaveDataInt;
import ru.stepup.course.model.Logins;
import ru.stepup.course.model.Users;
import ru.stepup.course.repo.LoginsRepository;
import ru.stepup.course.repo.UsersRepository;

import java.util.List;
// сохранение в базу данных по пользователям и входам
@Component
@Setter
public class DataBaseSaver implements SaveDataInt<LogData> {
    @Autowired
    private UsersRepository usersRep;
    @Autowired
    private LoginsRepository loginsRep;

    @Override
    public void save(List<LogData> data, ErrorSaverInt errSaver) {
        for (LogData ld : data) {
            String login = ld.getLogin();
            if(usersRep.existsUsersByUsername(login)){
                Long userId = usersRep.getIdByUsername(login);
                Users user = usersRep.getReferenceById(userId);
                Logins l = loginsRep.saveAndFlush(new Logins(null,ld.getDate(),user,ld.getTypeApp()));
            }else {
                Users user = usersRep.saveAndFlush(new Users(null,ld.getLogin(),ld.getFio()));
                Logins l = loginsRep.saveAndFlush(new Logins(null,ld.getDate(),user,ld.getTypeApp()));
            }
        }
    }
}
