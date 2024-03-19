package com.example.authenticationserivce.controller;

import com.example.authenticationserivce.util.JwtTokenUtil;
import com.example.authenticationserivce.util.StringOperations;
import com.example.authenticationserivce.util.UserInfoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> getParent(@RequestHeader("Authorization") String token) {
        token = StringOperations.removeBearerIfExist(token);
        token = StringOperations.removeQuotesIfExist(token);
        if (!JwtTokenUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        String childEmail = JwtTokenUtil.getEmailFromToken(token);
        return ResponseEntity.ok().body(userInfoHelper.getChildParentEmail(childEmail));
    }

    @GetMapping("/child/doctor")
    public ResponseEntity<String> getDoctor(@RequestHeader("Authorization") String token) {
        token = StringOperations.removeBearerIfExist(token);
        token = StringOperations.removeQuotesIfExist(token);
        if (!JwtTokenUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        String childEmail = JwtTokenUtil.getEmailFromToken(token);
        return ResponseEntity.ok().body(userInfoHelper.getChildDoctorEmail(childEmail));
    }

    @GetMapping("/username")
    public ResponseEntity<String> getUsername(@RequestHeader("Authorization") String token) {
        token = StringOperations.removeBearerIfExist(token);
        token = StringOperations.removeQuotesIfExist(token);
        if (!JwtTokenUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        String username = JwtTokenUtil.getUsernameFromToken(token);
        return ResponseEntity.ok().body(username);
    }

    @GetMapping("/parent/children")
    public ResponseEntity<?> fetchParentChildrenEmails(@RequestHeader("Authorization") String token) {
        token = StringOperations.removeBearerIfExist(token);
        token = StringOperations.removeQuotesIfExist(token);
        if (!JwtTokenUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        return ResponseEntity.ok().body(userInfoHelper.getParentChildEmails(token));
    }

    @GetMapping("/doctor/children")
    public ResponseEntity<?> fetchDoctorChildrenEmails(@RequestHeader("Authorization") String token) {
        token = StringOperations.removeBearerIfExist(token);
        token = StringOperations.removeQuotesIfExist(token);
        if (!JwtTokenUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        return ResponseEntity.ok().body(userInfoHelper.getDoctorChildEmails(token));
    }
}
