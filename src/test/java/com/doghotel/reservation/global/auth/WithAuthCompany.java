package com.doghotel.reservation.global.auth;

import com.doghotel.reservation.domain.role.Roles;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomCompanySecurityContextFactory.class)
public @interface WithAuthCompany {
    String email() default "company@gmail.com";
    String password() default "1234abcd!";
    Roles roles() default Roles.COMPANY;
}
