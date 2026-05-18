package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("TestController: integration tests")
public class TeacherControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    protected TeacherRepository teacherRepository;

    protected Teacher savedTeacher;

    @BeforeEach
    void buildMockData() {
        Teacher teacher = Teacher.builder()
                .lastName("Portique")
                .firstName("Miranda")
                .build();
        Teacher teacher2 = Teacher.builder()
                .lastName("Boulon")
                .firstName("Michel")
                .build();
        savedTeacher = teacherRepository.save(teacher);
        teacherRepository.save(teacher2);
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
            mockMvc.perform(get("/api/teacher/" + savedTeacher.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Miranda"))
                    .andExpect(jsonPath("$.lastName").value("Portique"));
        }

        @Test
        @WithMockUser
        @DisplayName("should return not found when teacher does not exist")
        public void findById_sessionNotFound_shouldReturnNotFound() throws Exception {
            mockMvc.perform(get("/api/teacher/999"))
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
            mockMvc.perform(get("/api/teacher"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].firstName").value("Miranda"))
                    .andExpect(jsonPath("$[0].lastName").value("Portique"));
        }
    }
}
