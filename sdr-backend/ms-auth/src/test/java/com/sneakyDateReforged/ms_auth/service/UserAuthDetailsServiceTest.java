package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAuthDetailsServiceTest {

    @Mock
    private UserAuthRepository userAuthRepository;

    @InjectMocks
    private UserAuthDetailsService userAuthDetailsService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        // GIVEN
        UserAuthModel user = UserAuthModel.builder()
                .email("test@email.com")
                .password("hashedPassword")
                .build();

        when(userAuthRepository.findByEmail("test@email.com"))
                .thenReturn(Optional.of(user));

        // WHEN
        UserDetails userDetails = userAuthDetailsService.loadUserByUsername("test@email.com");

        // THEN
        assertNotNull(userDetails);
        assertEquals("test@email.com", userDetails.getUsername());
        assertEquals("hashedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenEmailIsNull() {
        assertThrows(UsernameNotFoundException.class, () -> {
            userAuthDetailsService.loadUserByUsername(null);
        });
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        when(userAuthRepository.findByEmail("notfound@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userAuthDetailsService.loadUserByUsername("notfound@email.com");
        });
    }
}
