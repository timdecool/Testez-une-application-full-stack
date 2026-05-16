package com.openclassrooms.starterjwt.mappers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserMapperTest {

    @Autowired
    UserMapper userMapper;

    @MockBean
    UserRepository userRepository;

    private User mockUser;
    private UserDto mockUserDto;

    @BeforeEach
    void buildMockData() {
        mockUser =  User.builder()
                .id(1L)
                .firstName("Michel")
                .lastName("Boulon")
                .email("michel.boulon@laposte.net")
                .password("password")
                .createdAt(LocalDateTime.of(2026, 5, 15, 0, 0))
                .updatedAt(LocalDateTime.of(2026, 5, 15, 0, 0))
                .admin(false)
                .build();

        mockUserDto = new UserDto();
        mockUserDto.setId(1L);
        mockUserDto.setEmail("michel.boulon@laposte.net");
        mockUserDto.setFirstName("Michel");
        mockUserDto.setLastName("Boulon");
        mockUserDto.setPassword("password");
        mockUserDto.setAdmin(false);
    }

    @Test
    @DisplayName("toDto: should map session fields")
    void toDto_shouldMapAllFields() {
        UserDto dto = userMapper.toDto(mockUser);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isEqualTo("Michel");
        assertThat(dto.getLastName()).isEqualTo("Boulon");
        assertThat(dto.getPassword()).isEqualTo("password");
        assertThat(dto.getEmail()).isEqualTo("michel.boulon@laposte.net");
        assertThat(dto.isAdmin()).isFalse();
        assertThat(dto.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 5, 15, 0, 0));
        assertThat(dto.getUpdatedAt()).isEqualTo(LocalDateTime.of(2026, 5, 15, 0, 0));
    }

    @Test
    @DisplayName("toDto: null session should return null")
    void toDto_nullSession_shouldReturnNull() {
        assertThat(userMapper.toDto((User) null)).isNull();
    }

    @Test
    @DisplayName("toDto: should map entity list")
    void toDto_shouldMapList() {
        List<User> users = new ArrayList<>();
        users.add(mockUser);
        List<UserDto> dtos = userMapper.toDto(users);
        assertThat(dtos).hasSize(1).extracting(UserDto::getId).containsExactly(1L);
    }

    @Test
    @DisplayName("toEntity: should map dto fields correctly")
    void toEntity_shouldMapAllFields() {
        User user = userMapper.toEntity(mockUserDto);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getFirstName()).isEqualTo(("Michel"));
        assertThat(user.getLastName()).isEqualTo(("Boulon"));
        assertThat(user.getEmail()).isEqualTo(("michel.boulon@laposte.net"));
        assertThat(user.getPassword()).isEqualTo(("password"));
    }

    @Test
    @DisplayName("toEntity: null dto should return null")
    void toEntity_nullDto_shouldReturnNull() {
        assertThat(userMapper.toEntity((UserDto) null)).isNull();
    }

    @Test
    @DisplayName("toEntity: should map dto list")
    void toEntity_shouldMapList() {
        List<UserDto> userDtos = new ArrayList<>();
        userDtos.add(mockUserDto);

        List<User> users = userMapper.toEntity(userDtos);
        assertThat(users).hasSize(1).extracting(User::getId).containsExactly(1L);
    }
}