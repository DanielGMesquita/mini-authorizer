package dev.danielmesquita.miniauthorizer.dto;

import java.time.Instant;

public class CustomError {

  private final Instant timeStamp;
  private final Integer status;
  private final String error;
  private final String path;

  public CustomError(Instant timeStamp, Integer status, String error, String path) {
    this.timeStamp = timeStamp;
    this.status = status;
    this.error = error;
    this.path = path;
  }

  public Instant getTimeStamp() {
    return timeStamp;
  }

  public Integer getStatus() {
    return status;
  }

  public String getError() {
    return error;
  }

  public String getPath() {
    return path;
  }
}
