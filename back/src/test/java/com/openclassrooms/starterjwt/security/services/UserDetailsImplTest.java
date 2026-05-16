package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTest {

    private UserDetailsImpl user;

    @BeforeEach
    void setUp() {
        user = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Michel")
                .lastName("Boulon")
                .password("encoded")
                .admin(false)
                .build();
    }

    @Nested
    @DisplayName("equals")
    class EqualsTest {

        @Test
        @DisplayName("same instance should be equal")
        void equals_sameInstance_shouldReturnTrue() {
            assertThat(user.equals(user)).isTrue();
        }

        @Test
        @DisplayName("same id should be equal")
        void equals_sameId_shouldReturnTrue() {
            UserDetailsImpl user2 = UserDetailsImpl.builder().id(1L).build();
            assertThat(user.equals(user2)).isTrue();
        }

        @Test
        @DisplayName("different id should not be equal")
        void equals_differentId_shouldReturnFalse() {
            UserDetailsImpl user2 = UserDetailsImpl.builder().id(2L).build();
            assertThat(user.equals(user2)).isFalse();
        }

        @Test
        @DisplayName("null should not be equal")
        void equals_null_shouldReturnFalse() {
            assertThat(user.equals(null)).isFalse();
        }

        @Test
        @DisplayName("different type should not be equal")
        void equals_differentType_shouldReturnFalse() {
            assertThat(user.equals("not a UserDetailsImpl")).isFalse();
        }
    }

    @Nested
    @DisplayName("UserDetails interface methods")
    class UserDetailsMethodsTest {

        @Test
        @DisplayName("getAuthorities should return empty collection")
        void getAuthorities_shouldReturnEmptyCollection() {
            assertThat(user.getAuthorities()).isEmpty();
        }

        @Test
        @DisplayName("account should be non expired")
        void isAccountNonExpired_shouldReturnTrue() {
            assertThat(user.isAccountNonExpired()).isTrue();
        }

        @Test
        @DisplayName("account should be non locked")
        void isAccountNonLocked_shouldReturnTrue() {
            assertThat(user.isAccountNonLocked()).isTrue();
        }

        @Test
        @DisplayName("credentials should be non expired")
        void isCredentialsNonExpired_shouldReturnTrue() {
            assertThat(user.isCredentialsNonExpired()).isTrue();
        }

        @Test
        @DisplayName("account should be enabled")
        void isEnabled_shouldReturnTrue() {
            assertThat(user.isEnabled()).isTrue();
        }
    }
}