package dev.danielmesquita.miniauthorizer.service;

import dev.danielmesquita.miniauthorizer.dto.CardDTO;
import dev.danielmesquita.miniauthorizer.entity.Card;
import dev.danielmesquita.miniauthorizer.enums.TransactionStatus;
import dev.danielmesquita.miniauthorizer.exception.CardAlreadyExistsException;
import dev.danielmesquita.miniauthorizer.exception.ResourceNotFoundException;
import dev.danielmesquita.miniauthorizer.exception.TransactionException;
import dev.danielmesquita.miniauthorizer.repository.CardRepository;
import java.math.BigDecimal;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardService {

  private final CardRepository repository;

  public CardService(CardRepository repository) {
    this.repository = repository;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Transactional
  public CardDTO createCard(CardDTO cardDTO) {
    boolean exists = repository.findByCardNumber(cardDTO.getCardNumber()).isPresent();
    if (exists) {
      throw new CardAlreadyExistsException(
          "Card with number " + cardDTO.getCardNumber() + " already exists.");
    }

    Card entity = new Card();
    entity.setCardHolderName(cardDTO.getCardHolderName());
    entity.setCardNumber(cardDTO.getCardNumber());
    entity.setPassword(passwordEncoder().encode(cardDTO.getPassword()));
    entity.setBalance(new BigDecimal("0")); // Initial balance

    entity = repository.save(entity);

    return new CardDTO(entity);
  }

  @Transactional(readOnly = true)
  public BigDecimal getBalance(String cardNumber) {
    Card card =
        repository
            .findByCardNumber(cardNumber)
            .orElseThrow(
                () -> new ResourceNotFoundException("Card not found with number: " + cardNumber));
    return card.getBalance();
  }

  @Transactional
  public void executeOperation(String cardNumber, String password, BigDecimal value) {
    Card card =
        repository
            .findByCardNumber(cardNumber)
            .orElseThrow(() -> new TransactionException(TransactionStatus.CARTAO_INEXISTENTE));

    if (!card.getPassword().equals(password)) {
      throw new TransactionException(TransactionStatus.SENHA_INVALIDA);
    }

    if (card.getBalance().compareTo(value) < 0) {
      throw new TransactionException(TransactionStatus.SALDO_INSUFICIENTE);
    }

    card.setBalance(card.getBalance().subtract(value));
    repository.save(card);
  }
}
