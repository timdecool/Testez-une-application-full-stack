package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.awt.*;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController: register and authenticate user endpoints")
public class AuthControllerTest {
    @InjectMocks
    private AuthController authController;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    @Nested
    @DisplayName("Authenticate user")
    class AuthenticateUserTest {
        @Test
        @DisplayName("should return JWT response when valid credentials")
        void authenticateUser_validCredentials_shouldReturnJwtReponse() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("yoga@studio.com");
            loginRequest.setPassword("password123");

            UserDetailsImpl userDetails = UserDetailsImpl.builder()
                    .id(1L)
                    .username("yoga@studio.com")
                    .firstName("Michel")
                    .lastName("Boulon")
                    .password("hashed")
                    .build();

            User user = new User()
                    .setEmail("yoga@studio.com")
                    .setPassword("hashed")
                    .setFirstName("Michel")
                    .setLastName("Boulon")
                    .setAdmin(false)
                    .setId(1L);
            Authentication authentication = mock(Authentication.class);

            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn("generatedToken");
            when(userRepository.findByEmail("yoga@studio.com")).thenReturn(Optional.of(user));

            ResponseEntity<?> response = authController.authenticateUser(loginRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            JwtResponse body = (JwtResponse) response.getBody();
            Assertions.assertNotNull(body);
            assertThat(body.getToken()).isEqualTo("generatedToken");
            assertThat(body.getId()).isEqualTo(1L);
            assertThat(body.getUsername()).isEqualTo("yoga@studio.com");
            assertThat(body.getFirstName()).isEqualTo("Michel");
            assertThat(body.getLastName()).isEqualTo("Boulon");
            assertThat(body.getAdmin()).isFalse();
        }
    }

    @Nested
    @DisplayName("Register User")
    class RegisterUserTest {
        
        private SignupRequest buildSignupRequest() {
            SignupRequest request = new SignupRequest();
            request.setEmail("new@studio.com");
            request.setFirstName("Michel");
            request.setLastName("Boulon");
            request.setPassword("password123");
            return request;
        }

        @Test
        @DisplayName("should return 200 success when new user is created")
        void register_newUser_shouldReturnSuccessMessage() {
            when(userRepository.existsByEmail("new@studio.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encoded");

            ResponseEntity<?> response = authController.registerUser(buildSignupRequest());
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            MessageResponse body = (MessageResponse) response.getBody();
            Assertions.assertNotNull(body);
            assertThat(body.getMessage()).isEqualTo("User registered successfully!");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should encode password before saving when new user is valid")
        void register_newUser_shouldEncodePassword() {
            when(userRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

            authController.registerUser(buildSignupRequest());
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-password");
        }

        @Test
        @DisplayName("should return 400 bad request when email already exists")
        void register_existingEmail_shouldReturnBadRequest() {
            when(userRepository.existsByEmail("new@studio.com")).thenReturn(true);

            ResponseEntity<?> response = authController.registerUser(buildSignupRequest());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            MessageResponse body = (MessageResponse) response.getBody();
            Assertions.assertNotNull(body);
            assertThat(body.getMessage()).isEqualTo("Error: Email is already taken!");
            verify(userRepository, never()).save(any());
        }
    }
}
