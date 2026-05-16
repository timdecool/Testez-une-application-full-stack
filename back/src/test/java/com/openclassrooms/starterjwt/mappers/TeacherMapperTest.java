package com.openclassrooms.starterjwt.mappers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TeacherMapperTest {

    @Autowired
    TeacherMapper teacherMapper;

    @MockBean
    TeacherRepository teacherRepository;

    private Teacher mockTeacher;
    private TeacherDto mockTeacherDto;

    @BeforeEach
    void buildMockData() {
        mockTeacher =  Teacher.builder()
                .id(1L)
                .firstName("Michel")
                .lastName("Boulon")
                .createdAt(LocalDateTime.of(2026, 5, 15, 0, 0))
                .updatedAt(LocalDateTime.of(2026, 5, 15, 0, 0))
                .build();

        mockTeacherDto = new TeacherDto();
        mockTeacherDto.setId(1L);
        mockTeacherDto.setFirstName("Michel");
        mockTeacherDto.setLastName("Boulon");
        mockTeacherDto.setCreatedAt(LocalDateTime.of(2026, 5, 15, 0, 0));
        mockTeacherDto.setUpdatedAt(LocalDateTime.of(2026, 5, 15, 0, 0));
    }

    @Nested
    @DisplayName("to dto")
    class ToDtoTest {
        @Test
        @DisplayName("should map session fields")
        void toDto_shouldMapAllFields() {
            TeacherDto dto = teacherMapper.toDto(mockTeacher);
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getFirstName()).isEqualTo("Michel");
            assertThat(dto.getLastName()).isEqualTo("Boulon");
            assertThat(dto.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 5, 15, 0, 0));
            assertThat(dto.getUpdatedAt()).isEqualTo(LocalDateTime.of(2026, 5, 15, 0, 0));
        }

        @Test
        @DisplayName("null session should return null")
        void toDto_nullSession_shouldReturnNull() {
            assertThat(teacherMapper.toDto((Teacher) null)).isNull();
        }

        @Test
        @DisplayName("should map entity list")
        void toDto_shouldMapList() {
            List<Teacher> teachers = new ArrayList<>();
            teachers.add(mockTeacher);
            List<TeacherDto> dtos = teacherMapper.toDto(teachers);
            assertThat(dtos).hasSize(1).extracting(TeacherDto::getId).containsExactly(1L);
        }
    }

    @Nested
    @DisplayName("to entity")
    class ToEntityTest {
        @Test
        @DisplayName("should map dto fields correctly")
        void toEntity_shouldMapAllFields() {
            Teacher teacher = teacherMapper.toEntity(mockTeacherDto);

            assertThat(teacher.getId()).isEqualTo(1L);
            assertThat(teacher.getFirstName()).isEqualTo(("Michel"));
            assertThat(teacher.getLastName()).isEqualTo(("Boulon"));
            assertThat(teacher.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 5, 15, 0, 0));
            assertThat(teacher.getUpdatedAt()).isEqualTo(LocalDateTime.of(2026, 5, 15, 0, 0));
        }

        @Test
        @DisplayName("toEntity: null dto should return null")
        void toEntity_nullDto_shouldReturnNull() {
            assertThat(teacherMapper.toEntity((TeacherDto) null)).isNull();
        }

        @Test
        @DisplayName("toEntity: should map dto list")
        void toEntity_shouldMapList() {
            List<TeacherDto> teacherDtos = new ArrayList<>();
            teacherDtos.add(mockTeacherDto);

            List<Teacher> users = teacherMapper.toEntity(teacherDtos);
            assertThat(users).hasSize(1).extracting(Teacher::getId).containsExactly(1L);
        }
    }
}