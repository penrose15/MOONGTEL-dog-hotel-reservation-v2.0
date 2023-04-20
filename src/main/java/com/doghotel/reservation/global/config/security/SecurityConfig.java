package com.doghotel.reservation.global.config.security;


import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import com.doghotel.reservation.global.config.redis.RedisTemplateRepository;
import com.doghotel.reservation.global.config.security.filter.JwtAuthenticationFilter;
import com.doghotel.reservation.global.config.security.filter.JwtVerificationFilter;
import com.doghotel.reservation.global.config.security.handler.UsersAuthenticationFailureHandler;
import com.doghotel.reservation.global.config.security.handler.UsersAuthenticationSuccessHandler;
import com.doghotel.reservation.global.config.security.jwt.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private final CorsFilter corsFilter;
    private final RedisTemplateRepository redisTemplateRepository;
    private final JwtTokenizer jwtTokenizer;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.formLogin().disable();
        http.headers().frameOptions().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic()
                .disable()
                .apply(new CustomDsl())
                .and()
                .authorizeRequests()
                .antMatchers("/h2/**")
                .permitAll()
                .antMatchers("/api/login")
                .permitAll()
                .antMatchers("/profile").permitAll()
                .antMatchers("/test").permitAll()
                .antMatchers("/v1/company/account")
                .permitAll()
                .antMatchers("/v1/customer/account")
                .permitAll()
                .antMatchers("/v1/company/**")
                .access("hasRole('ROLE_COMPANY') or hasRole('ROLE_ADMIN')")
                .antMatchers("/v1/customer/**")
                .access("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/v1/post/company/**")
                .access("hasRole('ROLE_COMPANY') or hasRole('ROLE_ADMIN')")
                .antMatchers("/v1/post/**")
                .permitAll()
                .anyRequest().authenticated();


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public class CustomDsl extends AbstractHttpConfigurer<CustomDsl, HttpSecurity> {

        @Override
        public void configure(HttpSecurity builder) {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter
                    = new JwtAuthenticationFilter(authenticationManager, redisTemplateRepository, jwtTokenizer, passwordEncoder(), companyRepository, customerRepository);
            jwtAuthenticationFilter.setFilterProcessesUrl("/api/login");
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new UsersAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new UsersAuthenticationFailureHandler());

            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, customerRepository, companyRepository, redisTemplateRepository);

            builder
                    .addFilter(corsFilter)
                    .addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
        }
    }
}