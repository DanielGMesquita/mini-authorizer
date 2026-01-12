package dev.danielmesquita.miniauthorizer.service;

import dev.danielmesquita.miniauthorizer.dto.CardDTO;
import dev.danielmesquita.miniauthorizer.entity.Card;
import dev.danielmesquita.miniauthorizer.enums.TransactionStatus;
import dev.danielmesquita.miniauthorizer.exception.CardAlreadyExistsException;
import dev.danielmesquita.miniauthorizer.exception.ResourceNotFoundException;
import dev.danielmesquita.miniauthorizer.exception.TransactionException;
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
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class CardServiceTests {

  @InjectMocks private CardService service;

  @Mock private CardRepository repository;

  @Mock private PasswordEncoder passwordEncoder;

  private String existingCardNumber;

  private String nonExistingCardNumber;

  private final String rightPassword = "password123";

  private final String wrongPassword = "wrongPassword";

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

    Assertions.assertThrows(CardAlreadyExistsException.class, () -> service.createCard(cardDTO));

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
        ResourceNotFoundException.class, () -> service.getBalance(nonExistingCardNumber));

    Mockito.verify(repository, Mockito.times(1)).findByCardNumber(nonExistingCardNumber);
  }

  @Test
  public void executeOperationShouldWorkWhenCardExistsAndPasswordIsValidAndSufficientBalance() {
    Mockito.when(passwordEncoder.matches(rightPassword, card.getPassword())).thenReturn(true);
    Mockito.when(repository.findByCardNumber(existingCardNumber)).thenReturn(Optional.of(card));
    card.setBalance(new BigDecimal("100"));

    Assertions.assertDoesNotThrow(
        () -> {
          service.executeOperation(existingCardNumber, card.getPassword(), new BigDecimal("50"));
        });

    Mockito.verify(repository, Mockito.times(1)).findByCardNumber(existingCardNumber);
  }

  @Test
  public void executeOperationShouldThrowExceptionWhenCardDoesNotExist() {
    Mockito.when(repository.findByCardNumber(nonExistingCardNumber)).thenReturn(Optional.empty());

    TransactionException exception =
        Assertions.assertThrows(
            TransactionException.class,
            () ->
                service.executeOperation(
                    nonExistingCardNumber, card.getPassword(), new BigDecimal("50")));

    Assertions.assertEquals(TransactionStatus.CARTAO_INEXISTENTE, exception.getStatus());
  }

  @Test
  public void executeOperationShouldThrowExceptionWhenPasswordIsInvalid() {
    Mockito.when(repository.findByCardNumber(existingCardNumber)).thenReturn(Optional.of(card));
    Mockito.when(passwordEncoder.matches(wrongPassword, card.getPassword())).thenReturn(false);

    TransactionException exception =
        Assertions.assertThrows(
            TransactionException.class,
            () ->
                service.executeOperation(existingCardNumber, wrongPassword, new BigDecimal("50")));

    Assertions.assertEquals(TransactionStatus.SENHA_INVALIDA, exception.getStatus());
  }
}
