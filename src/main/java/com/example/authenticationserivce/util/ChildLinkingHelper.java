package com.example.authenticationserivce.util;

import com.example.authenticationserivce.custom_exceptions.EmailSendingException;
import com.example.authenticationserivce.database.DAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChildLinkingHelper {
    private final EmailSenderService emailSenderService;
    private final DAO dao;

    private final String PARENT_CONFIRMATION_EMAIL_SUBJECT =
            "Confirmation of Parent-Child Relationship";

    @Autowired
    public ChildLinkingHelper(EmailSenderService emailSenderService, DAO dao) {
        this.emailSenderService = emailSenderService;
        this.dao = dao;
    }

    public void composeParentConfirmationEmail(String childEmail, String parentEmail, String pin)
            throws EmailSendingException {
        String subject = "Confirmation of Parent-Child Relationship";

        // Compose email body using StringBuilder
        String bodyBuilder = "Dear parent,\n\n" +
                "Your child (" + childEmail + ") has provided your email as their parent's email address. " +
                "If this is correct, please enter the following PIN into your child's device: (" +
                pin + ")\n\n" +
                "If this is not correct, please disregard this email.\n\n" +
                "Sincerely,\n" +
                "ASD Care";

        // Send email to parent
        emailSenderService.sendEmail(parentEmail, subject, bodyBuilder);
    }

    public void composeDoctorConfirmationEmail(String childEmail, String doctorEmail, String pin)
            throws EmailSendingException {
        String subject = "Confirmation of Child Mentorship";
        String parentEmail = dao.getParentEmail(childEmail);
        
        // Compose email body using StringBuilder
        String bodyBuilder = "Dear Doctor,\n\n" +
                "The child with email address " + childEmail + " has listed you as their mentor. " +
                "If you are available to provide mentorship, kindly send the following PIN (" + pin + ") to the child's parent at their email address: " + parentEmail + "\n\n" +
                "If you are unable to mentor the child at this time, please disregard this message.\n\n" +
                "Thank you for your attention.\n\n" +
                "Sincerely,\n" +
                "ASD Care";

        // Send email to parent
        emailSenderService.sendEmail(doctorEmail, subject, bodyBuilder);
    }
}
