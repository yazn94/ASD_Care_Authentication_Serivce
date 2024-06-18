package com.example.authenticationserivce.controller;

import com.example.authenticationserivce.custom_annotations.ValidJwtToken;
import com.example.authenticationserivce.enums.UserType;
import com.example.authenticationserivce.model.EmailRequest;
import com.example.authenticationserivce.util.JwtTokenUtil;
import com.example.authenticationserivce.util.UserInfoHelper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth/get")
public class InquiriesController {
    private final UserInfoHelper userInfoHelper;

    @Autowired
    public InquiriesController(UserInfoHelper userInfoHelper) {
        this.userInfoHelper = userInfoHelper;
    }

    @GetMapping("/child/parent")
    @ValidJwtToken
    public ResponseEntity<String> getParent(@RequestHeader("Authorization") String token) {
        String childEmail = JwtTokenUtil.getEmailFromToken(token);
        return ResponseEntity.ok().body(userInfoHelper.getChildParentEmail(childEmail));
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

    @GetMapping("/parent/child/age")
    @ValidJwtToken
    public ResponseEntity<?> fetchParentChildAge(@RequestHeader("Authorization") String token, @Valid @RequestBody EmailRequest childEmail) {
        String parentEmail = JwtTokenUtil.getEmailFromToken(token);
        return ResponseEntity.ok().body(userInfoHelper.getChildAgeForParent(parentEmail, childEmail.getEmail()));
    }

    @GetMapping("/doctor/child/age")
    @ValidJwtToken
    public ResponseEntity<?> fetchDoctorChildAge(@RequestHeader("Authorization") String token, @Valid @RequestBody EmailRequest childEmail) {
        String doctorEmail = JwtTokenUtil.getEmailFromToken(token);
        return ResponseEntity.ok().body(userInfoHelper.getChildAgeForDoctor(doctorEmail, childEmail.getEmail()));
    }
}
