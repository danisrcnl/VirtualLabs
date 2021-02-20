package it.polito.ai.lab2;
import it.polito.ai.lab2.dtos.*;
import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.repositories.*;
import it.polito.ai.lab2.services.AssignmentService;
import it.polito.ai.lab2.services.NotificationService;
import it.polito.ai.lab2.services.TeamService;
import it.polito.ai.lab2.services.VmService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@SpringBootApplication
public class Lab2Application {

    @Autowired
    PasswordEncoder encoder;

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("ideagraphicdesign.lecce@gmail.com");
        mailSender.setPassword("grezewvfqmufaotk");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    CommandLineRunner runner (VmModelRepository vmModelRepository, CourseRepository courseRepository, VmService vmService, TeamService teamService,
                              AssignmentService assignmentService, TeacherRepository teacherRepository,TeamRepository teamRepository, StudentRepository studentRepository, UserRepository userRepository, NotificationService notificationService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {




            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(Lab2Application.class, args);
    }

}
