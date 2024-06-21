package com.example.authenticationserivce.util;

import com.example.authenticationserivce.database.DAO;
import com.example.authenticationserivce.enums.UserType;
import com.example.authenticationserivce.model.DoctorData;
import com.example.authenticationserivce.model.MentorChildrenEmailAndNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
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

    public ArrayList<MentorChildrenEmailAndNames> getParentChildrenEmailsAndNames(String token) {
        String parentEmail = JwtTokenUtil.getEmailFromToken(token);
        return dao.getParentChildrenEmailsAndNames(parentEmail);
    }

    public boolean hasDoctor(String childEmail) {
        return dao.hasDoctor(childEmail);
    }

    public ArrayList<MentorChildrenEmailAndNames> getDoctorChildrenEmailsAndNames(String token) {
        String parentEmail = JwtTokenUtil.getEmailFromToken(token);
        return dao.getDoctorChildrenEmailsAndNames(parentEmail);
    }

    public ArrayList<String> getDoctorChildEmails(String doctorEmail) {
        return dao.getDoctorChildEmails(doctorEmail);
    }

    public int getChildAge(String token) {
        String childEmail = JwtTokenUtil.getEmailFromToken(token);
        LocalDate date = dao.getChildBirthDate(childEmail);
        LocalDate now = LocalDate.now();
        return Period.between(date, now).getYears();
    }

    public int getChildAgeForParent(String parentEmail, String childEmail) {
        return dao.getChildAgeForParent(parentEmail, childEmail);
    }

    public int getChildAgeForDoctor(String doctorEmail, String childEmail) {
        return dao.getChildAgeForDoctor(doctorEmail, childEmail);
    }

    public ArrayList<DoctorData> getAllRegisteredDoctors() {
        return dao.getAllDoctors();
    }
}
