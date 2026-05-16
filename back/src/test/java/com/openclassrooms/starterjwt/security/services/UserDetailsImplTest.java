package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTest {

    private UserDetailsImpl user;

    @BeforeEach
    void buildMockData() {
        user = UserDetailsImpl.builder()
                .id(1L)
                .username("michel.boulon@laposte.net")
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
        @DisplayName("should be equal with same instance")
        void equals_sameInstance_shouldReturnTrue() {
            assertThat(user.equals(user)).isTrue();
        }

        @Test
        @DisplayName("should be equal with same id")
        void equals_sameId_shouldReturnTrue() {
            UserDetailsImpl user2 = UserDetailsImpl.builder().id(1L).build();
            assertThat(user.equals(user2)).isTrue();
        }

        @Test
        @DisplayName("should not be equal with different id")
        void equals_differentId_shouldReturnFalse() {
            UserDetailsImpl user2 = UserDetailsImpl.builder().id(2L).build();
            assertThat(user.equals(user2)).isFalse();
        }

        @Test
        @DisplayName("should not be equal with null")
        void equals_null_shouldReturnFalse() {
            assertThat(user.equals(null)).isFalse();
        }

        @Test
        @DisplayName("should not be equal with different types")
        void equals_differentType_shouldReturnFalse() {
            assertThat(user.equals("not a UserDetailsImpl")).isFalse();
        }
    }

    @Nested
    @DisplayName("UserDetails interface methods")
    class UserDetailsMethodsTest {

        @Test
        @DisplayName("getAuthorities: should return empty collection")
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