package dev.danielmesquita.miniauthorizer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransactionDTO {

    @NotBlank(message = "Card number is required")
    private String cardNumber;

    @NotBlank(message = "Card password is required")
    private String password;

    @NotNull(message = "Transaction value is required")
    @DecimalMin(value = "0.01", message = "Value must be greater than zero")
    private BigDecimal value;

    public TransactionDTO() {
    }

    public TransactionDTO(String cardNumber, String password, BigDecimal valor) {
        this.cardNumber = cardNumber;
        this.password = password;
        this.value = valor;
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

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }
}
