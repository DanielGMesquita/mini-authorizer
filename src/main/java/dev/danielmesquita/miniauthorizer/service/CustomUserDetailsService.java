package dev.danielmesquita.miniauthorizer.service;

import dev.danielmesquita.miniauthorizer.entity.User;
import dev.danielmesquita.miniauthorizer.projection.UserDetailsProjection;
import dev.danielmesquita.miniauthorizer.repository.UserRepository;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    List<UserDetailsProjection> result = userRepository.findUserDetailsByEmail(username);
    if (result.isEmpty()) {
      throw new UsernameNotFoundException("User not found with email: " + username);
    }
    User user = new User();
    user.setEmail(username);
    user.setPassword(result.getFirst().getPassword());

    return user;
  }
}
