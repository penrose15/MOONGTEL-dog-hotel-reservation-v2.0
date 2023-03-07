package com.doghotel.reservation.global.config.security.filter;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import com.doghotel.reservation.global.config.redis.RedisTemplateRepository;
import com.doghotel.reservation.global.config.security.jwt.JwtTokenizer;
import com.doghotel.reservation.global.config.security.userdetail.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {
    private final JwtTokenizer jwtTokenizer;
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final RedisTemplateRepository redisTemplateRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Map<String, Object> claims = verifyJws(request);
            setAuthenticationToContext(claims);
        }  catch (ExpiredJwtException | SignatureException ee) {
            ee.printStackTrace();
            reissueToken(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String authorization = request.getHeader("Authorization");
        return authorization == null || !authorization.startsWith("Bearer ");

    }

    private String getAccessJwtToken(HttpServletRequest request) {
        if(request.getHeader("Authorization") != null) {
            return request.getHeader("Authorization").substring(7);
        }
        return null;
    }
    private String getRefreshToken(HttpServletRequest request) {
        if(request.getHeader("Refresh") != null) {
            return request.getHeader("Refresh");
        }
        return null;
    }

    private Map<String, Object> verifyJws(HttpServletRequest request) {
        String jws = request.getHeader("Authorization").replace("Bearer ", ""); // (3-1)
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey()); // (3-2)
        Map<String, Object> claims = jwtTokenizer.getClaims(jws, base64EncodedSecretKey).getBody();   // (3-3)

        return claims;
    }

    private void reissueToken(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(">>reissue start");
        String jws = getAccessJwtToken(request);
        String refreshToken = getRefreshToken(request);
        if(!jwtTokenizer.validateToken(jws) && refreshToken != null) {
            try {
                refreshToken = refreshToken.substring(7);
                if(jwtTokenizer.validateToken(refreshToken)) {
                    String email = jwtTokenizer.getEmailFromRefreshToken(refreshToken);

                    String email2 = redisTemplateRepository.findById(email)
                            .orElseThrow(NoSuchElementException::new)
                            .getEmail();

                    Optional<Customer> customer = customerRepository.findByEmail(email2);
                    Optional<Company> company = companyRepository.findByEmail(email2);

                    String email1 = "";
                    String roles = "";
                    if(customer.isPresent()) {
                        email1 = customer.get().getEmail();
                        roles = customer.get().getRoles().name();
                    } else if(company.isPresent()) {
                        email1 = company.get().getEmail();
                        roles = company.get().getRoles().name();
                    } else {
                        throw new NoSuchElementException("잘못된 토큰");
                    }
                    if(email.equals(email1)) {
                        Map<String, Object> claims = new HashMap<>();
                        claims.put("username", email);
                        claims.put("roles", roles);
                        System.out.println(">>");
                        Date expiration = jwtTokenizer.getTokenExpiration(Integer.parseInt(jwtTokenizer.getAccessTokenExpirationMinutes()));
                        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
                        String accessToken = jwtTokenizer.generateAccessToken(claims, email, expiration, base64EncodedSecretKey);
                        response.setHeader("Authorization", accessToken);
                        setAuthenticationToContext(claims);
                    }
                    else {
                        throw new MalformedJwtException("wrong refreshToken");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.info("리프레시 토큰 없음, 유효하지 않은 액서스 토큰");
        }
    }

    private void setAuthenticationToContext(Map<String, Object> claims) {
        String username = (String) claims.get("username");

        Optional<Customer> optionalCustomer = customerRepository.findByEmail(username);
        Optional<Company> optionalCompany = companyRepository.findByEmail(username);

        CustomUserDetails customUserDetails;
        if(optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customUserDetails = new CustomUserDetails(customer.getEmail(), customer.getPassword(), customer.getRoles());
        } else if(optionalCompany.isPresent()) {
            Company company = optionalCompany.get();
            customUserDetails = new CustomUserDetails(company.getEmail(), company.getPassword(), company.getRoles());
        } else {
            throw new NoSuchElementException("존재하지 않는 유저");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        String roles = (String) claims.get("roles");
        authorities.add(new SimpleGrantedAuthority("ROLE_" + roles));

        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
