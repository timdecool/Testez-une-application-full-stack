package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController : integration tests")
public class UserControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    protected UserRepository userRepository;

    protected User mockUser;

    @BeforeEach
    void buildMockData() {
        mockUser = User.builder()
                .id(1L)
                .admin(false)
                .firstName("Michel")
                .lastName("Boulon")
                .email("michel.boulon@laposte.net")
                .password("hashed")
                .build();
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
            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            mockMvc.perform(get("/api/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.lastName").value("Boulon"))
                    .andExpect(jsonPath("$.firstName").value("Michel"));
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
            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            doNothing().when(userRepository).deleteById(1L);
            mockMvc.perform(delete("/api/user/1")).andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "other@studio.com")
        @DisplayName("should return 401 status when authenticated user is not user to delete")
        void delete_authenticatedUserDifferent_shouldReturnUnauthorized() throws Exception {
            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            mockMvc.perform(delete("/api/user/1")).andExpect(status().isUnauthorized());
        }
    }
}
