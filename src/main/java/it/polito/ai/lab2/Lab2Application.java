package it.polito.ai.lab2;

import it.polito.ai.lab2.dtos.CourseDTO;
import it.polito.ai.lab2.dtos.StudentDTO;
import it.polito.ai.lab2.entities.Course;
import it.polito.ai.lab2.entities.Student;
import it.polito.ai.lab2.repositories.CourseRepository;
import it.polito.ai.lab2.repositories.StudentRepository;
import it.polito.ai.lab2.services.NotificationService;
import it.polito.ai.lab2.services.NotificationServiceImpl;
import it.polito.ai.lab2.services.TeamService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@SpringBootApplication
public class Lab2Application {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("my.gmail@gmail.com");
        mailSender.setPassword("password");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    CommandLineRunner runner (NotificationService notificationService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {

                notificationService.sendMessage("kribos@hotmail.it", "aaa", "bbb");

            }
        };
    }

    public static void main(String[] args) { SpringApplication.run(Lab2Application.class, args); }

}
