package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
    @DisplayName("validateJwtToken: invalid signature should return false")
    void validateJwtToken_invalidSignature_shouldReturnFalse() {
        // Token signé avec une autre clé
        String otherSecret = "autreSecretTresLongPourLesTests1234567890";
        String tokenWithWrongSignature = Jwts.builder()
                .setSubject("test@test.com")
                .signWith(SignatureAlgorithm.HS512, otherSecret)
                .compact();

        assertThat(jwtUtils.validateJwtToken(tokenWithWrongSignature)).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken: malformed token should return false")
    void validateJwtToken_malformedToken_shouldReturnFalse() {
        assertThat(jwtUtils.validateJwtToken("not.a.valid.jwt.token")).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken: empty token should return false")
    void validateJwtToken_emptyToken_shouldReturnFalse() {
        assertThat(jwtUtils.validateJwtToken("")).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken: unsupported token should return false")
    void validateJwtToken_unsupportedToken_shouldReturnFalse() {
        // Token sans signature (algorithme "none")
        String unsignedToken = Jwts.builder()
                .setSubject("test@test.com")
                .compact(); // ← pas de signature
        assertThat(jwtUtils.validateJwtToken(unsignedToken)).isFalse();
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