package com.doghotel.reservation.global.auth;

import com.doghotel.reservation.global.config.security.userdetail.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;
import java.util.List;

public class WithMockCustomCustomerSecurityContextFactory implements WithSecurityContextFactory<WithAuthCustomer> {



    @Override
    public SecurityContext createSecurityContext(WithAuthCustomer annotation) {
        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        CustomUserDetails principal =
                new CustomUserDetails(annotation.email(), annotation.password(), annotation.roles());
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(annotation.roles().name()));
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        securityContext.setAuthentication(authentication);
        return securityContext;
    }
}
