package dev.danielmesquita.miniauthorizer.repository;

import dev.danielmesquita.miniauthorizer.entity.Card;
import dev.danielmesquita.miniauthorizer.utils.Factory;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class CardRepositoryTest {

  @Autowired private CardRepository repository;

  private String existingCardNumber;
  private String nonExistingCardNumber;
  private Card card;

  @BeforeEach
  public void setUp() {
    card = Factory.createCard();
    existingCardNumber = "1234567890123456"; // Assume this card number exists in the test database
    nonExistingCardNumber = "0000000000000000"; // Assume this card number does not exist
  }

  @Test
  public void saveShouldPersistCardWithAutoIncrementWhenIdIsNull() {
    card.setId(null);
    card.setCardNumber(existingCardNumber);
    card = repository.save(card);
    Assertions.assertNotNull(card);
    Assertions.assertNotNull(card.getId());
  }

  @Test
  public void findByCardNumberShouldReturnEmptyOptionalWhenCardNumberDoesNotExist() {
    Optional<Card> result = repository.findByCardNumber(nonExistingCardNumber);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void findByCardNumberShouldReturnNonEmptyOptionalWhenCardNumberExists() {
    card.setCardNumber(existingCardNumber);
    repository.save(card);
    Optional<Card> result = repository.findByCardNumber(existingCardNumber);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  public void findByCardNumberForUpdateShouldReturnEmptyOptionalWhenCardNumberDoesNotExist() {
    Optional<Card> result = repository.findByCardNumberForUpdate(nonExistingCardNumber);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void findByCardNumberForUpdateShouldReturnNonEmptyOptionalWhenCardNumberExists() {
    card.setCardNumber(existingCardNumber);
    repository.save(card);
    Optional<Card> result = repository.findByCardNumberForUpdate(existingCardNumber);
    Assertions.assertTrue(result.isPresent());
  }
}
