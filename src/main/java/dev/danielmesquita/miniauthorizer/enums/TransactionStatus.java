package dev.danielmesquita.miniauthorizer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionStatus {
  OK("OK"),
  SALDO_INSUFICIENTE("Saldo insuficiente"),
  SENHA_INVALIDA("Senha inválida"),
  CARTAO_INEXISTENTE("Cartão inexistente");

  private final String status;
}
