package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecretKey123456789testSecretKey");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000);
    }

    @Test
    @DisplayName("generateJwtToken: should generate valid token")
    void generateJwtToken_shouldGenerateToken() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String token = jwtUtils.generateJwtToken(auth);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("validateJwtToken: valid token should return true")
    void validateJwtToken_validToken_shouldReturnTrue() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L).username("test@test.com").build();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        String token = jwtUtils.generateJwtToken(auth);

        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
    }

    @Test
    @DisplayName("validateJwtToken: invalid token should return false")
    void validateJwtToken_invalidToken_shouldReturnFalse() {
        assertThat(jwtUtils.validateJwtToken("invalid.token.here")).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken: expired token should return false")
    void validateJwtToken_expiredToken_shouldReturnFalse() {
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -1000); // ← expiré immédiatement
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L).username("test@test.com").build();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        String token = jwtUtils.generateJwtToken(auth);

        assertThat(jwtUtils.validateJwtToken(token)).isFalse();
    }

    @Test
    @DisplayName("getUserNameFromJwtToken: should extract username")
    void getUserNameFromJwtToken_shouldReturnUsername() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        String token = jwtUtils.generateJwtToken(auth);

        assertThat(jwtUtils.getUserNameFromJwtToken(token)).isEqualTo("test@test.com");
    }
}