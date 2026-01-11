package dev.danielmesquita.miniauthorizer.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "tb_card")
public class Card {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String cardNumber;

  private String cardHolderName;

  private String password;

  @Column(nullable = false)
  private BigDecimal balance;

  public Card() {}

  public Card(Long id, String cardNumber, String cardHolderName, String password, BigDecimal balance) {
    this.id = id;
    this.cardNumber = cardNumber;
    this.cardHolderName = cardHolderName;
    this.password = password;
    this.balance = balance;
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

  public String getCardHolderName() {
    return cardHolderName;
  }

  public void setCardHolderName(String cardHolderName) {
    this.cardHolderName = cardHolderName;
  }

  public String getPassword() {
    return password;
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
