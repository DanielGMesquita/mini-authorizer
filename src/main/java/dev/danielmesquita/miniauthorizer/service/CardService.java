package dev.danielmesquita.miniauthorizer.service;

import dev.danielmesquita.miniauthorizer.dto.CardDTO;
import dev.danielmesquita.miniauthorizer.dto.TransactionDTO;
import dev.danielmesquita.miniauthorizer.entity.Card;
import dev.danielmesquita.miniauthorizer.enums.TransactionStatus;
import dev.danielmesquita.miniauthorizer.exception.CardAlreadyExistsException;
import dev.danielmesquita.miniauthorizer.exception.ResourceNotFoundException;
import dev.danielmesquita.miniauthorizer.exception.TransactionException;
import dev.danielmesquita.miniauthorizer.repository.CardRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardService {

  private final CardRepository repository;
  private final PasswordEncoder passwordEncoder;

  public CardService(CardRepository repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public CardDTO createCard(CardDTO cardDTO) {
    Optional<Card> existing = repository.findByCardNumberForUpdate(cardDTO.getCardNumber());
    if (existing.isPresent()) {
      throw new CardAlreadyExistsException("Card number already exists");
    }

    Card entity = new Card();
    entity.setCardNumber(cardDTO.getCardNumber());
    entity.setPassword(passwordEncoder.encode(cardDTO.getPassword()));
    entity.setBalance(new BigDecimal("500")); // Initial balance

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
  public CardDTO executeTransaction(TransactionDTO transactionDTO) {
    Card card =
        repository
            .findByCardNumberForUpdate(transactionDTO.getCardNumber())
            .orElseThrow(() -> new TransactionException(TransactionStatus.CARTAO_INEXISTENTE));

    if (!passwordEncoder.matches(transactionDTO.getPassword(), card.getPassword())) {
      throw new TransactionException(TransactionStatus.SENHA_INVALIDA);
    }

    BigDecimal value = transactionDTO.getValue();
    BigDecimal balance = card.getBalance();
    BigDecimal newBalance = balance.subtract(value);

    if ((newBalance).compareTo(BigDecimal.ZERO) < 0) {
      throw new TransactionException(TransactionStatus.SALDO_INSUFICIENTE);
    }

    card.setBalance(newBalance);
    repository.save(card);

    return new CardDTO(card);
  }
}
