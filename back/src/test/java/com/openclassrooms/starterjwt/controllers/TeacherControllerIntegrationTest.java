package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TestController: integration tests")
public class TeacherControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    protected TeacherRepository teacherRepository;

    protected List<Teacher> mockTeachers;

    @BeforeEach
    void buildMockData() {
        Teacher mockTeacher = Teacher.builder()
                .id(1L)
                .lastName("Portique")
                .firstName("Miranda")
                .build();
        Teacher mockTeacher2 = Teacher.builder()
                .id(2L)
                .lastName("Boulon")
                .firstName("Michel")
                .build();
        mockTeachers = new ArrayList<>();
        mockTeachers.add(mockTeacher);
        mockTeachers.add(mockTeacher2);
    }

    @Nested
    @DisplayName("Find by id integration: GET /api/session/{id}")
    class FindByIdIntegrationClass {
        @Test
        @DisplayName("should return 401 when user is not authenticated")
        void findById_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/api/teacher/1")).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        @DisplayName("should return mapped teacher")
        public void findById_shouldReturnSession() throws Exception {
            when(teacherRepository.findById(1L)).thenReturn(Optional.of(mockTeachers.get(0)));
            mockMvc.perform(get("/api/teacher/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.lastName").value("Portique"));
        }

        @Test
        @WithMockUser
        @DisplayName("should return not found when teacher does not exist")
        public void findById_sessionNotFound_shouldReturnNotFound() throws Exception {
            when(teacherRepository.findById(1L)).thenReturn(Optional.empty());
            mockMvc.perform(get("/api/teacher/1"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Find all integration: GET /api/teacher")
    class FindAllIntegrationClass {
        @Test
        @DisplayName("should return 401 when user is not authenticated")
        void findByAll_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/api/teacher")).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        @DisplayName("should return all teachers")
        void findAll_shouldReturnAll() throws Exception {
            when(teacherRepository.findAll()).thenReturn(mockTeachers);
            mockMvc.perform(get("/api/teacher"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].lastName").value("Portique"));
        }
    }
}
