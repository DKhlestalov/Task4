package ru.stepup.course;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.stepup.course.datamigr.Migrator;
import ru.stepup.course.logmigr.*;
import ru.stepup.course.model.Users;
import ru.stepup.course.repo.LoginsRepository;
import ru.stepup.course.repo.UsersRepository;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
@Testcontainers
public class TestTask {
    @Autowired
    private UsersRepository userRep;
    @Autowired
    private LoginsRepository loginRep;
    @Autowired
    Migrator migrator;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("test")
            .withUsername("postgres")
            .withPassword("12345");

    @BeforeAll
    static void beforeAll() {
        System.out.println("pg started!!!!!!!");
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        System.out.println("pg stopped!!!!!!!");
        postgres.stop();
    }

    @BeforeEach
    void beforeEach() {
        loginRep.deleteAll();
        userRep.deleteAll();
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry reg) {
        reg.add("spring.datasource.url",postgres::getJdbcUrl);
                //() -> String.format("jdbc:postgresql://localhost:%d/test", 5432));
        reg.add("spring.datasource.username", () -> "postgres");
        reg.add("spring.datasource.password", () -> "12345");
        reg.add("reader.input-catalog",()->"\\src\\test\\resources\\in");
        reg.add("log-error.path",()->"\\src\\test\\resources\\out\\errors.log");
    }

    @Test
    @DisplayName("Проверка на чтение из файла")
    public void test10() {
        String logFilePath = "\\src\\test\\resources\\in";
        LogReader reader = new LogReader(logFilePath);
        ErrorSaver err = new ErrorSaver();
        Assertions.assertDoesNotThrow(() -> reader.read(err), "Ошибка при чтении файла с данными из " + logFilePath);
        Assertions.assertEquals(err.error, "", "Ошибка при чтении данных из " + logFilePath + ": " + err.error);

        List<LogData> lst = reader.read(null);
        Assertions.assertEquals(lst.size(), 8, "Ошибка: Считывается некорректное кол-во элементов файла");
        for (LogData logData : lst) {
            String login = logData.getLogin();
            Assertions.assertNotNull(login, "Считался пустой логин пользователя");
        }

    }

    //@Test
    @DisplayName("Проверка правильности чтения ФИО")
    public void test11() {
        String logFilePath = "\\src\\test\\resources\\in";
        LogReader reader = new LogReader(logFilePath);
        ErrorSaver err = new ErrorSaver();
        List<LogData> lst = reader.read(null);
        Pattern patt = Pattern.compile("\"^[A-z]\\\\w+\\\\<[А-я ]+\\\\>:\"");
        for (LogData logData : lst) {
            String fio = logData.getFio();
            Assertions.assertNotNull(fio, "Не считывается ФИО");
            Matcher match = patt.matcher(fio);
            Assertions.assertTrue(match.find(), "Неверно считывается ФИО: "+fio);
        }
    }

    @Test
    @DisplayName("Проверка работы компоненты на трансформацию ФИО")
    public void test20() {
        String logFilePath = "\\src\\test\\resources\\in";
        LogReader reader = new LogReader(logFilePath);
        ErrorSaver err = new ErrorSaver();
        List<LogData> lst = reader.read(null);
        TransformFio transformer = new TransformFio();
        transformer.transform(lst, err);
        Assertions.assertEquals(err.error, "", "Ошибка при трансформации ФИО: " + err.error);
        Pattern patt = Pattern.compile("(^|\\s)([а-я])");
        for (LogData logData : lst) {
            String fio = logData.getFio();
            Matcher match = patt.matcher(fio);
            Assertions.assertFalse(match.find(), "Ошибка. в фамилии " + fio + " есть слово с маленькой буквы");
        }
    }

