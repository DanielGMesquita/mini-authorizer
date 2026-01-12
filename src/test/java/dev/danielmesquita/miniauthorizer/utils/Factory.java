package dev.danielmesquita.miniauthorizer.utils;

import dev.danielmesquita.miniauthorizer.dto.CardDTO;
import dev.danielmesquita.miniauthorizer.entity.Card;
import java.math.BigDecimal;

public class Factory {
  public static Card createCard() {
    return new Card(null, null, "Joao da Silva", "password123", BigDecimal.valueOf(0L));
  }

  public static CardDTO createCardDTO() {
    Card card = createCard();
    return new CardDTO(card);
  }
}
