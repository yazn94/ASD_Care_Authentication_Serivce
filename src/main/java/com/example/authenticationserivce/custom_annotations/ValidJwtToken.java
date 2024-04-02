package com.example.authenticationserivce.custom_annotations;


import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidJwtToken {
    String message() default "Invalid JWT token";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
