package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionController: session management endpoints")
public class SessionControllerTest {

    @InjectMocks
    private SessionController sessionController;

    @Mock
    private SessionMapper sessionMapper;

    @Mock
    private SessionService sessionService;

    @Nested
    @DisplayName("Find by id")
    class FindByIdTest {

        @Test
        @DisplayName("should return session with 200 status when id exists")
        void findById_idExists_shouldReturnSession() {
            Session session = new Session()
                    .setId(1L)
                    .setName("Yoga du soir")
                    ;

            SessionDto sessionDto = new SessionDto();
            sessionDto.setId(1L);
            sessionDto.setName("Yoga du soir");

            when(sessionService.getById(1L)).thenReturn(session);
            when(sessionMapper.toDto(session)).thenReturn(sessionDto);

            ResponseEntity<?> response = sessionController.findById("1");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            SessionDto body = (SessionDto) response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getId()).isEqualTo(1L);
            assertThat(body.getName()).isEqualTo("Yoga du soir");
            verify(sessionService).getById(1L);
            verify(sessionMapper).toDto(session);
        }

        @Test
        @DisplayName("should return 404 status when session is not found")
        void findById_sessionNotFound_shouldReturnNotFoundStatus() {
            when(sessionService.getById(1L)).thenReturn(null);
            ResponseEntity<?> response = sessionController.findById("1");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNull();
            verify(sessionMapper, never()).toDto((Session) any());
        }

        @Test
        @DisplayName("should return 400 status when requested id is invalid")
        void findById_idInvalid_shouldReturnBadRequestStatus() {
            ResponseEntity<?> response = sessionController.findById("ABC");
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNull();

        }
    }

    @Nested
    @DisplayName("Find all")
    class FindAllTest {

        @Test
        @DisplayName("Should return all sessions")
        void findAll_shouldReturnAllSessions() {
            List<Session> sessions = new ArrayList<>();
            sessions.add(new Session().setId(1L));
            sessions.add(new Session().setId(2L));
            when(sessionService.findAll()).thenReturn(sessions);

            List<SessionDto> sessionDtos = new ArrayList<>();
            SessionDto session1 = new SessionDto();
            session1.setId(1L);
            sessionDtos.add(session1);
            SessionDto session2 = new SessionDto();
            session1.setId(2L);
            sessionDtos.add(session2);
            when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

            ResponseEntity<?> response = sessionController.findAll();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(sessionDtos);
        }
    }

    @Nested
    @DisplayName("Create")
    class CreateTest {
        @Test
        @DisplayName("should return created session when dto is valid")
        void create_validDto_shouldReturnCreatedSession() {
            SessionDto requestSession = new SessionDto();
            requestSession.setName("Yoga du soir");

            Session session = new Session().setName("Yoga du soir");
            SessionDto responseSession = new SessionDto();
            responseSession.setName("Yoga du soir");
            responseSession.setId(1L);

            when(sessionMapper.toEntity(requestSession)).thenReturn(session);
            when(sessionService.create(session)).thenReturn(session);
            when(sessionMapper.toDto(session)).thenReturn(responseSession);

            ResponseEntity<?> response = sessionController.create(requestSession);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(responseSession);
            verify(sessionService).create(session);
        }
    }

    @Nested
    @DisplayName("Update")
    class UpdateTest {
        @Test
        @DisplayName("should update session and return dto")
        void update_shouldReturnSession() {
            SessionDto requestSession = new SessionDto();
            requestSession.setName("Yoga du soir");

            Session session = new Session().setName("Yoga du soir");
            SessionDto responseSession = new SessionDto();
            responseSession.setName("Yoga du soir");
            responseSession.setId(1L);

            when(sessionMapper.toEntity(requestSession)).thenReturn(session);
            when(sessionService.update(1L, session)).thenReturn(session);
            when(sessionMapper.toDto(session)).thenReturn(responseSession);

            ResponseEntity<?> response = sessionController.update("1", requestSession);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(responseSession);
            verify(sessionService).update(1L, session);
        }

        @Test
        @DisplayName("should return 400 status when given id is invalid")
        void update_idInvalid_shouldReturnBadRequest() {
            SessionDto sessionDto = new SessionDto();
            ResponseEntity<?> response = sessionController.update("ABC", sessionDto);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNull();
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTest {

        @Test
        @DisplayName("should delete session when id is valid")
        void delete_idExists_shouldDelete() {
            Session session = new Session().setId(1L);
            when(sessionService.getById(1L)).thenReturn(session);

            ResponseEntity<?> response = sessionController.delete("1");
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(sessionService).delete(1L);
        }

        @Test
        @DisplayName("should return 404 status when session is not found")
        void delete_sessionNotFound_shouldReturnNotFound() {
            when(sessionService.getById(1L)).thenReturn(null);

            ResponseEntity<?> response = sessionController.delete("1");
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNull();
            verify(sessionService, never()).delete(any());
        }

        @Test
        @DisplayName("should return 400 status when id is invalid")
        void delete_idInvalid_shouldReturnBadRequest() {
            ResponseEntity<?> response = sessionController.delete("ABC");
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            verify(sessionService, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Participate")
    class ParticipateTest {
        @Test
        @DisplayName("should add participation and return 200")
        void participate_shouldAddParticipation() {
            ResponseEntity<?> response = sessionController.participate("1", "1");
            verify(sessionService).participate(1L, 1L);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("should return 400 status when user id is invalid")
        void participate_userIdInvalid_shouldReturnBadRequest() {
            ResponseEntity<?> response = sessionController.participate("1", "ABC");
            verify(sessionService, never()).participate(any(), any());
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("should return 400 status when session id is invalid")
        void participate_sessionIdInvalid_shouldReturnBadRequest() {
            ResponseEntity<?> response = sessionController.participate("ABC", "1");
            verify(sessionService, never()).participate(any(), any());
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("No longer participate")
    class NoLongerParticipateTest {
        @Test
        @DisplayName("should remove participation and return 200")
        void noLongerParticipate_shouldRemoveParticipation() {
            ResponseEntity<?> response = sessionController.noLongerParticipate("1", "1");
            verify(sessionService).noLongerParticipate(1L, 1L);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("should return 400 status when user id is invalid")
        void noLongerParticipate_userIdInvalid_shouldReturnBadRequest() {
            ResponseEntity<?> response = sessionController.noLongerParticipate("1", "ABC");
            verify(sessionService, never()).noLongerParticipate(any(), any());
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("should return 400 status when session id is invalid")
        void noLongerParticipate_sessionIdInvalid_shouldReturnBadRequest() {
            ResponseEntity<?> response = sessionController.noLongerParticipate("ABC", "1");
            verify(sessionService, never()).noLongerParticipate(any(), any());
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}
