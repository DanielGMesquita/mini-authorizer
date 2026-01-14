package dev.danielmesquita.miniauthorizer.dto;

import dev.danielmesquita.miniauthorizer.entity.Card;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class CardDTO {

  @Size(min = 3, max = 80, message = "Name must be 10-80 characters")
  @NotBlank(message = "Card number is required")
  private String cardNumber;

  @NotBlank(message = "Password is required")
  private String password;

  private BigDecimal balance;

  public CardDTO() {}

  public CardDTO(String cardNumber, String password, BigDecimal balance) {
    this.cardNumber = cardNumber;
    this.password = password;
    this.balance = balance;
  }

  public CardDTO(Card entity) {
    this.cardNumber = entity.getCardNumber();
    this.password = entity.getPassword();
    this.balance = entity.getBalance();
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public String getPassword() {
    return password;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }
}
