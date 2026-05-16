package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @InjectMocks
    UserDetailsServiceImpl userDetailsService;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("should return UserDetails when user exists")
    void loadUserByUsername_userExists_shouldReturnUserDetails() {
        User user = new User()
                .setEmail("michel.boulon@laposte.net")
                .setFirstName("Michel")
                .setLastName("Boulon")
                .setPassword("hashed");

        when(userRepository.findByEmail("michel.boulon@laposte.net")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("michel.boulon@laposte.net");
        assertThat(userDetails.getUsername()).isEqualTo("michel.boulon@laposte.net");
        assertThat(userDetails.getPassword()).isEqualTo("hashed");
    }

    @Test
    @DisplayName("should throw UsernameNotFoundException when user not found")
    void loadUserByUsername_userNotFound_shouldThrowException() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() ->
                userDetailsService.loadUserByUsername("unknown@test.com")
        ).isInstanceOf(UsernameNotFoundException.class);
    }
}