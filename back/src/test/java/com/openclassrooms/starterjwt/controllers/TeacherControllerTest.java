package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeacherController: teacher management endpoints")
public class TeacherControllerTest {

    @InjectMocks
    private TeacherController teacherController;

    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherMapper teacherMapper;

    @Nested
    @DisplayName("Find by id")
    class FindByIdTest {
        @Test
        @DisplayName("should return teacher with 200 status")
        void findById_shouldReturnTeacher() {
            Teacher teacher = new Teacher().setId(1L);
            TeacherDto teacherDto = new TeacherDto();
            teacherDto.setId(1L);

            when(teacherService.findById(1L)).thenReturn(teacher);
            when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

            ResponseEntity<?> response = teacherController.findById("1");
            TeacherDto body = (TeacherDto) response.getBody();
            verify(teacherService).findById(1L);
            verify(teacherMapper).toDto(teacher);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(body).isNotNull();
            assertThat(body.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should return 404 status when teacher is not found")
        void findById_teacherNotFound_shouldReturnNotFound() {
            when(teacherService.findById(1L)).thenReturn(null);

            ResponseEntity<?> response = teacherController.findById("1");
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            verify(teacherService).findById(1L);
            verify(teacherMapper, never()).toDto((Teacher) any());
        }

        @Test
        @DisplayName("should return 400 when id is invalid")
        void findById_idInvalid_shouldReturnBadRequest() {
            ResponseEntity<?> response = teacherController.findById("ABC");
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            verify(teacherService, never()).findById(any());
            verify(teacherMapper, never()).toDto((Teacher) any());
        }
    }

    @Nested
    @DisplayName("Find all")
    class FindAllTest {
        @Test
        @DisplayName("should return all teachers")
        void findAll_shouldReturnAllTeachers() {
            List<Teacher> teachers = new ArrayList<>();
            teachers.add(new Teacher().setId(1L));
            teachers.add(new Teacher().setId(2L));

            List<TeacherDto> teacherDtos = new ArrayList<>();
            TeacherDto teacherDto1 = new TeacherDto();
            teacherDto1.setId(1L);
            teacherDtos.add(teacherDto1);
            TeacherDto teacherDto2 = new TeacherDto();
            teacherDto2.setId(2L);
            teacherDtos.add(teacherDto2);

            when(teacherService.findAll()).thenReturn(teachers);
            when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

            ResponseEntity<?> response = teacherController.findAll();
            List<TeacherDto> body = (List<TeacherDto>) response.getBody();

            verify(teacherService).findAll();
            verify(teacherMapper).toDto(teachers);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(body).hasSize(2)
                    .extracting(TeacherDto::getId)
                    .containsExactly(1L, 2L);
        }
    }
}
