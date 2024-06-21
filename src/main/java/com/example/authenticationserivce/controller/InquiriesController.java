package com.example.authenticationserivce.controller;

import com.example.authenticationserivce.custom_annotations.ValidJwtToken;
import com.example.authenticationserivce.enums.UserType;
import com.example.authenticationserivce.model.EmailDTO;
import com.example.authenticationserivce.model.EmailRequest;
import com.example.authenticationserivce.model.GetUsernameRequestInput;
import com.example.authenticationserivce.util.JwtTokenUtil;
import com.example.authenticationserivce.util.StringOperations;
import com.example.authenticationserivce.util.UserContactsHelper;
import com.example.authenticationserivce.util.UserInfoHelper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth/get")
public class InquiriesController {
    private final UserInfoHelper userInfoHelper;
    private final UserContactsHelper userContactsHelper;

    @Autowired
    public InquiriesController(UserInfoHelper userInfoHelper, UserContactsHelper userContactsHelper) {
        this.userInfoHelper = userInfoHelper;
        this.userContactsHelper = userContactsHelper;
    }


    @GetMapping("/child/parent")
    @ValidJwtToken
    public ResponseEntity<String> getParent(@RequestHeader("Authorization") String token) {
        String childEmail = JwtTokenUtil.getEmailFromToken(token);
        return ResponseEntity.ok().body(userInfoHelper.getChildParentEmail(childEmail));
    }

    // getting the parent email for doctor
    @PostMapping("/child/parent/email/for/doctor")
    @ValidJwtToken
    public ResponseEntity<EmailDTO> getParentForDoctor(@RequestHeader("Authorization") String token, @Valid @RequestBody EmailRequest childEmail) {
        return ResponseEntity.ok().body(
                new EmailDTO(
                        userInfoHelper.getChildParentEmail(childEmail.getEmail())
                )
        );
    }

    // check if the child has a doctor assigned
    @PostMapping("/child/has/doctor")
    @ValidJwtToken
    public ResponseEntity<Boolean> hasDoctor(@RequestHeader("Authorization") String token, @Valid @RequestBody EmailRequest childEmail) {
        return ResponseEntity.ok().body(userInfoHelper.hasDoctor(childEmail.getEmail()));
    }

    // getting the doctor email for parent
    @PostMapping("/child/doctor/email/for/parent")
    @ValidJwtToken
    public ResponseEntity<EmailDTO> getDoctorForChild(@RequestHeader("Authorization") String token, @Valid @RequestBody EmailRequest childEmail) {
        System.out.println(userInfoHelper.getChildDoctorEmail(childEmail.getEmail()));
        return ResponseEntity.ok().body(
                new EmailDTO(
                        userInfoHelper.getChildDoctorEmail(childEmail.getEmail())
                )
        );
    }

    @GetMapping("/child/doctor")
    @ValidJwtToken
    public ResponseEntity<String> getDoctor(@RequestHeader("Authorization") String token) {
        String childEmail = JwtTokenUtil.getEmailFromToken(token);
        return ResponseEntity.ok().body(userInfoHelper.getChildDoctorEmail(childEmail));
    }

    // In case of child user, first name is returned as the username
    @GetMapping("/username")
    @ValidJwtToken
    public ResponseEntity<String> getUsername(@RequestHeader("Authorization") String token) {
        String username = JwtTokenUtil.getUsernameFromToken(token);
        return ResponseEntity.ok().body(username);
    }

    @GetMapping("/parent/children")
    @ValidJwtToken
    public ResponseEntity<?> fetchParentChildrenEmails(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(userInfoHelper.getParentChildEmails(token));
    }

    @GetMapping("/mentor/children/emails/and/names")
    @ValidJwtToken
    public ResponseEntity<?> fetchMentorChildrenEmailsAndNames(@RequestHeader("Authorization") String token) {
        UserType userType = JwtTokenUtil.getUserTypeFromToken(token);
        if (userType == UserType.CHILD) {
            return ResponseEntity.badRequest().body("This endpoint is only for parent and doctor users");
        } else if (userType == UserType.DOCTOR) {
            return ResponseEntity.ok().body(userInfoHelper.getDoctorChildrenEmailsAndNames(token));
        } else {
            return ResponseEntity.ok().body(userInfoHelper.getParentChildrenEmailsAndNames(token));
        }
    }

    @GetMapping("/doctor/children")
    @ValidJwtToken
    public ResponseEntity<?> fetchDoctorChildrenEmails(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(userInfoHelper.getDoctorChildEmails(token));
    }

    @GetMapping("/child/age")
    @ValidJwtToken
    public ResponseEntity<?> fetchChildAge(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(userInfoHelper.getChildAge(token));
    }

    @PostMapping("/parent/child/age")
    @ValidJwtToken
    public ResponseEntity<?> fetchParentChildAge(@RequestHeader("Authorization") String token, @Valid @RequestBody EmailRequest childEmail) {
        String parentEmail = JwtTokenUtil.getEmailFromToken(token);
        return ResponseEntity.ok().body(userInfoHelper.getChildAgeForParent(parentEmail, childEmail.getEmail()));
    }

    @PostMapping("/doctor/child/age")
    @ValidJwtToken
    public ResponseEntity<?> fetchDoctorChildAge(@RequestHeader("Authorization") String token, @Valid @RequestBody EmailRequest childEmail) {
        String doctorEmail = JwtTokenUtil.getEmailFromToken(token);
        return ResponseEntity.ok().body(userInfoHelper.getChildAgeForDoctor(doctorEmail, childEmail.getEmail()));
    }

    @GetMapping("/chat/user/contacts/{email}")
    public ResponseEntity<String> getContacts(@RequestHeader("Authorization") String token, @PathVariable String email) {
        token = StringOperations.removeBearerIfExist(token);
        token = StringOperations.removeQuotesIfExist(token);

        if (!JwtTokenUtil.validateToken(token) || !JwtTokenUtil.getEmailFromToken(token).equals(email)) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
        if (JwtTokenUtil.getUserTypeFromToken(token) == UserType.DOCTOR) {
            return ResponseEntity.ok().body(userContactsHelper.getDoctorContacts(email));
        } else if (JwtTokenUtil.getUserTypeFromToken(token) == UserType.PARENT) {
            return ResponseEntity.ok().body(userContactsHelper.getParentContacts(email));
        } else {
            return ResponseEntity.badRequest().body("Child users are not allowed to chat");
        }
    }


    @GetMapping("/user/type")
    @ValidJwtToken
    public ResponseEntity<UserType> getUserType(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(JwtTokenUtil.getUserTypeFromToken(token));
    }

    @GetMapping("/user/email")
    @ValidJwtToken
    public ResponseEntity<String> getUserEmail(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(JwtTokenUtil.getEmailFromToken(token));
    }

    // Any user with authenticated token can get the username of another user by providing the email and user type
    @PostMapping("/username/by/email/and/usertype")
    @ValidJwtToken
    public ResponseEntity<String> getUsernameByEmail(@RequestHeader("Authorization") String token, @Valid @RequestBody GetUsernameRequestInput getUsernameRequestInput) {
        return ResponseEntity.ok().body(userInfoHelper.getUsername(
                getUsernameRequestInput.getEmail(),
                getUsernameRequestInput.getUserType())
        );
    }

    // Any user with authenticated token can preview the list of all registered doctors in the system
    @GetMapping("/all/doctors/data")
    @ValidJwtToken
    public ResponseEntity<?> getDoctors(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(userInfoHelper.getAllRegisteredDoctors());
    }


}
