package com.example.authenticationserivce.custom_annotations;


import com.example.authenticationserivce.util.JwtTokenUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
public class TokenValidationAspect {
    final String authorizationHeader = "Authorization";

    @Around("@annotation(ValidJwtToken)")
    public Object validateToken(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            RequestHeader requestHeader = parameters[i].getAnnotation(RequestHeader.class);
            if (requestHeader != null && authorizationHeader.equals(requestHeader.value())) {
                String token = (String) joinPoint.getArgs()[i];
                if (!JwtTokenUtil.validateToken(token)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
                }
            }
        }

        return joinPoint.proceed();
    }
}
