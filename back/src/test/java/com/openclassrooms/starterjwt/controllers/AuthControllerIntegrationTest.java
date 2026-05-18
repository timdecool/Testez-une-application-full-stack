package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController: integration tests")
public class AuthControllerIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @MockBean
    protected AuthenticationManager authenticationManager;

    @Nested
    @DisplayName("Authenticate user integration: POST /api/auth/login")
    class AuthenticateUserIntegrationTest {

        private LoginRequest loginRequest;
        private UserDetailsImpl mockUserDetails;
        private Authentication mockAuthentication;

        @BeforeEach
        void buildMockData() {
            User user = new User()
                    .setEmail("michel.boulon@laposte.net")
                    .setLastName("Boulon")
                    .setFirstName("Michel")
                    .setPassword("encoded")
                    .setAdmin(false);
            userRepository.save(user);

            loginRequest = new LoginRequest();
            loginRequest.setEmail("michel.boulon@laposte.net");
            loginRequest.setPassword("password");

            mockUserDetails = UserDetailsImpl.builder()
                    .id(user.getId())
                    .username("michel.boulon@laposte.net")
                    .firstName("Michel")
                    .lastName("Boulon")
                    .password("encoded")
                    .build();

            mockAuthentication = mock(Authentication.class);
            when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
        }

        @Test
        @DisplayName("should return jwt and userDetails on valid credentials")
        void authenticateUser_shouldReturnJwtAndUserDetails() throws Exception {
            when(authenticationManager.authenticate(any())).thenReturn(mockAuthentication);
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.username").value("michel.boulon@laposte.net"))
                    .andExpect(jsonPath("$.firstName").value("Michel"))
                    .andExpect(jsonPath("$.lastName").value("Boulon"))
                    .andExpect(jsonPath("$.admin").value(false));
        }

        @Test
        @DisplayName("should return admin true when user is admin")
        void authenticateUser_adminUser_shouldReturnIsAdminTrue() throws Exception {
            User adminUser = userRepository.findByEmail("michel.boulon@laposte.net").orElseThrow(NoSuchElementException::new);
            adminUser.setAdmin(true);
            userRepository.save(adminUser);

            when(authenticationManager.authenticate(any())).thenReturn(mockAuthentication);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.admin").value(true));
        }

        @Test
        @DisplayName("should return isAdmin false when user not found in db")
        void authenticateUser_userNotFound_shouldReturnIsAdminFalse() throws Exception {
            userRepository.deleteAll();
            when(authenticationManager.authenticate(any())).thenReturn(mockAuthentication);
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.admin").value(false));
        }

        @Test
        @DisplayName("should return 401 when credentials are invalid")
        void authenticateUser_invalidCredentials_shouldReturnUnauthorized() throws Exception {
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Invalid credentials"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("should return 400 when body is invalid")
        void authenticateUser_bodyInvalid_shouldReturnBadRequest() throws Exception {
            loginRequest.setEmail(null);
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Register user integration: POST /api/auth/register")
    class RegisterUserIntegrationTest {

        private SignupRequest signupRequest;

        @BeforeEach
        void buildMockData() {
            signupRequest = new SignupRequest();
            signupRequest.setLastName("Boulon");
            signupRequest.setFirstName("Michel");
            signupRequest.setEmail("michel.boulon@laposte.net");
            signupRequest.setPassword("password");
        }

        @Test
        @DisplayName("should register user and return 200 status")
        void registerUser_shouldRegisterUser() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User registered successfully!"));

            User saved = userRepository.findByEmail("michel.boulon@laposte.net")
                    .orElseThrow(NoSuchElementException::new);
            assertThat(saved.getPassword())
                    .isNotEqualTo("password")
                    .startsWith("$2a$");
        }

        @Test
        @DisplayName("should return 400 when email already exists")
        void registerUser_emailExists_shouldReturnBadRequest() throws Exception {
            userRepository.save(new User()
                    .setEmail("michel.boulon@laposte.net")
                    .setLastName("Boulon")
                    .setFirstName("Michel")
                    .setPassword("encoded")
                    .setAdmin(false));

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));

            assertThat(userRepository.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("should return 400 when body is invalid")
        void registerUser_bodyInvalid_shouldReturnBadRequest() throws Exception {
            signupRequest.setLastName(null);
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());

            assertThat(userRepository.findAll()).isEmpty();
        }
    }
}