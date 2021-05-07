package it.polito.ai.lab2;
import it.polito.ai.lab2.repositories.*;
import it.polito.ai.lab2.services.assignment.AssignmentService;
import it.polito.ai.lab2.services.notification.NotificationService;
import it.polito.ai.lab2.services.team.TeamService;
import it.polito.ai.lab2.services.vm.VmService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
                              AssignmentService assignmentService, TeamRepository teamRepository, StudentRepository studentRepository, UserRepository userRepository, NotificationService notificationService) {
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
