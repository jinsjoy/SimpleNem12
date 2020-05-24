package com.redenergy.validation;

/**
 * The Nem12ValidationResult Object is used to find the result of testing
 * of predicate and get the reason if its a failure.
 */
public class Nem12ValidationResult {

  private boolean valid;
  private String reason;

  public boolean isValid() {
    return valid;
  }

  public String getReason() {
    return reason;
  }

  public static Nem12ValidationResult ok() {
    return new Nem12ValidationResult(true);
  }

  private Nem12ValidationResult(boolean valid) {
    this.valid = valid;
  }

  private Nem12ValidationResult(boolean valid, String reason) {
    this.valid = valid;
    this.reason = reason;
  }

  public static Nem12ValidationResult fail(String reason) {
    return new Nem12ValidationResult(false, reason);
  }

}
