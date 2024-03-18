package com.example.authenticationserivce.util;

import com.example.authenticationserivce.custom_exceptions.DoctorDoesNotExistException;
import com.example.authenticationserivce.custom_exceptions.UserAlreadyExistsException;
import com.example.authenticationserivce.database.DAO;
import com.example.authenticationserivce.enums.UserType;
import com.example.authenticationserivce.model.ChildRegistrationData;
import com.example.authenticationserivce.model.DoctorRegistrationData;
import com.example.authenticationserivce.model.ParentRegistrationData;
import com.example.authenticationserivce.model.SharedRegistrationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.Doc;

@Service
public class UserRegistrationHelper {
    private final DAO dao;

    @Autowired
    public UserRegistrationHelper(DAO dao) {
        this.dao = dao;
    }

    public void registerChild(ChildRegistrationData childRegistrationData) throws UserAlreadyExistsException {
        // registration in auth tables
        String password = childRegistrationData.getPassword();
        String salt = SaltGenerator.generateSalt();
        String hashedPassword = PasswordHashingUtil.hashPassword(password, salt);
        dao.registerUserAuthTables(childRegistrationData.getEmail(), hashedPassword, salt, UserType.CHILD);

        // registration in child profile
        dao.registerChildProfile(childRegistrationData.getEmail(), childRegistrationData.getUsername(), childRegistrationData.getParentEmail(), childRegistrationData.getAge());
    }

    public void registerParent(ParentRegistrationData parentData) throws UserAlreadyExistsException {
        // registration in auth tables
        String password = parentData.getPassword();
        String salt = SaltGenerator.generateSalt();
        String hashedPassword = PasswordHashingUtil.hashPassword(password, salt);
        dao.registerUserAuthTables(parentData.getEmail(), hashedPassword, salt, UserType.PARENT);

        // registration in child profile
        dao.registerParentProfile(parentData.getEmail(), parentData.getUsername());
    }

    public void registerDoctor(DoctorRegistrationData doctorRegistrationData)  throws UserAlreadyExistsException {
        // registration in auth tables
        String password = doctorRegistrationData.getPassword();
        String salt = SaltGenerator.generateSalt();
        String hashedPassword = PasswordHashingUtil.hashPassword(password, salt);
        dao.registerUserAuthTables(doctorRegistrationData.getEmail(), hashedPassword, salt, UserType.DOCTOR);

        // registration in child profile
        dao.registerDoctorProfile(doctorRegistrationData.getEmail(), doctorRegistrationData.getUsername());
    }

    public boolean verifyUserInfo(SharedRegistrationData userData) {
        String expectedHashedPassword = dao.getHashedPasswordByEmail(userData.getEmail(), userData.getUserType());
        if (expectedHashedPassword == null || expectedHashedPassword.isBlank()) {
            return false;
        }

        String saltValue = dao.getSaltByEmail(userData.getEmail(), userData.getUserType());
        String actualHashedPassword = PasswordHashingUtil.hashPassword(userData.getPassword(), saltValue);
        try {
            return actualHashedPassword.equals(expectedHashedPassword);
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public void confirmDoctorMentorship(String childEmail, String doctorEmail) throws DoctorDoesNotExistException {
        dao.linkChildWithDoctor(childEmail, doctorEmail);
    }
}
