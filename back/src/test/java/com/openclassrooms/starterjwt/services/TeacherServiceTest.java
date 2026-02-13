package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    TeacherRepository teacherRepository;

    @InjectMocks
    TeacherService teacherService;

    @Test
    public void findById_correctId_returnsTeacher() {
        Teacher teacher = new Teacher()
                .setId(1L)
                .setLastName("BOULON")
                .setFirstName("Michel");
        when(teacherRepository.findById(1L)).thenReturn(Optional.ofNullable(teacher));

        Teacher foundTeacher = teacherService.findById(1L);

        Assertions.assertThat(foundTeacher).isEqualTo(teacher);
    }

    @Test
    public void findById_incorrectId_returnsNull() {
        when(teacherRepository.findById(2L)).thenReturn(Optional.empty());

        Teacher foundTeacher = teacherService.findById(2L);

        Assertions.assertThat(foundTeacher).isNull();
    }

    @Test
    public void findAll_returnsTeacherList() {
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(new Teacher().setId(1L).setLastName("BOULON").setFirstName("Michel"));
        teachers.add(new Teacher().setId(2L).setLastName("PORTIQUE").setFirstName("Miranda"));
        teachers.add(new Teacher().setId(3L).setLastName("DECOOL").setFirstName("Timothé"));
        when(teacherRepository.findAll()).thenReturn(teachers);

        List<Teacher> allTeachers = teacherService.findAll();

        Assertions.assertThat(allTeachers.size()).isEqualTo(3);
    }

}