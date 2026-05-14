package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController: user management endpoints")
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Nested
    @DisplayName("Find by id")
    class FindByIdTest {
        @Test
        @DisplayName("should return user with 200 status")
        void findById_shouldReturnUser() {
            User user = new User().setId(1L);
            UserDto userDto = new UserDto();
            userDto.setId(1L);
            when(userService.findById(1L)).thenReturn(user);
            when(userMapper.toDto(user)).thenReturn(userDto);

            ResponseEntity<?> response = userController.findById("1");
            UserDto body = (UserDto) response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(body.getId()).isEqualTo(1L);
            verify(userService).findById(1L);
            verify(userMapper).toDto(user);
        }

        @Test
        @DisplayName("should return 404 status when user is not found")
        void findById_userNotFound_shouldReturnNotFound() {
            when(userService.findById(1L)).thenReturn(null);
            ResponseEntity<?> response = userController.findById("1");
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            verify(userService).findById(1L);
            verify(userMapper, never()).toDto((User) any());
        }

        @Test
        @DisplayName("should return 400 status when id is invalid")
        void findById_idInvalid_shouldReturnBadRequest() {
            ResponseEntity<?> response = userController.findById("ABC");
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            verify(userService, never()).findById(any());
            verify(userMapper, never()).toDto((User) any());
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTest {

        @AfterEach
        void tearDown() {
            SecurityContextHolder.clearContext();
        }

        @Test
        @DisplayName("should delete user and return 200 status")
        void delete_shouldDeleteUser() {
            User user = new User();
            user.setId(1L);
            user.setEmail("yoga@studio.com");

            UserDetails userDetails = mock(UserDetails.class);
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            when(userService.findById(1L)).thenReturn(user);
            when(userDetails.getUsername()).thenReturn("yoga@studio.com");
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            ResponseEntity<?> response = userController.delete("1");
            verify(userService).delete(1L);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("should return 404 status when user is not found")
        void delete_userNotFound_shouldReturnNotFound() {
            when(userService.findById(1L)).thenReturn(null);
            ResponseEntity<?> response = userController.delete("1");
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            verify(userService).findById(1L);
            verify(userService, never()).delete(1L);
        }

        @Test
        @DisplayName("should return 401 status when authenticated user is not equal to user to delete")
        void delete_idNotEqualToAuthenticatedUser_shouldReturnUnauthorized() {
            User user = new User();
            user.setId(1L);
            user.setEmail("yoga@studio.com");

            UserDetails userDetails = mock(UserDetails.class);
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            when(userService.findById(1L)).thenReturn(user);
            when(userDetails.getUsername()).thenReturn("other@studio.com");
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            ResponseEntity<?> response = userController.delete("1");
            verify(userService, never()).delete(any());
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("should return 400 status when id is invalid")
        void delete_idInvalid_shouldReturnBadRequest() {
            ResponseEntity<?> response = userController.delete("ABC");
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            verify(userService, never()).findById(any());
        }
    }
}
