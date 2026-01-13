package dev.danielmesquita.miniauthorizer.controller;

import dev.danielmesquita.miniauthorizer.dto.CardDTO;
import dev.danielmesquita.miniauthorizer.dto.TransactionDTO;
import dev.danielmesquita.miniauthorizer.service.CardService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping
public class CardController {

  private final CardService cardService;

  public CardController(CardService cardService) {
    this.cardService = cardService;
  }

  @GetMapping("/cards/{cardNumber}")
  public ResponseEntity<BigDecimal> balance(@PathVariable String cardNumber) {
    return ResponseEntity.ok(cardService.getBalance(cardNumber));
  }

  @PostMapping("/cards")
  public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CardDTO cardDTO) {
    cardDTO = cardService.createCard(cardDTO);
    URI uri =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{cardNumber}")
            .buildAndExpand(cardDTO.getCardNumber())
            .toUri();
    return ResponseEntity.created(uri).body(cardDTO);
  }

  @PostMapping("/transactions")
  public ResponseEntity<CardDTO> processTransaction(
      @Valid @RequestBody TransactionDTO transactionDTO) {
    CardDTO cardDTO = cardService.executeTransaction(transactionDTO);
    return ResponseEntity.ok().body(cardDTO);
  }
}
