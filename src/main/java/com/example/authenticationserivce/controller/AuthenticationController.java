package com.example.authenticationserivce.controller;

import com.example.authenticationserivce.custom_exceptions.DoctorDoesNotExistException;
import com.example.authenticationserivce.custom_exceptions.EmailSendingException;
import com.example.authenticationserivce.custom_exceptions.UserAlreadyExistsException;
import com.example.authenticationserivce.enums.UserType;
import com.example.authenticationserivce.model.*;
import com.example.authenticationserivce.util.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final ChildLinkingHelper childLinkingHelper;
    private final UserRegistrationPinHelper userRegistrationPinHelper;
    private final UserRegistrationHelper userRegistrationHelper;
    private final UserInfoHelper userInfoHelper;

    @Autowired
    public AuthenticationController(ChildLinkingHelper childLinkingHelper,
                                    UserRegistrationPinHelper userRegistrationPinHelper,
                                    UserRegistrationHelper userRegistrationHelper,
                                    UserInfoHelper userInfoHelper) {

        this.childLinkingHelper = childLinkingHelper;
        this.userRegistrationPinHelper = userRegistrationPinHelper;
        this.userRegistrationHelper = userRegistrationHelper;
        this.userInfoHelper = userInfoHelper;
    }

    @PostMapping("verify/user/email/send-pin")
    public ResponseEntity<String> sendUserPin(@Valid @RequestBody EmailRequest email, BindingResult bindingResult) throws EmailSendingException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + bindingResult.getAllErrors());
        }
        String pin = PinGenerator.generatePin();
        userRegistrationPinHelper.sendLoginPin(email.getEmail(), pin);
        return ResponseEntity.ok().body(pin);
    }

    @PostMapping("link/child/parent/send-pin")
    public ResponseEntity<String> sendEmailToParent(@Valid @RequestBody @NotNull ChildParentEmailRequest request,
                                                    BindingResult bindingResult) throws EmailSendingException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + bindingResult.getAllErrors());
        }
        String childEmail = request.getChildEmail();
        String parentEmail = request.getParentEmail();
        String pin = PinGenerator.generatePin();

        childLinkingHelper.composeParentConfirmationEmail(childEmail, parentEmail, pin);
        return ResponseEntity.ok().body(pin);
    }

    @PostMapping("link/child/doctor/send-pin")
    public ResponseEntity<String> sendEmailToDoctor(@Valid @RequestBody @NotNull ChildDoctorEmailRequest request,
                                                    BindingResult bindingResult) throws EmailSendingException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + bindingResult.getAllErrors());
        }
        // Get child email and parent email from request
        String childEmail = request.getChildEmail();
        String doctorEmail = request.getDoctorEmail();
        String pin = PinGenerator.generatePin();

        childLinkingHelper.composeDoctorConfirmationEmail(childEmail, doctorEmail, pin);
        return ResponseEntity.ok().body(pin);
    }

    @PostMapping("link/child/doctor/confirmed")
    public ResponseEntity<String> linkChildDoctor(@RequestHeader("Authorization") String token,
                                                  @Valid @RequestBody @NotNull ChildDoctorEmailRequest request,
                                                  BindingResult bindingResult) throws DoctorDoesNotExistException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + bindingResult.getAllErrors());
        }

        token = StringOperations.removeBearerIfExist(token);
        token = StringOperations.removeQuotesIfExist(token);
        if (!JwtTokenUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        String childEmail = request.getChildEmail();
        String doctorEmail = request.getDoctorEmail();

        userRegistrationHelper.confirmDoctorMentorship(childEmail, doctorEmail);
        return ResponseEntity.ok().body("Child has been linked to the doctor successfully!");
    }

    @PostMapping("register/child")
    public ResponseEntity<String> registerChild(@Valid @RequestBody @NotNull ChildRegistrationData childData,
                                                BindingResult bindingResult) throws UserAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + bindingResult.getAllErrors());
        }

        userRegistrationHelper.registerChild(childData);
        String token = JwtTokenUtil.generateToken(childData.getEmail(), childData.getFirstName(), UserType.CHILD);
        return ResponseEntity.ok().body(token);
    }

    @PostMapping("register/parent")
    public ResponseEntity<String> registerParent(@Valid @RequestBody @NotNull ParentRegistrationData parentRegistrationData,
                                                 BindingResult bindingResult) throws UserAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + bindingResult.getAllErrors());
        }
        userRegistrationHelper.registerParent(parentRegistrationData);
        String token = JwtTokenUtil.generateToken(parentRegistrationData.getEmail(), parentRegistrationData.getUsername(), UserType.PARENT);
        return ResponseEntity.ok().body(token);
    }

    @PostMapping("register/doctor")
    public ResponseEntity<String> registerDoctor(@Valid @RequestBody @NotNull DoctorRegistrationData doctorRegistrationData,
                                                 BindingResult bindingResult) throws UserAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + bindingResult.getAllErrors());
        }
        userRegistrationHelper.registerDoctor(doctorRegistrationData);
        String token = JwtTokenUtil.generateToken(doctorRegistrationData.getEmail(), doctorRegistrationData.getUsername(), UserType.DOCTOR);
        return ResponseEntity.ok().body(token);
    }

    @PostMapping("login/user")
    public ResponseEntity<?> verifyUserInfo(@Valid @RequestBody @NotNull SharedRegistrationData userData,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + bindingResult.getAllErrors());
        }
        if (!userRegistrationHelper.verifyUserInfo(userData)) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
        String email = userData.getEmail();
        UserType userType = userData.getUserType();
        String username = userInfoHelper.getUsername(email, userType);
        String token = JwtTokenUtil.generateToken(userData.getEmail(), username, userData.getUserType());
        return ResponseEntity.ok().body(token);
    }

    @PostMapping("validate/user/token")
    public ResponseEntity<Boolean> validateUserToken(@RequestHeader("Authorization") String token) {
        boolean value = JwtTokenUtil.validateToken(token);
        return ResponseEntity.ok().body(value);
    }
}
