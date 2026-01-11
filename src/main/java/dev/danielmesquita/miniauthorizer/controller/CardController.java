package dev.danielmesquita.miniauthorizer.controller;

import dev.danielmesquita.miniauthorizer.dto.CardDTO;
import dev.danielmesquita.miniauthorizer.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/cards")
public class CardController {

  private final CardService cardService;

  public CardController(CardService cardService) {
    this.cardService = cardService;
  }

  @GetMapping("/{cardNumber}/balance")
  public ResponseEntity<BigDecimal> balance (@PathVariable String cardNumber) {
    return ResponseEntity.ok(cardService.getBalance(cardNumber));
  }

  @PostMapping("/create")
  public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CardDTO cardDTO) {
    cardDTO = cardService.createCard(cardDTO);
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(cardDTO.getId())
            .toUri();
    return ResponseEntity.created(uri).body(cardDTO);
  }
}
