package dev.danielmesquita.miniauthorizer.dto;

import dev.danielmesquita.miniauthorizer.entity.Card;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CardDTO {
  private Long id;

  @NotBlank(message = "Holder name is required")
  private String cardHolderName;

  @Size(min = 3, max = 80, message = "Name must be 10-80 characters")
  @NotBlank(message = "Card number is required")
  private String cardNumber;

  @NotBlank(message = "Password is required")
  private String password;

  public CardDTO() {
  }

  public CardDTO(Long id, String cardHolderName, String cardNumber, String password) {
    this.id = id;
    this.cardHolderName = cardHolderName;
    this.cardNumber = cardNumber;
    this.password = password;
  }

  public CardDTO(Card card) {
    this.id = card.getId();
    this.cardHolderName = card.getCardHolderName();
    this.cardNumber = card.getCardNumber();
    this.password = card.getPassword();
  }

  public Long getId() {
    return id;
  }

  public String getCardHolderName() {
    return cardHolderName;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public String getPassword() {
    return password;
  }
}
