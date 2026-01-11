package dev.danielmesquita.miniauthorizer.dto;

import dev.danielmesquita.miniauthorizer.entity.Card;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CardDTO {
  private Long id;

  @Size(min = 3, max = 80, message = "Name must be 10-80 characters")
  @NotBlank(message = "Card number is required")
  private String cardNumber;

  @NotBlank(message = "Password is required")
  private String password;

  public CardDTO() {
  }

  public CardDTO(Long id, String cardNumber, String password) {
    this.id = id;
    this.cardNumber = cardNumber;
    this.password = password;
  }

  public CardDTO(Card entity) {
    this.id = entity.getId();
    this.cardNumber = entity.getCardNumber();
    this.password = entity.getPassword();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
