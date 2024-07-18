package ru.stepup.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import ru.stepup.course.datamigr.Migrator;

@SpringBootApplication(scanBasePackages = "ru.stepup.course")
@EnableConfigurationProperties
public class Main {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Main.class,args);

        Migrator migrator = ctx.getBean("migrator", Migrator.class);
        migrator.migrate();

        System.out.println("ok");
    }
}