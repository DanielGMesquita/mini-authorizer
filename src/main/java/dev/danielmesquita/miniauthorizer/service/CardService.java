package dev.danielmesquita.miniauthorizer.service;

import dev.danielmesquita.miniauthorizer.dto.CardDTO;
import dev.danielmesquita.miniauthorizer.entity.Card;
import dev.danielmesquita.miniauthorizer.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CardService {

  private final CardRepository repository;

  public CardService(CardRepository repository) {
    this.repository = repository;
  }

  public CardDTO createCard(CardDTO cardDTO) {
    Card entity = new Card();
    entity.setCardNumber(cardDTO.getCardNumber());
    entity.setPassword(cardDTO.getPassword());
    entity.setBalance(new BigDecimal("0")); // Initial balance

    entity = repository.save(entity);

    return new CardDTO(entity);
  }

  public BigDecimal getBalance(String cardNumber) {
    Card card = repository.findByCardNumberForUpdate(cardNumber)
            .orElseThrow(() -> new IllegalArgumentException("Card not found with number: " + cardNumber));
    return card.getBalance();
  }

  @Transactional
  public void executeOperation(String cardNumber, String password, BigDecimal value) {
    Card card = repository.findByCardNumberForUpdate(cardNumber)
            .orElseThrow(() -> new IllegalArgumentException("Card not found with number: " + cardNumber));

    if (!card.getPassword().equals(password)) {
      throw new IllegalArgumentException("Invalid card password.");
    }

    if (card.getBalance().compareTo(value) < 0) {
      throw new IllegalArgumentException("Insufficient balance.");
    }

    card.setBalance(card.getBalance().subtract(value));
    repository.save(card);
  }
}
