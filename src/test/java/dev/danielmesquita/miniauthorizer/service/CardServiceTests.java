package dev.danielmesquita.miniauthorizer.service;

import dev.danielmesquita.miniauthorizer.dto.CardDTO;
import dev.danielmesquita.miniauthorizer.entity.Card;
import dev.danielmesquita.miniauthorizer.exception.CardAlreadyExistsException;
import dev.danielmesquita.miniauthorizer.exception.ResourceNotFoundException;
import dev.danielmesquita.miniauthorizer.repository.CardRepository;
import dev.danielmesquita.miniauthorizer.utils.Factory;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CardServiceTests {

  @InjectMocks private CardService service;

  @Mock private CardRepository repository;

  private String existingCardNumber;

  private String nonExistingCardNumber;

  private Card card = new Card();

  private CardDTO cardDTO = new CardDTO();

  @BeforeEach
  public void setUp() {
    existingCardNumber = "12345678";
    nonExistingCardNumber = "111";
    card = Factory.createCard();
    cardDTO = Factory.createCardDTO();
  }

  @Test
  public void saveShouldReturnCardDTOWhenCardIsSaved() {
    cardDTO.setCardNumber(nonExistingCardNumber);
    Mockito.when(repository.save(Mockito.any())).thenReturn(card);

    Assertions.assertDoesNotThrow(
        () -> {
          service.createCard(cardDTO);
        });

    Mockito.verify(repository, Mockito.times(1)).save(Mockito.any());
  }

  @Test
  public void createCardShouldThrowExceptionWhenCardNumberExists() {
    cardDTO.setCardNumber(existingCardNumber);
    Mockito.when(repository.findByCardNumber(existingCardNumber)).thenReturn(Optional.of(card));

    Assertions.assertThrows(
        CardAlreadyExistsException.class,
        () -> {
          service.createCard(cardDTO);
        });

    Mockito.verify(repository, Mockito.times(1)).findByCardNumber(existingCardNumber);
  }

  @Test
  public void getBalanceShouldReturnBalanceWhenCardExists() {
    Mockito.when(repository.findByCardNumber(existingCardNumber)).thenReturn(Optional.of(card));

    Assertions.assertDoesNotThrow(
        () -> {
          service.getBalance(existingCardNumber);
        });

    Mockito.verify(repository, Mockito.times(1)).findByCardNumber(existingCardNumber);
  }

  @Test
  public void getBalanceShouldThrowExceptionWhenCardDoesNotExist() {
    Mockito.when(repository.findByCardNumber(nonExistingCardNumber)).thenReturn(Optional.empty());

    Assertions.assertThrows(
        ResourceNotFoundException.class,
        () -> {
          service.getBalance(nonExistingCardNumber);
        });

    Mockito.verify(repository, Mockito.times(1)).findByCardNumber(nonExistingCardNumber);
  }

  @Test
  public void executeOperationShouldWorkWhenCardExistsAndPasswordIsValidAndSufficientBalance() {
    Mockito.when(repository.findByCardNumber(existingCardNumber)).thenReturn(Optional.of(card));

    Assertions.assertDoesNotThrow(
        () -> {
          service.executeOperation(existingCardNumber, card.getPassword(), new BigDecimal("50"));
        });

    Mockito.verify(repository, Mockito.times(1)).findByCardNumber(existingCardNumber);
  }
}
