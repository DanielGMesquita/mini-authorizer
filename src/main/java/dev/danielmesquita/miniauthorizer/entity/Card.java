package dev.danielmesquita.miniauthorizer.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "tb_card")
public class Card {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private String cardNumber;

  private String cardHolderName;

  @Column(nullable = false)
  private BigDecimal balance;

  public Card() {}

  public Card(String cardNumber, String cardHolderName, BigDecimal balance) {
    this.cardNumber = cardNumber;
    this.cardHolderName = cardHolderName;
    this.balance = balance;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public String getCardHolderName() {
    return cardHolderName;
  }

  public void setCardHolderName(String cardHolderName) {
    this.cardHolderName = cardHolderName;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Card card = (Card) o;
    return Objects.equals(cardNumber, card.cardNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(cardNumber);
  }
}
