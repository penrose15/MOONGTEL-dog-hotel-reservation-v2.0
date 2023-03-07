package com.doghotel.reservation.global.config.security.filter;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import com.doghotel.reservation.global.config.redis.RedisTemplateRepository;
import com.doghotel.reservation.global.config.security.dto.LoginDto;
import com.doghotel.reservation.global.config.security.jwt.JwtTokenizer;
import com.doghotel.reservation.global.config.security.userdetail.CustomUserDetails;
import com.doghotel.reservation.global.refreshtoken.RefreshToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final RedisTemplateRepository redisTemplateRepository;
    private final JwtTokenizer jwtTokenizer;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);

        Optional<Company> optionalCompany = companyRepository.findByEmail(loginDto.getUsername());
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(loginDto.getUsername());


        String password = "";
        if(optionalCompany.isPresent()) {
            Company company = optionalCompany.get();
            password = company.getPassword();

        } else if(optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            password = customer.getPassword();
        } else {
            throw new IllegalArgumentException("잘못된 이메일");
        }

        if(!passwordEncoder.matches(loginDto.getPassword(), password)) {
            throw new AuthenticationException("wrong password") {
                @Override
                public String getMessage() {
                    return super.getMessage();
                }
            };
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        String accessToken = delegateAccessToken(customUserDetails);
        String refreshToken = delegateRefreshToken(customUserDetails);

        response.setHeader("Authorization","Bearer "+ accessToken);
        response.setHeader("Refresh", "Bearer "+ refreshToken);

        redisTemplateRepository.save(new RefreshToken("Bearer" + refreshToken, customUserDetails.getEmail()));

        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }

    private String delegateAccessToken(CustomUserDetails customUserDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", customUserDetails.getEmail());
        claims.put("roles",customUserDetails.getRole().name());

        String subject = customUserDetails.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(Integer.parseInt(jwtTokenizer.getAccessTokenExpirationMinutes()));
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
        String accessToken = jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);
        return accessToken;
    }

    private String delegateRefreshToken(CustomUserDetails customUserDetails) {
        String subject = customUserDetails.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(Integer.parseInt(jwtTokenizer.getRefreshTokenExpirationMinutes()));
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String refreshToken = jwtTokenizer.generateRefreshToken(subject, expiration, base64EncodedSecretKey);
        return refreshToken;
    }
}
