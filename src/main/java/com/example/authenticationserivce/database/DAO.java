package com.example.authenticationserivce.database;

import com.example.authenticationserivce.custom_exceptions.DoctorDoesNotExistException;
import com.example.authenticationserivce.custom_exceptions.UserAlreadyExistsException;
import com.example.authenticationserivce.enums.UserType;
import com.example.authenticationserivce.model.ContactDTO;
import com.example.authenticationserivce.model.MentorChildrenEmailAndNames;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
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
    private final String BASE_PROGRESS = "{\n" +
            "    \"parentFeedback\": [],\n" +
            "    \"machineLearningFeedback\": \"\",\n" +
            "    \"gameSummaries\": [],\n" +
            "    \"gamesPlayed\": 0\n" +
            "}";
    private final String DOC_PARENT_USERNAME_FIELD = "username";
    private final String CHILD_USERNAME_FIELD = "firstName";

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

    public void registerChildProfile(String email, String firstName, String lastName, String parentEmail, LocalDate date) throws UserAlreadyExistsException {
        // Check if the child email already exists
        String query = "SELECT COUNT(*) FROM " + CHILD_PRO + " WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(query, new Object[]{email}, Integer.class);
        if (count != null && count > 0) {
            throw new UserAlreadyExistsException("Child with email " + email + " already exists.");
        }

        // Insert the child profile into the child_profile table
        String insertQuery = "INSERT INTO " + CHILD_PRO + " (email, firstName, lastName, parentEmail, dateOfBirth, registerDate, currentPoints, pointsSystemAvailability) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(insertQuery, email, firstName, lastName, parentEmail, date,
                    (LocalDate.now()), 0, false);
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
        String insertQuery = "INSERT INTO " + PARENT_PRO + " (email, username, registerDate) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(insertQuery, email, username, LocalDate.now());
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
        String insertQuery = "INSERT INTO " + DOCTOR_PRO + " (email, username, registerDate) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(insertQuery, email, username, LocalDate.now());
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
        String usernameField = getUserName(userType);
        String query = "SELECT " + usernameField + " FROM " + tableName + " WHERE email = ?";

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

    public LocalDate getChildBirthDate(String childEmail) {
        String query = "SELECT dateOfBirth FROM " + CHILD_PRO + " WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{childEmail}, LocalDate.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int getChildAgeForParent(String parentEmail, String childEmail) {
        // Check if the child email belongs to the given parent
        String parentQuery = "SELECT parentEmail FROM " + CHILD_PRO + " WHERE email = ?";
        String actualParentEmail = jdbcTemplate.queryForObject(parentQuery, new Object[]{childEmail}, String.class);
        if (!parentEmail.equals(actualParentEmail)) {
            throw new IllegalArgumentException("The given child email does not belong to the provided parent email.");
        }

        // Calculate the age
        return Period.between(getChildBirthDate(childEmail), LocalDate.now()).getYears();
    }

    public int getChildAgeForDoctor(String childEmail, String doctorEmail) {
        // Check if the child email belongs to the given doctor
        String doctorQuery = "SELECT doctorEmail FROM " + CHILD_PRO + " WHERE email = ?";
        String actualDoctorEmail = jdbcTemplate.queryForObject(doctorQuery, new Object[]{childEmail}, String.class);
        if (!doctorEmail.equals(actualDoctorEmail)) {
            throw new IllegalArgumentException("The given child email does not belong to the provided doctor email.");
        }

        // Calculate the age
        return Period.between(getChildBirthDate(childEmail), LocalDate.now()).getYears();
    }

    public ArrayList<MentorChildrenEmailAndNames> getParentChildrenEmailsAndNames(String parentEmail) {
        String query = "SELECT email, firstName, lastName FROM " + CHILD_PRO + " WHERE parentEmail = ?";
        try {
            return (ArrayList<MentorChildrenEmailAndNames>) jdbcTemplate.query(query, new Object[]{parentEmail}, (rs, rowNum) -> {
                MentorChildrenEmailAndNames parentChildrenEmailAndNames = new MentorChildrenEmailAndNames();
                parentChildrenEmailAndNames.setEmail(rs.getString("email"));
                parentChildrenEmailAndNames.setFirstName(rs.getString("firstName"));
                parentChildrenEmailAndNames.setLastName(rs.getString("lastName"));
                return parentChildrenEmailAndNames;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public ArrayList<MentorChildrenEmailAndNames> getDoctorChildrenEmailsAndNames(String doctorEmail) {
        String query = "SELECT email, firstName, lastName FROM " + CHILD_PRO + " WHERE doctorEmail = ?";
        try {
            return (ArrayList<MentorChildrenEmailAndNames>) jdbcTemplate.query(query, new Object[]{doctorEmail}, (rs, rowNum) -> {
                MentorChildrenEmailAndNames parentChildrenEmailAndNames = new MentorChildrenEmailAndNames();
                parentChildrenEmailAndNames.setEmail(rs.getString("email"));
                parentChildrenEmailAndNames.setFirstName(rs.getString("firstName"));
                parentChildrenEmailAndNames.setLastName(rs.getString("lastName"));
                return parentChildrenEmailAndNames;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public ArrayList<ContactDTO> getParentContacts(String parentEmail) {
        String query = "SELECT doctorEmail FROM " + CHILD_PRO + " WHERE parentEmail = ?";
        try {
            ArrayList<String> doctorsEmails = (ArrayList<String>) jdbcTemplate.queryForList(query, new Object[]{parentEmail}, String.class);
            ArrayList<ContactDTO> doctors = new ArrayList<>();

            for (String doctorEmail : doctorsEmails) {
                ContactDTO dto = new ContactDTO();
                dto.setEmail(doctorEmail);
                query = "SELECT username FROM " + DOCTOR_PRO + " WHERE email = ?";
                dto.setUsername(jdbcTemplate.queryForObject(query, new Object[]{doctorEmail}, String.class));
                doctors.add(dto);
            }
            return doctors;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public ArrayList<ContactDTO> getDoctorContacts(String doctorEmail) {
        String query = "SELECT parentEmail FROM " + CHILD_PRO + " WHERE doctorEmail = ?";
        try {
            ArrayList<String> parentsEmails = (ArrayList<String>) jdbcTemplate.queryForList(query, new Object[]{doctorEmail}, String.class);
            ArrayList<ContactDTO> parents = new ArrayList<>();

            for (String parentEmail : parentsEmails) {
                ContactDTO dto = new ContactDTO();
                dto.setEmail(parentEmail);
                query = "SELECT username FROM " + PARENT_PRO + " WHERE email = ?";
                dto.setUsername(jdbcTemplate.queryForObject(query, new Object[]{parentEmail}, String.class));
                parents.add(dto);
            }
            return parents;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    private String getUserName(UserType userType) {
        switch (userType) {
            case PARENT:
            case DOCTOR:
                return DOC_PARENT_USERNAME_FIELD;
            default:
                return CHILD_USERNAME_FIELD;
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
