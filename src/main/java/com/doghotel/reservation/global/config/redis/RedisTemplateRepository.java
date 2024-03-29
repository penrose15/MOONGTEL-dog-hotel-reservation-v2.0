package com.doghotel.reservation.global.config.redis;

import com.doghotel.reservation.global.exception.ExceptionCode;
import com.doghotel.reservation.global.refreshtoken.RefreshToken;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisTemplateRepository {
    private final RedisTemplate redisTemplate;

    public RedisTemplateRepository(final RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(final RefreshToken refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken.getRefreshToken(), refreshToken.getEmail());
        redisTemplate.expire(refreshToken.getRefreshToken(), 60L * 60  * 24, TimeUnit.SECONDS);
    }
    public Optional<RefreshToken> findById(final String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String email = valueOperations.get(refreshToken);

        if(Objects.isNull(email)) {
            return Optional.empty();
        }
        return Optional.of(new RefreshToken(refreshToken, email));
    }

    public void saveAuthCode(String email, String authCode) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(email, authCode);
        redisTemplate.expire(email, 60L * 10, TimeUnit.SECONDS);
    }

    public String findEmail(String email) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String authCode = valueOperations.get(email);

        if(Objects.isNull(authCode)) {
            throw new IllegalArgumentException();
        }
        return authCode;
    }

}