    @Test
    @DisplayName("Проверка работы компоненты на трансформацию <приложения>")
    public void test30() {
        String logFilePath = "\\src\\test\\resources\\in";
        LogReader reader = new LogReader(logFilePath);
        ErrorSaver err = new ErrorSaver();
        List<LogData> lst = reader.read(null);
        List<String> typesAppOld = new ArrayList<>();
        for (LogData logData : lst) {
            typesAppOld.add(logData.getTypeApp());
        }
        TransformTypeApp transformer = new TransformTypeApp();
        transformer.transform(lst, err);
        Assertions.assertEquals(err.error, "", "Ошибка при трансформации типа приложения: " + err.error);
        for (int i = 0; i < typesAppOld.size(); i++) {
            String typeOld = typesAppOld.get(i);
            String typeNew = lst.get(i).getTypeApp();
            if (typeOld.equals("web") | typeOld.equals("mobile")) {
                Assertions.assertEquals(typeNew, typeOld, "Ошибка трансформации типа приложения: " + typeOld + "->" + typeNew);
            } else
                Assertions.assertEquals(typeNew, "other", "Ошибка трансформации типа приложения: " + typeOld + "->" + typeNew);
        }
    }
    @Test
    @DisplayName("Проверка работы компоненты на трансформацию даты")
    public void test40() {
        String logFilePath = "\\src\\test\\resources\\in";
        LogReader reader = new LogReader(logFilePath);
        ErrorSaver err = new ErrorSaver();
        List<LogData> lst = reader.read(null);
        TransformDate transformer = new TransformDate();
        transformer.transform(lst, err);
        Assertions.assertNotNull(err.error, "Ошибка при трансформации даты: нет ошибки при отсутствии даты");
        for (int i = 0; i < lst.size(); i++) {
            Date date = lst.get(i).getDate();
            Assertions.assertNotNull(date, "Ошибка, компонента трансформации даты не убирает записи с пустой датой");
        }
    }

    @Test
    @DisplayName("Проверка на запись данных в базу")
    public void test50() {
        String logFilePath = "\\src\\test\\resources\\in";
        LogReader reader = new LogReader(logFilePath);
        DataBaseSaver dataSaver = new DataBaseSaver();
        dataSaver.setLoginsRep(loginRep);
        dataSaver.setUsersRep(userRep);
        ErrorSaver err = new ErrorSaver();
        List<LogData> lst = reader.read(null);

        Assertions.assertEquals(lst.size(), 8, "Ошибка: Считывается некорректное кол-во элементов файла");

        TransformFio transformFio = new TransformFio();
        TransformDate transformDate = new TransformDate();
        TransformTypeApp transformTypeApp = new TransformTypeApp();

        transformFio.transform(lst, err);
        transformDate.transform(lst, err);
        transformTypeApp.transform(lst, err);

        Assertions.assertNotNull(userRep, "Ошибка внедрения репозитория Users");
        Assertions.assertNotNull(loginRep, "Ошибка внедрения репозитория Logins");
        err.clearError();
        dataSaver.save(lst, err);
        Assertions.assertEquals(err.error, "", "Ошибка при записи данных в базу: " + err.error);
        // проверим наличие всех данных в базе
        Map<String, Integer> loginCounts = new HashMap<>();
        for(LogData logData : lst ){
            String login = logData.getLogin();
            Long idUser = userRep.getIdByUsername(login);
            Assertions.assertTrue(idUser > 0, "Ошибка: в базе нет данных пользователя "+login);
            if(!loginCounts.containsKey(login)) loginCounts.put(login, 0);
            loginCounts.put(login, loginCounts.get(login)+1);
        }

        for(String login:loginCounts.keySet()){
            Long userId = userRep.getIdByUsername(login);
            Users user = userRep.getReferenceById(userId);
            Integer countDB = loginRep.getLoginsCountByUser(user);
            Assertions.assertEquals(loginCounts.get(login), countDB, "Неверное кол-во логинов в базе по пользователю "+login+": должно быть "+loginCounts.get(login));
        }
    }


    @Test
    @DisplayName("Проверка работы всех компонент")
    public void test60() throws IOException {

        migrator.migrate();

        // проверим наличие в базе всех данных из файлов
        String logFilePath = "\\src\\test\\resources\\in";
        LogReader reader = new LogReader(logFilePath);
        List<LogData> lst = reader.read(null);
        ErrorSaver err = new ErrorSaver();
        TransformFio transformFio = new TransformFio();
        TransformDate transformDate = new TransformDate();
        TransformTypeApp transformTypeApp = new TransformTypeApp();
        transformFio.transform(lst, err);
        transformDate.transform(lst, err);
        transformTypeApp.transform(lst, err);

        Map<String, Integer> loginCounts = new HashMap<>();
        for(LogData logData : lst ){
            String login = logData.getLogin();
            Long idUser = userRep.getIdByUsername(login);
            Assertions.assertNotNull(idUser, "Ошибка: в базе нет данных пользователя "+login);
            if(!loginCounts.containsKey(login)) loginCounts.put(login, 0);
            loginCounts.put(login, loginCounts.get(login)+1);
        }

        for(String login:loginCounts.keySet()){
            Long userId = userRep.getIdByUsername(login);
            Users user = userRep.getReferenceById(userId);
            Integer countDB = loginRep.getLoginsCountByUser(user);
            Assertions.assertEquals(loginCounts.get(login), countDB, "Неверное кол-во логинов в базе по пользователю "+login+": должно быть "+loginCounts.get(login));
        }

    }

}
