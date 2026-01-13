package dev.danielmesquita.miniauthorizer.service;

import dev.danielmesquita.miniauthorizer.entity.Card;
import dev.danielmesquita.miniauthorizer.repository.CardRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final CardRepository cardRepository;

  public CustomUserDetailsService(CardRepository cardRepository) {
    this.cardRepository = cardRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    Card card =
        cardRepository
            .findByCardNumber(username)
            .orElseThrow(() -> new UsernameNotFoundException("Card not found"));
    return User.builder()
        .username(card.getCardNumber())
        .password(card.getPassword())
        .roles("USER")
        .build();
  }
}
