package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("UserController : integration tests")
public class UserControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    protected UserRepository userRepository;

    protected User savedUser;

    @BeforeEach
    void buildMockData() {
        User user = User.builder()
                .admin(false)
                .firstName("Michel")
                .lastName("Boulon")
                .email("michel.boulon@laposte.net")
                .password("hashed")
                .createdAt(LocalDateTime.of(2026, 5, 15, 0, 0))
                .updatedAt(LocalDateTime.of(2026, 5, 15, 0, 0))
                .build();
        savedUser = userRepository.save(user);
    }

    @Nested
    @DisplayName("Find by id integration: GET /api/user/{id}")
    class FindByIdIntegrationTest {
        @Test
        @DisplayName("should return 401 when user is not authenticated")
        void findById_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/api/user/1")).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        @DisplayName("should return mapped user")
        public void findById_shouldReturnSession() throws Exception {
            mockMvc.perform(get("/api/user/" + savedUser.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.lastName").value("Boulon"))
                    .andExpect(jsonPath("$.firstName").value("Michel"));
        }

        @Test
        @WithMockUser
        void findById_userNotFound_shouldReturnNotFound() throws Exception {
            mockMvc.perform(get("/api/user/99999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        void findById_idInvalid_shouldReturnBadRequest() throws Exception {
            mockMvc.perform(get("/api/user/ABC"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Delete integration: DELETE /api/user/{id}")
    class DeleteIntegrationTest {
        @Test
        @DisplayName("should return 401 when user is not authenticated")
        void delete_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
            mockMvc.perform(delete("/api/user/1")).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "michel.boulon@laposte.net")
        @DisplayName("should delete user when authenticated user is equal to user to delete")
        void delete_shouldDeleteUser() throws Exception {
            mockMvc.perform(delete("/api/user/" + savedUser.getId())).andExpect(status().isOk());
            assertThat(userRepository.findById(savedUser.getId())).isEmpty();
        }

        @Test
        @WithMockUser(username = "michel.boulon@laposte.net")
        void delete_userNotFound_shouldReturnNotFound() throws Exception {
            mockMvc.perform(delete("/api/user/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "other@studio.com")
        @DisplayName("should return 401 status when authenticated user is not user to delete")
        void delete_authenticatedUserDifferent_shouldReturnUnauthorized() throws Exception {
            mockMvc.perform(delete("/api/user/" + savedUser.getId())).andExpect(status().isUnauthorized());
        }
    }
}
