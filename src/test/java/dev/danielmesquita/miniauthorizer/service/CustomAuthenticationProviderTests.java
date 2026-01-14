package dev.danielmesquita.miniauthorizer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.danielmesquita.miniauthorizer.exception.UnauthorizedException;
import dev.danielmesquita.miniauthorizer.projection.UserDetailsProjection;
import dev.danielmesquita.miniauthorizer.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class CustomAuthenticationProviderTests {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @InjectMocks private CustomAuthenticationProvider provider;

  @Test
  void authenticateSuccess() {
    String username = "user@example.com";
    String password = "password";
    UserDetailsProjection projection = mock(UserDetailsProjection.class);
    when(projection.getPassword()).thenReturn("hashed");
    when(userRepository.findUserDetailsByEmail(username)).thenReturn(List.of(projection));
    when(passwordEncoder.matches(password, "hashed")).thenReturn(true);

    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(username, password);

    assertDoesNotThrow(() -> provider.authenticate(auth));
  }

  @Test
  void authenticateUserNotFoundThrowsUnauthorized() {
    String username = "notfound@example.com";
    String password = "password";
    when(userRepository.findUserDetailsByEmail(username)).thenReturn(List.of());

    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(username, password);

    assertThrows(UnauthorizedException.class, () -> provider.authenticate(auth));
  }

  @Test
  void authenticateWrongPasswordThrowsUnauthorized() {
    String username = "user@example.com";
    String password = "wrong";
    UserDetailsProjection projection = mock(UserDetailsProjection.class);
    when(projection.getPassword()).thenReturn("hashed");
    when(userRepository.findUserDetailsByEmail(username)).thenReturn(List.of(projection));
    when(passwordEncoder.matches(password, "hashed")).thenReturn(false);

    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(username, password);

    assertThrows(UnauthorizedException.class, () -> provider.authenticate(auth));
  }

  @Test
  void supportsReturnsTrueForUsernamePasswordAuthenticationToken() {
    assertTrue(provider.supports(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  void supportsReturnsFalseForOtherClass() {
    assertFalse(provider.supports(String.class));
  }
}
