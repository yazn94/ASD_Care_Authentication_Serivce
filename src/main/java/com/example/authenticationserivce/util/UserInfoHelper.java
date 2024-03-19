package com.example.authenticationserivce.util;

import com.example.authenticationserivce.database.DAO;
import com.example.authenticationserivce.enums.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserInfoHelper {
    private final DAO dao;

    @Autowired
    public UserInfoHelper(DAO dao) {
        this.dao = dao;
    }

    public String getChildParentEmail(String childEmail) {
        return dao.getParentEmail(childEmail);
    }

    public String getChildDoctorEmail(String childEmail) {
        return dao.getDoctorEmail(childEmail);
    }

    public String getUsername(String email, UserType userType) {
        return dao.getUsername(email, userType);
    }

    public ArrayList<String> getParentChildEmails(String token) {
        String parentEmail = JwtTokenUtil.getEmailFromToken(token);
        return dao.getParentChildEmails(parentEmail);
    }

    public ArrayList<String> getDoctorChildEmails(String doctorEmail) {
        return dao.getDoctorChildEmails(doctorEmail);
    }
}
