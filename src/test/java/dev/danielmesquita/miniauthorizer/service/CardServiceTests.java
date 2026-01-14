package dev.danielmesquita.miniauthorizer.service;

import dev.danielmesquita.miniauthorizer.dto.CardDTO;
import dev.danielmesquita.miniauthorizer.dto.TransactionDTO;
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

  private Card card = new Card();

  private CardDTO cardDTO = new CardDTO();

  private final TransactionDTO transactionDTO = new TransactionDTO();

  @BeforeEach
  public void setUp() {
    existingCardNumber = "12345678";
    nonExistingCardNumber = "111";
    card = Factory.createCard();
    cardDTO = Factory.createCardDTO();
    transactionDTO.setCardNumber(existingCardNumber);
    transactionDTO.setPassword(rightPassword);
    transactionDTO.setValue(new BigDecimal("50"));
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
    Mockito.when(repository.findByCardNumberForUpdate(existingCardNumber))
        .thenReturn(Optional.of(card));

    Assertions.assertThrows(CardAlreadyExistsException.class, () -> service.createCard(cardDTO));

    Mockito.verify(repository, Mockito.times(1)).findByCardNumberForUpdate(existingCardNumber);
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
    Mockito.when(repository.findByCardNumberForUpdate(existingCardNumber))
        .thenReturn(Optional.of(card));
    card.setBalance(new BigDecimal("100"));

    Assertions.assertDoesNotThrow(() -> service.executeTransaction(transactionDTO));

    Mockito.verify(repository, Mockito.times(1)).findByCardNumberForUpdate(existingCardNumber);
  }

  @Test
  public void executeOperationShouldThrowExceptionWhenCardDoesNotExist() {
    Mockito.when(repository.findByCardNumberForUpdate(nonExistingCardNumber))
        .thenReturn(Optional.empty());
    transactionDTO.setCardNumber(nonExistingCardNumber);

    TransactionException exception =
        Assertions.assertThrows(
            TransactionException.class, () -> service.executeTransaction(transactionDTO));

    Assertions.assertEquals(TransactionStatus.CARTAO_INEXISTENTE, exception.getStatus());
  }

  @Test
  public void executeOperationShouldThrowExceptionWhenPasswordIsInvalid() {
    Mockito.when(repository.findByCardNumberForUpdate(existingCardNumber))
        .thenReturn(Optional.of(card));
    String wrongPassword = "wrongPassword";
    Mockito.when(passwordEncoder.matches(wrongPassword, card.getPassword())).thenReturn(false);
    transactionDTO.setPassword(wrongPassword);

    TransactionException exception =
        Assertions.assertThrows(
            TransactionException.class, () -> service.executeTransaction(transactionDTO));

    Assertions.assertEquals(TransactionStatus.SENHA_INVALIDA, exception.getStatus());
  }

  @Test
  public void executeOperationShouldThrowExceptionWhenInsufficientBalance() {
    Mockito.when(repository.findByCardNumberForUpdate(existingCardNumber))
        .thenReturn(Optional.of(card));
    Mockito.when(passwordEncoder.matches(rightPassword, card.getPassword())).thenReturn(true);
    card.setBalance(new BigDecimal("20"));

    TransactionException exception =
        Assertions.assertThrows(
            TransactionException.class, () -> service.executeTransaction(transactionDTO));

    Assertions.assertEquals(TransactionStatus.SALDO_INSUFICIENTE, exception.getStatus());
  }
}
