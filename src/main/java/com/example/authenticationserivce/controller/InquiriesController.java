package com.example.authenticationserivce.controller;

import com.example.authenticationserivce.custom_annotations.ValidJwtToken;
import com.example.authenticationserivce.util.JwtTokenUtil;
import com.example.authenticationserivce.util.UserInfoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
