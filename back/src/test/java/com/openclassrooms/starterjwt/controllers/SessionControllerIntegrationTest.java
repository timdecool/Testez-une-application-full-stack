package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SessionController: integration tests")
public class SessionControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected SessionRepository sessionRepository;

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected TeacherRepository teacherRepository;

    protected SessionDto mockSessionDto;
    protected List<Session> mockSessions;
    protected Teacher mockTeacher;

    @BeforeEach
    void buildMockData() {
        User user = new User().setId(1L);
        List<User> participants = new ArrayList<>();
        participants.add(user);

        Session mockSession = Session.builder()
                .id(1L)
                .name("Yoga du soir")
                .description("Nouvelle session!")
                .date(new Date())
                .users(new ArrayList<>())
                .build();

        Session mockSession2 = Session.builder()
                .id(2L)
                .name("Yoga du matin")
                .date(new Date())
                .description("Autre session!")
                .users(participants)
                .build();
        mockSessions = new ArrayList<>();
        mockSessions.add(mockSession);
        mockSessions.add(mockSession2);

        mockSessionDto = new SessionDto();
        mockSessionDto.setId(1L);
        mockSessionDto.setName("Yoga du soir");
        mockSessionDto.setDescription("Nouvelle session!");
        mockSessionDto.setDate(new Date());
        mockSessionDto.setTeacher_id(1L);
        mockTeacher = Teacher.builder().id(1L).firstName("Miranda").lastName("Portique").build();
    }

    @Nested
    @DisplayName("Find by id integration: GET /api/session/{id}")
    class FindByIdIntegrationTest {
        @Test
        @DisplayName("should return 401 when user is not authenticated")
        void findById_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/api/session/1")).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        @DisplayName("should return mapped session")
        public void findById_shouldReturnSession() throws Exception {
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSessions.get(0)));
            mockMvc.perform(get("/api/session/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Yoga du soir"));
        }

        @Test
        @WithMockUser
        @DisplayName("should return not found when session does not exist")
        public void findById_sessionNotFound_shouldReturnNotFound() throws Exception {
            when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
            mockMvc.perform(get("/api/session/1"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("should return bad request when id is invalid")
        public void findById_idInvalid_shouldReturnBadRequest() throws Exception {
            mockMvc.perform(get("/api/session/ABC"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Find all integration: GET /api/session")
    class FindAllIntegrationTest {
        @Test
        @WithMockUser
        @DisplayName("Should return all sessions")
        void findAll_shouldReturnAll() throws Exception {
            List<Session> sessions = new ArrayList<>();
            sessions.add(new Session().setId(1L).setName("Yoga du soir"));
            sessions.add(new Session().setId(2L).setName("Yoga du matin"));
            when(sessionRepository.findAll()).thenReturn(sessions);

            mockMvc.perform(get("/api/session"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L));
        }
    }

    @Nested
    @DisplayName("Create integration: POST /api/session")
    class CreateIntegrationTest {
        @Test
        @WithMockUser
        @DisplayName("should return created session with status 200")
        public void create_shouldReturnSession() throws Exception {
            when(sessionRepository.save(any(Session.class))).thenReturn(mockSessions.get(0));
            when(teacherRepository.findById(1L)).thenReturn(Optional.of(mockTeacher));
            mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockSessionDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Yoga du soir"));
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/session should return bad request when body is invalid")
        public void create_bodyInvalid_shouldReturnBadRequest() throws Exception {
            mockSessionDto.setName(null);
            mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockSessionDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Update integration: PUT /api/session/{id}")
    class UpdateIntegrationTest {
        @Test
        @WithMockUser
        @DisplayName("should update and return session")
        void update_shouldUpdateSession() throws Exception {
            when(sessionRepository.save(any(Session.class))).thenReturn(mockSessions.get(0));
            mockMvc.perform(put("/api/session/1").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockSessionDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Yoga du soir"));
        }

        @Test
        @WithMockUser
        @DisplayName("should return status 400 when body is invalid")
        void update_bodyInvalid_shouldReturnBadRequest() throws Exception {
            mockSessionDto.setName(null);
            mockMvc.perform(put("/api/session/1").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockSessionDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Delete integration: DELETE /api/session/{id}")
    class DeleteIntegrationTest {
        @Test
        @WithMockUser
        @DisplayName("should delete session when id is valid")
        void delete_shouldDeleteSession() throws Exception {
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSessions.get(0)));
            doNothing().when(sessionRepository).deleteById(1L);
            mockMvc.perform(delete("/api/session/1")).andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        @DisplayName("should return 404 status when session is not found")
        void delete_sessionNotFound_shouldReturnNotFound() throws Exception {
            when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
            mockMvc.perform(delete("/api/session/1")).andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Participate integration: POST /api/session/{id}/participate/{userId}")
    class ParticipateIntegrationTest {
        @Test
        @WithMockUser
        @DisplayName("should add participation when user and session exist")
        void participate_shouldAddParticipation() throws Exception {
            User user = new User().setId(1L).setLastName("Boulon");
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSessions.get(0)));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            mockMvc.perform(post("/api/session/1/participate/1"))
                    .andExpect(status().isOk());
            verify(sessionRepository).save(any(Session.class));
        }

        @Test
        @WithMockUser
        @DisplayName("should return not found when user is not found")
        void participate_userNotFound_shouldReturnNotFound() throws Exception {
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSessions.get(0)));
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/session/1/participate/1"))
                    .andExpect(status().isNotFound());
            verify(sessionRepository, never()).save(any(Session.class));
        }

        @Test
        @WithMockUser
        @DisplayName("should return not found when session is not found")
        void participate_sessionNotFound_shouldReturnNotFound() throws Exception {
            User user = new User().setId(1L).setLastName("Boulon");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/session/1/participate/1"))
                    .andExpect(status().isNotFound());
            verify(sessionRepository, never()).save(any(Session.class));
        }

        @Test
        @WithMockUser
        @DisplayName("should return 400 status when user already participates")
        void participate_userAlreadyParticipates_shouldReturnBadRequest() throws Exception {
            User user = new User().setId(1L).setLastName("Boulon");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(sessionRepository.findById(2L)).thenReturn(Optional.of(mockSessions.get(1)));

            mockMvc.perform(post("/api/session/2/participate/1"))
                    .andExpect(status().isBadRequest());
            verify(sessionRepository, never()).save(any(Session.class));

        }
    }

    @Nested
    @DisplayName("No longer participate: DELETE /api/session/{id}/participate/{userId}")
    class NoLongerParticipateIntegrationTest {
        @Test
        @WithMockUser
        @DisplayName("should remove participation")
        void noLongerParticipate_shouldRemoveParticipation() throws Exception {
            User user = new User().setId(1L).setLastName("Boulon");
            when(sessionRepository.findById(2L)).thenReturn(Optional.of(mockSessions.get(1)));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            mockMvc.perform(delete("/api/session/2/participate/1"))
                    .andExpect(status().isOk());
            verify(sessionRepository).save(any(Session.class));
        }

        @Test
        @WithMockUser
        @DisplayName("should return 404 status when session is not found")
        void noLongerParticipate_sessionNotFound_shouldReturnNotFound() throws Exception {
            User user = new User().setId(1L).setLastName("Boulon");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

            mockMvc.perform(delete("/api/session/1/participate/1"))
                    .andExpect(status().isNotFound());
            verify(sessionRepository, never()).save(any(Session.class));
        }

        @Test
        @WithMockUser
        @DisplayName("should return 400 status when user is not participating")
        void noLongerParticipate_userNotParticipating_shouldReturnBadRequest() throws Exception {
            User user = new User().setId(1L).setLastName("Boulon");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSessions.get(0)));

            mockMvc.perform(delete("/api/session/1/participate/1"))
                    .andExpect(status().isBadRequest());
            verify(sessionRepository, never()).save(any(Session.class));
        }
    }
}
