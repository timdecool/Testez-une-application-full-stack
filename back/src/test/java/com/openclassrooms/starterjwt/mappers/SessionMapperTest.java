package com.openclassrooms.starterjwt.mappers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.mapper.SessionMapperImpl;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SessionMapperTest {

    @Autowired
    SessionMapper sessionMapper;

    @MockBean
    TeacherService teacherService;

    @MockBean
    UserService userService;

    @MockBean
    SessionRepository sessionRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    TeacherRepository teacherRepository;
    @Test
    @DisplayName("toDto: should map session fields")
    void toDto_shouldMapAllFields() {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("Miranda")
                .lastName("Portique")
                .build();

        User user = new User().setId(1L);
        List<User> users = new ArrayList<>();
        users.add(user);
        Session session = Session.builder()
                .id(1L)
                .name("Yoga du soir")
                .description("Test")
                .date(new Date())
                .teacher(teacher)
                .users(users)
                .build();

        SessionDto dto = sessionMapper.toDto(session);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Yoga du soir");
        assertThat(dto.getDescription()).isEqualTo("Test");
        assertThat(dto.getTeacher_id()).isEqualTo(1L);
        assertThat(dto.getUsers())
                .hasSize(1)
                .containsExactly(1L);
    }

    @Test
    @DisplayName("toDto: null session should return null")
    void toDto_nullSession_shouldReturnNull() {
        assertThat(sessionMapper.toDto((Session) null)).isNull();
    }

    @Test
    @DisplayName("toDto: should map entity list")
    void toDto_shouldMapList() {
        List<Session> sessions = new ArrayList<>();
        sessions.add(new Session().setId(1L));
        sessions.add(new Session().setId(2L));
        List<SessionDto> dtos = sessionMapper.toDto(sessions);
        assertThat(dtos).hasSize(2).extracting(SessionDto::getId).containsExactly(1L, 2L);
    }

    @Test
    @DisplayName("toEntity: should map dto fields correctly")
    void toEntity_shouldMapAllFields() {
        SessionDto dto = new SessionDto();
        dto.setId(1L);
        dto.setName("Yoga du soir");
        dto.setTeacher_id(1L);

        Session session = sessionMapper.toEntity(dto);

        assertThat(session.getId()).isEqualTo(1L);
        assertThat(session.getName()).isEqualTo("Yoga du soir");
    }

    @Test
    @DisplayName("toEntity: null dto should return null")
    void toEntity_nullDto_shouldReturnNull() {
        assertThat(sessionMapper.toEntity((SessionDto) null)).isNull();
    }

    @Test
    @DisplayName("toEntity: should map dto list")
    void toEntity_shouldMapList() {
        List<SessionDto> sessionDtos = new ArrayList<>();
        SessionDto dto = new SessionDto();
        dto.setId(1L);
        SessionDto dto2 = new SessionDto();
        dto2.setId(2L);
        sessionDtos.add(dto);
        sessionDtos.add(dto2);

        List<Session> sessions = sessionMapper.toEntity(sessionDtos);
        assertThat(sessions).hasSize(2).extracting(Session::getId).containsExactly(1L, 2L);
    }
}