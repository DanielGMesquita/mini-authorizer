package dev.danielmesquita.miniauthorizer.service;

import dev.danielmesquita.miniauthorizer.exception.UnauthorizedException;
import dev.danielmesquita.miniauthorizer.projection.UserDetailsProjection;
import dev.danielmesquita.miniauthorizer.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public CustomAuthenticationProvider(
      UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = Objects.requireNonNull(authentication.getCredentials()).toString();

    List<UserDetailsProjection> result = userRepository.findUserDetailsByEmail(username);
    if (result.isEmpty()) {
      throw new UnauthorizedException("Invalid username or password");
    }

    String storedPassword = result.getFirst().getPassword();
    if (!passwordEncoder.matches(password, storedPassword)) {
      throw new UnauthorizedException("Invalid username or password");
    }

    return new UsernamePasswordAuthenticationToken(username, password, List.of());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
