package dev.danielmesquita.miniauthorizer.dto;

import dev.danielmesquita.miniauthorizer.entity.Card;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CardDTO {
  private Long id;

  @NotBlank(message = "Holder name is required")
  private String cardHolderName;

  @Size(min = 3, max = 80, message = "Name must be 10-80 characters")
  @NotBlank(message = "Card number is required")
  private String cardNumber;

  @NotBlank(message = "Password is required")
  private String password;

  private BigDecimal balance;

  public CardDTO() {
  }

  public CardDTO(Long id, String cardHolderName, String cardNumber, String password, BigDecimal balance) {
    this.id = id;
    this.cardHolderName = cardHolderName;
    this.cardNumber = cardNumber;
    this.password = password;
    this.balance = balance;
  }

  public CardDTO(Card entity) {
    this.id = entity.getId();
    this.cardHolderName = entity.getCardHolderName();
    this.cardNumber = entity.getCardNumber();
    this.password = entity.getPassword();
    this.balance = entity.getBalance();
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

  public BigDecimal getBalance() {
    return balance;
  }
}
