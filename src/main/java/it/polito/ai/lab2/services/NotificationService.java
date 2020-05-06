package it.polito.ai.lab2.services;

public interface NotificationService {

    void sendMessage(String address, String subject, String body);
}
