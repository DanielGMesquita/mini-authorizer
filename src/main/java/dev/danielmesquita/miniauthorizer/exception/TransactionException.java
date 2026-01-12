package dev.danielmesquita.miniauthorizer.exception;

import dev.danielmesquita.miniauthorizer.enums.TransactionStatus;

public class TransactionException extends RuntimeException {

  private final TransactionStatus status;

  public TransactionException(TransactionStatus status) {
    super(status.getStatus());
    this.status = status;
  }

  public TransactionStatus getStatus() {
    return status;
  }
}
