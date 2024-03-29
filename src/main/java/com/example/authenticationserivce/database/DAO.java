package com.example.authenticationserivce.database;

import com.example.authenticationserivce.custom_exceptions.DoctorDoesNotExistException;
import com.example.authenticationserivce.custom_exceptions.UserAlreadyExistsException;
import com.example.authenticationserivce.enums.UserType;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;

@Service
@Configuration
public class DAO {
    private final JdbcTemplate jdbcTemplate;
    private final String DOCTOR_AUTH = "doctor_auth";
    private final String PARENT_AUTH = "parent_auth";
    private final String CHILD_AUTH = "child_auth";
    private final String CHILD_PRO = "child_profile";
    private final String PARENT_PRO = "parent_profile";
    private final String DOCTOR_PRO = "doctor_profile";

    @Autowired
    public DAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void registerUserAuthTables(@NotNull String email, @NotNull String hashedPassword, @NotNull String salt,
                                       @NotNull UserType userType) throws UserAlreadyExistsException {
        String tableName = getAuthTableName(userType);

        // Check if the user already exists
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(query, new Object[]{email}, Integer.class);
        if (count != null && count > 0) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists.");
        }

        // Insert the user into the appropriate table
        String insertQuery = "INSERT INTO " + tableName + " (email, hashedPassword, salt) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertQuery, email, hashedPassword, salt);
    }

    public String getHashedPasswordByEmail(String email, UserType userType) {
        String tableName = getAuthTableName(userType);
        String query = "SELECT hashedPassword FROM " + tableName + " WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{email}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String getSaltByEmail(String email, UserType userType) {
        String tableName = getAuthTableName(userType);
        String query = "SELECT salt FROM " + tableName + " WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{email}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void registerChildProfile(String email, String username, String parentEmail, int age) throws UserAlreadyExistsException {
        // Check if the child email already exists
        String query = "SELECT COUNT(*) FROM " + CHILD_PRO + " WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(query, new Object[]{email}, Integer.class);
        if (count != null && count > 0) {
            throw new UserAlreadyExistsException("Child with email " + email + " already exists.");
        }

        // Insert the child profile into the child_profile table
        String insertQuery = "INSERT INTO " + CHILD_PRO + " (email, username, parentEmail, age, registerDay, currentPoints, pointsSystemAvailability, progress) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(insertQuery, email, username, parentEmail, age,
                    new Timestamp(System.currentTimeMillis()), 0, false, "{}");
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
        }
    }

    public void registerParentProfile(String email, String username) throws UserAlreadyExistsException {
        // Check if the parent email already exists
        String query = "SELECT COUNT(*) FROM " + PARENT_PRO + " WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(query, new Object[]{email}, Integer.class);
        if (count != null && count > 0) {
            throw new UserAlreadyExistsException("Parent with email " + email + " already exists.");
        }

        // Insert the parent profile into the parent_profile table
        String insertQuery = "INSERT INTO " + PARENT_PRO + " (email, username, registerDay) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(insertQuery, email, username, new Timestamp(System.currentTimeMillis()));
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
        }
    }

    public void registerDoctorProfile(String email, String username) throws UserAlreadyExistsException {
        // Check if the doctor email already exists
        String query = "SELECT COUNT(*) FROM " + DOCTOR_PRO + " WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(query, new Object[]{email}, Integer.class);
        if (count != null && count > 0) {
            throw new UserAlreadyExistsException("Doctor with email " + email + " already exists.");
        }

        // Insert the doctor profile into the doctor_profile table
        String insertQuery = "INSERT INTO " + DOCTOR_PRO + " (email, username, registerDay) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(insertQuery, email, username, new Timestamp(System.currentTimeMillis()));
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
        }
    }

    public String getParentEmail(String childEmail) {
        String query = "SELECT parentEmail FROM " + CHILD_PRO + " WHERE email = ?";
        System.out.println("child email is: " + childEmail);
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{childEmail}, String.class);
        } catch (EmptyResultDataAccessException e) {
            // If no parent email found for the given child email, return null
            return null;
        }
    }

    public String getDoctorEmail(String childEmail) {
        String query = "SELECT doctorEmail FROM " + CHILD_PRO + " WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{childEmail}, String.class);
        } catch (EmptyResultDataAccessException e) {
            // If no doctor email found for the given child email, return null
            return null;
        }
    }

    public void linkChildWithDoctor(String childEmail, String doctorEmail) throws DoctorDoesNotExistException {
        // Check if the doctor email exists in the database
        String doctorQuery = "SELECT COUNT(*) FROM " + DOCTOR_AUTH + " WHERE email = ?";
        Integer doctorCount = jdbcTemplate.queryForObject(doctorQuery, new Object[]{doctorEmail}, Integer.class);
        if (doctorCount != null && doctorCount == 0) {
            // If the doctor email doesn't exist, you can choose to handle it accordingly, such as throwing an exception
            throw new DoctorDoesNotExistException("Doctor with email " + doctorEmail + " does not exist, and can't be linked with the child.");
        }

        // Update the child's doctor email in the child_profile table
        String updateQuery = "UPDATE " + CHILD_PRO + " SET doctorEmail = ? WHERE email = ?";
        jdbcTemplate.update(updateQuery, doctorEmail, childEmail);
    }

    public String getUsername(String email, UserType userType) {
        String tableName = getProfileTableName(userType);
        String query = "SELECT username FROM " + tableName + " WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{email}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    public ArrayList<String> getParentChildEmails(String parentEmail) {
        String query = "SELECT email FROM " + CHILD_PRO + " WHERE parentEmail = ?";
        try {
            return (ArrayList<String>) jdbcTemplate.queryForList(query, new Object[]{parentEmail}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public ArrayList<String> getDoctorChildEmails(String doctorEmail) {
        String query = "SELECT email FROM " + CHILD_PRO + " WHERE doctorEmail = ?";
        try {
            return (ArrayList<String>) jdbcTemplate.queryForList(query, new Object[]{doctorEmail}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private String getAuthTableName(UserType userType) {
        switch (userType) {
            case CHILD:
                return CHILD_AUTH;
            case PARENT:
                return PARENT_AUTH;
            case DOCTOR:
                return DOCTOR_AUTH;
            default:
                throw new IllegalArgumentException("Invalid user type: " + userType);
        }
    }

    private String getProfileTableName(UserType userType) {
        switch (userType) {
            case CHILD:
                return CHILD_PRO;
            case PARENT:
                return PARENT_PRO;
            case DOCTOR:
                return DOCTOR_PRO;
            default:
                throw new IllegalArgumentException("Invalid user type: " + userType);
        }
    }
}
