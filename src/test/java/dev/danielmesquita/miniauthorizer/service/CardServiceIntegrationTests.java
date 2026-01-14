package dev.danielmesquita.miniauthorizer.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.danielmesquita.miniauthorizer.dto.CardDTO;
import dev.danielmesquita.miniauthorizer.dto.TransactionDTO;
import dev.danielmesquita.miniauthorizer.enums.TransactionStatus;
import dev.danielmesquita.miniauthorizer.exception.TransactionException;
import dev.danielmesquita.miniauthorizer.repository.CardRepository;
import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CardServiceIntegrationTests {
  @Autowired private CardService cardService;
  @Autowired private CardRepository cardRepository;

  private final String cardNumber = "99999999";
  private final String password = "1234";

  @BeforeEach
  void setup() {
    cardRepository.deleteAll();
    CardDTO cardDTO = new CardDTO();
    cardDTO.setCardNumber(cardNumber);
    cardDTO.setPassword(password);
    cardService.createCard(cardDTO);
  }

  @Test
  void concurrentTransactionsShouldNotAllowOverdraw() throws Exception {
    TransactionDTO tx1 = new TransactionDTO(cardNumber, password, new BigDecimal("400.00"));
    TransactionDTO tx2 = new TransactionDTO(cardNumber, password, new BigDecimal("200.00"));

    ExecutorService executor = Executors.newFixedThreadPool(2);
    Callable<Object> call1 =
        () -> {
          try {
            cardService.executeTransaction(tx1);
            return "SUCCESS";
          } catch (TransactionException e) {
            return e.getStatus();
          }
        };
    Callable<Object> call2 =
        () -> {
          try {
            cardService.executeTransaction(tx2);
            return "SUCCESS";
          } catch (TransactionException e) {
            return e.getStatus();
          }
        };

    Future<Object> f1 = executor.submit(call1);
    Future<Object> f2 = executor.submit(call2);

    Object r1 = f1.get();
    Object r2 = f2.get();

    assertTrue(
        (r1.equals("SUCCESS") && r2.equals(TransactionStatus.SALDO_INSUFICIENTE))
            || (r2.equals("SUCCESS") && r1.equals(TransactionStatus.SALDO_INSUFICIENTE)));
    executor.shutdown();
  }
}
