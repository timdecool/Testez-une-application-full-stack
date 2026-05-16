package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService: handle yoga sessions")
class SessionServiceTest {

    @InjectMocks
    SessionService sessionService;

    @Mock
    SessionRepository sessionRepository;

    @Mock
    UserRepository userRepository;

    @Captor
    ArgumentCaptor<Session> sessionCaptor;

    @Nested
    @DisplayName("sessionService crud")
    class SessionCrudTest {
        @Test
        @DisplayName("create: should save session and return it")
        public void create_shouldReturnSavedSession() {
            Session newSession = new Session()
                    .setName("Yoga du matin");
            Session createdSession = new Session()
                    .setId(1L)
                    .setName("Yoga du matin");
            when(sessionRepository.save(newSession)).thenReturn(createdSession);

            Session actualSession = sessionService.create(newSession);

            verify(sessionRepository, times(1)).save(newSession);
            assertThat(actualSession).isEqualTo(createdSession);
        }

        @Test
        @DisplayName("delete: should delete session")
        public void delete_shouldDeleteSession() {
            sessionService.delete(1L);
            verify(sessionRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("findAll: should return all saved sessions")
        public void findAll_shouldReturnAllSessions() {
            List<Session> sessions = new ArrayList<>();
            sessions.add(new Session().setId(1L));
            sessions.add(new Session().setId(2L));
            sessions.add(new Session().setId(3L));
            when(sessionRepository.findAll()).thenReturn(sessions);

            List<Session> foundSessions = sessionService.findAll();
            verify(sessionRepository, times(1)).findAll();
            assertThat(foundSessions)
                    .hasSize(3)
                    .extracting(Session::getId)
                    .containsExactly(1L, 2L, 3L);
        }

        @Test
        @DisplayName("getById: when session exists, should return it")
        public void getById_sessionFound_shouldReturnSession() {
            Session session = new Session().setId(1L);
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

            Session foundSession = sessionService.getById(1L);
            assertThat(foundSession.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("getById: when session does not exist, should return null")
        public void getById_sessionNotFound_shouldReturnNull() {
            when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

            Session foundSession = sessionService.getById(1L);
            assertThat(foundSession).isNull();
        }

        @Test
        @DisplayName("update: should update session with given id and return it")
        public void update_shouldReturnSavedSession() {
            Session updatedSession = new Session()
                    .setId(1L)
                    .setName("Yoga du soir");
            when(sessionRepository.save(any(Session.class))).thenReturn(updatedSession);

            Session result = sessionService.update(1L, updatedSession);

            verify(sessionRepository, times(1)).save(updatedSession);
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Yoga du soir");
        }
    }
    @Nested
    @DisplayName("participate: add user to session")
    class ParticipateTest {
        @Test
        @DisplayName("should add the user and save the session when user is not already in session")
        public void participate_newParticipant_shouldSaveSession() {
            User user = new User().setId(1L);
            Session session = new Session()
                    .setId(1L)
                    .setUsers(new ArrayList<>());
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            sessionService.participate(1L, 1L);

            verify(sessionRepository).save(sessionCaptor.capture());
            Session savedSession = sessionCaptor.getValue();

            assertThat(savedSession.getUsers())
                    .hasSize(1)
                    .contains(user);
        }

        @Test
        @DisplayName("should throw BadRequestException when user is already participating")
        public void participate_alreadyParticipant_shouldThrowBadRequestException() {
            User user = new User().setId(1L);
            List<User> participants = new ArrayList<>();
            participants.add(user);
            Session session = new Session()
                    .setId(1L)
                    .setUsers(participants);
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> sessionService.participate(1L, 1L))
                    .isInstanceOf(BadRequestException.class);
            verify(sessionRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw NotFoundException when session does not exist")
        public void participate_sessionNotFound_shouldThrowNotFoundException() {
            when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> sessionService.participate(1L, 1L))
                    .isInstanceOf(NotFoundException.class);
            verify(sessionRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw NotFoundException when user does not exist")
        public void participate_userNotFound_shouldThrowNotFoundException() {
            Session session = new Session()
                    .setId(1L)
                    .setUsers(new ArrayList<>());
            when(userRepository.findById(1L)).thenReturn(Optional.empty());
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

            assertThatThrownBy(() -> sessionService.participate(1L, 1L))
                    .isInstanceOf(NotFoundException.class);
            verify(sessionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("noLongerParticipate: remove participating user from session")
    class NoLongerParticipateTest {
        @Test
        @DisplayName("should remove user and save session when session exists and user participates")
        public void noLongerParticipate_userParticipating_shouldSaveSession() {
            List<User> participants = new ArrayList<>();
            participants.add(new User().setId(1L));
            participants.add(new User().setId(2L));
            Session session = new Session()
                    .setId(1L)
                    .setUsers(participants);

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

            sessionService.noLongerParticipate(1L, 1L);

            verify(sessionRepository).save(sessionCaptor.capture());
            Session savedSession = sessionCaptor.getValue();
            assertThat(savedSession.getUsers())
                    .hasSize(1)
                    .doesNotContain(new User().setId(1L));
        }

        @Test
        @DisplayName("should throw BadRequestException when given user id is not in session participants")
        public void noLongerParticipate_userNotParticipating_shouldThrowBadRequestException() {
            List<User> participants = new ArrayList<>();
            participants.add(new User().setId(2L));
            Session session = new Session()
                    .setId(1L)
                    .setUsers(participants);

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

            assertThatThrownBy(() -> sessionService.noLongerParticipate(1L, 1L))
                    .isInstanceOf(BadRequestException.class);
            verify(sessionRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw NotFoundException when user does not exist")
        public void noLongerParticipate_userNotFound_shouldThrowNotFoundException() {
            when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> sessionService.noLongerParticipate(1L, 1L))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("should throw NotFoundException when session does not exist")
        public void noLongerParticipate_sessionNotFound_shouldThrowNotFoundException() {
            when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> sessionService.noLongerParticipate(1L, 1L))
                    .isInstanceOf(NotFoundException.class);
            verify(sessionRepository, never()).save(any());
        }
    }
}