package com.redenergy.validation;

/**
 * The generic validation interface with a test method to validate the
 * received input parameter
 *
 * @param <K> any type
 */
@FunctionalInterface
public interface Validation<K> {

  /**
   * The test method which accepts a parameter.
   *
   * @param param
   * @return
   */
  public Nem12ValidationResult test(K param);

  /**
   * Method to evaluate multiple and conditions.
   *
   * @param other the other parameter
   * @return combined validation object.
   */
  default Validation<K> and(Validation<K> other) {
    return (param) -> {
      Nem12ValidationResult result = this.test(param);
      return !result.isValid() ? result : other.test(param);
    };
  }

  /**
   * Method of evaluate multiple or conditions.
   *
   * @param other the other parameter
   * @return combined validation object.
   */
  default Validation<K> or(Validation<K> other) {
    return (param) -> {
      Nem12ValidationResult result = this.test(param);
      return result.isValid() ? result : other.test(param);
    };
  }
}