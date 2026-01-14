package dev.danielmesquita.miniauthorizer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.danielmesquita.miniauthorizer.projection.UserDetailsProjection;
import dev.danielmesquita.miniauthorizer.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

  @Mock private UserRepository userRepository;
  @InjectMocks private CustomUserDetailsService service;

  @Test
  void loadUserByUsernameUserFoundReturnsUserDetails() {
    String username = "legituser@trustme.com";
    String password = "reallyTrustworthyPassword";
    UserDetailsProjection projection = mock(UserDetailsProjection.class);
    when(projection.getPassword()).thenReturn(password);
    when(userRepository.findUserDetailsByEmail(username)).thenReturn(List.of(projection));

    UserDetails userDetails = service.loadUserByUsername(username);

    assertEquals(username, userDetails.getUsername());
    assertEquals(password, userDetails.getPassword());
  }

  @Test
  void loadUserByUsernameUserNotFoundThrowsException() {
    String username = "findmeifyoucan@notfound.com";
    when(userRepository.findUserDetailsByEmail(username)).thenReturn(List.of());

    assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(username));
  }
}
