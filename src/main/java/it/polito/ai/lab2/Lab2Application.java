package it.polito.ai.lab2;
import it.polito.ai.lab2.entities.Token;
import it.polito.ai.lab2.repositories.TokenRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.sql.Timestamp;
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
    CommandLineRunner runner (TokenRepository tokenRepository) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {


            }
        };
    }

    public static void main(String[] args) { SpringApplication.run(Lab2Application.class, args); }

}
