package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService: handle user management")
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Nested
    @DisplayName("delete")
    class DeleteTest {
        @Test
        @DisplayName("delete: should delete user with given id")
        public void delete_shouldDelete() {
            userService.delete(1L);
            verify(userRepository, times(1)).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("find by id")
    class FindByIdTest {
        @Test
        @DisplayName("findById: when user exists, should return it")
        public void findById_userFound_shouldReturnUser() {
            User user = new User().setId(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            User foundUser = userService.findById(1L);
            verify(userRepository, times(1)).findById(1L);
            assertThat(foundUser.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("findById: when user does not exist, should return null")
        public void findById_userNotFound_shouldReturnNull() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            User foundUser = userService.findById(1L);
            verify(userRepository, times(1)).findById(1L);
            assertThat(foundUser).isNull();
        }


    }
}