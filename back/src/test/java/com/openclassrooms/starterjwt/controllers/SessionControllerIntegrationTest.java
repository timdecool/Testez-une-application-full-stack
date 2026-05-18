package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("SessionController: integration tests")
public class SessionControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TeacherRepository teacherRepository;

    protected Session savedSession1;
    protected Session savedSession2;
    protected User savedUser;
    protected SessionDto mockSessionDto;

    @BeforeEach
    void buildMockData() {
        Teacher savedTeacher = teacherRepository.save(Teacher.builder()
                .firstName("Miranda")
                .lastName("Portique")
                .build());

        savedUser = userRepository.save(new User()
                .setEmail("test@test.com")
                .setFirstName("Michel")
                .setLastName("Boulon")
                .setPassword("encoded")
                .setAdmin(false));

        savedSession1 = sessionRepository.save(Session.builder()
                .name("Yoga du soir")
                .description("Nouvelle session!")
                .date(new Date())
                .users(new ArrayList<>())
                .build());

        List<User> participants = new ArrayList<>();
        participants.add(savedUser);
        savedSession2 = sessionRepository.save(Session.builder()
                .name("Yoga du matin")
                .description("Autre session!")
                .date(new Date())
                .users(new ArrayList<>(participants))
                .build());

        mockSessionDto = new SessionDto();
        mockSessionDto.setName("Yoga du soir");
        mockSessionDto.setDescription("Nouvelle session!");
        mockSessionDto.setDate(new Date());
        mockSessionDto.setTeacher_id(savedTeacher.getId());
    }

    @Nested
    @DisplayName("Find by id integration: GET /api/session/{id}")
    class FindByIdIntegrationTest {

        @Test
        @DisplayName("should return 401 when user is not authenticated")
        void findById_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/api/session/" + savedSession1.getId())).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        @DisplayName("should return mapped session")
        void findById_shouldReturnSession() throws Exception {
            mockMvc.perform(get("/api/session/" + savedSession1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Yoga du soir"))
                    .andExpect(jsonPath("$.description").value("Nouvelle session!"));
        }

        @Test
        @WithMockUser
        @DisplayName("should return not found when session does not exist")
        void findById_sessionNotFound_shouldReturnNotFound() throws Exception {
            mockMvc.perform(get("/api/session/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("should return bad request when id is invalid")
        void findById_idInvalid_shouldReturnBadRequest() throws Exception {
            mockMvc.perform(get("/api/session/ABC"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Find all integration: GET /api/session")
    class FindAllIntegrationTest {

        @Test
        @WithMockUser
        @DisplayName("should return all sessions")
        void findAll_shouldReturnAll() throws Exception {
            mockMvc.perform(get("/api/session"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("Yoga du soir"));
        }
    }

    @Nested
    @DisplayName("Create integration: POST /api/session")
    class CreateIntegrationTest {

        @Test
        @WithMockUser
        @DisplayName("should create and return session")
        void create_shouldReturnSession() throws Exception {
            mockMvc.perform(post("/api/session")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockSessionDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Yoga du soir"))
                    .andExpect(jsonPath("$.description").value("Nouvelle session!"));
            assertThat(sessionRepository.findAll()).hasSize(3);
        }

        @Test
        @WithMockUser
        @DisplayName("should return bad request when body is invalid")
        void create_bodyInvalid_shouldReturnBadRequest() throws Exception {
            mockSessionDto.setName(null); // ← @NotBlank
            mockMvc.perform(post("/api/session")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockSessionDto)))
                    .andExpect(status().isBadRequest());

            assertThat(sessionRepository.findAll()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Update integration: PUT /api/session/{id}")
    class UpdateIntegrationTest {

        @Test
        @WithMockUser
        @DisplayName("should update and return session")
        void update_shouldUpdateSession() throws Exception {
            mockSessionDto.setName("Yoga du soir modifié");

            mockMvc.perform(put("/api/session/" + savedSession1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockSessionDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Yoga du soir modifié"));
        }

        @Test
        @WithMockUser
        @DisplayName("should return 400 when body is invalid")
        void update_bodyInvalid_shouldReturnBadRequest() throws Exception {
            mockSessionDto.setName(null);
            mockMvc.perform(put("/api/session/" + savedSession1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockSessionDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Delete integration: DELETE /api/session/{id}")
    class DeleteIntegrationTest {

        @Test
        @WithMockUser
        @DisplayName("should delete session")
        void delete_shouldDeleteSession() throws Exception {
            mockMvc.perform(delete("/api/session/" + savedSession1.getId()))
                    .andExpect(status().isOk());
            assertThat(sessionRepository.findById(savedSession1.getId())).isEmpty();
        }

        @Test
        @WithMockUser
        @DisplayName("should return 404 when session not found")
        void delete_sessionNotFound_shouldReturnNotFound() throws Exception {
            mockMvc.perform(delete("/api/session/99999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Participate integration: POST /api/session/{id}/participate/{userId}")
    class ParticipateIntegrationTest {

        @Test
        @WithMockUser
        @DisplayName("should add participation")
        void participate_shouldAddParticipation() throws Exception {
            mockMvc.perform(post("/api/session/" + savedSession1.getId()
                            + "/participate/" + savedUser.getId()))
                    .andExpect(status().isOk());
            Session updated = sessionRepository.findById(savedSession1.getId()).orElseThrow(NotFoundException::new);
            assertThat(updated.getUsers()).hasSize(1);
        }

        @Test
        @WithMockUser
        @DisplayName("should return 400 when user already participates")
        void participate_alreadyParticipates_shouldReturnBadRequest() throws Exception {
            mockMvc.perform(post("/api/session/" + savedSession2.getId()
                            + "/participate/" + savedUser.getId()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("should return 404 when session not found")
        void participate_sessionNotFound_shouldReturnNotFound() throws Exception {
            mockMvc.perform(post("/api/session/99999/participate/" + savedUser.getId()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("should return 404 when user not found")
        void participate_userNotFound_shouldReturnNotFound() throws Exception {
            mockMvc.perform(post("/api/session/" + savedSession1.getId() + "/participate/99999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("No longer participate: DELETE /api/session/{id}/participate/{userId}")
    class NoLongerParticipateIntegrationTest {

        @Test
        @WithMockUser
        @DisplayName("should remove participation")
        void noLongerParticipate_shouldRemoveParticipation() throws Exception {
            mockMvc.perform(delete("/api/session/" + savedSession2.getId()
                            + "/participate/" + savedUser.getId()))
                    .andExpect(status().isOk());

            Session updated = sessionRepository.findById(savedSession2.getId()).orElseThrow(NotFoundException::new);
            assertThat(updated.getUsers()).isEmpty();
        }

        @Test
        @WithMockUser
        @DisplayName("should return 400 when user not participating")
        void noLongerParticipate_userNotParticipating_shouldReturnBadRequest() throws Exception {
            mockMvc.perform(delete("/api/session/" + savedSession1.getId()
                            + "/participate/" + savedUser.getId()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("should return 404 when session not found")
        void noLongerParticipate_sessionNotFound_shouldReturnNotFound() throws Exception {
            mockMvc.perform(delete("/api/session/99999/participate/" + savedUser.getId()))
                    .andExpect(status().isNotFound());
        }
    }
}