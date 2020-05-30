package it.polito.ai.lab2;
import it.polito.ai.lab2.dtos.TeamDTO;
import it.polito.ai.lab2.entities.Token;
import it.polito.ai.lab2.entities.User;
import it.polito.ai.lab2.repositories.TokenRepository;
import it.polito.ai.lab2.repositories.UserRepository;
import it.polito.ai.lab2.services.NotificationService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@SpringBootApplication
public class Lab2Application {

    @Autowired
    UserRepository users;

    PasswordEncoder passwordEncoder = new PasswordEncoder() {
        @Override
        public String encode(CharSequence charSequence) {
            return charSequence.toString();
        }

        @Override
        public boolean matches(CharSequence charSequence, String s) {
            return charSequence.toString().equals(s);
        }
    };

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
    CommandLineRunner runner () {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {

                if(!users.findByUsername("user").isPresent())
                    users.save(User.builder()
                            .username("user")
                            .password(passwordEncoder.encode("password"))
                            .roles(Arrays.asList( "ROLE_USER"))
                            .build()
                    ); else System.out.println("Utente user già esistente");
                if(!users.findByUsername("admin").isPresent())
                    users.save(User.builder()
                            .username("admin")
                            .password(passwordEncoder.encode("password"))
                            .roles(Arrays.asList("ROLE_USER", "ROLE_ADMIN"))
                            .build()
                    ); else System.out.println("Utente admin già esistente");

                System.out.println(("printing all users..."));
                users.findAll().forEach(v -> System.out.println(" User :" + v.toString()));
            }
        };
    }

    public static void main(String[] args) { SpringApplication.run(Lab2Application.class, args); }

}
