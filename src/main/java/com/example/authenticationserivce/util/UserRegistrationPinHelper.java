package com.example.authenticationserivce.util;

import com.example.authenticationserivce.custom_exceptions.EmailSendingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationPinHelper {
    private final EmailSenderService emailSenderService;

    @Autowired
    public UserRegistrationPinHelper(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    public void sendLoginPin(String userEmail, String pin) throws EmailSendingException {
        String subject = "Login PIN Code";

        // Compose email body
        String body = "Dear user,\n\n" +
                "Your login PIN code is: (" + pin + ")\n\n" +
                "Please use this PIN to log in to our application.\n\n" +
                "Sincerely,\n" +
                "ASD Care Team";

        // Send email to the user
        emailSenderService.sendEmail(userEmail, subject, body);
    }
}
