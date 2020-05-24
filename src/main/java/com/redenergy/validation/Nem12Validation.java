package com.redenergy.validation;


import java.util.function.Predicate;

/**
 * The core validation class which executes the predicate and return validation result.
 *
 * @param <K> any input parameter.
 */
public class Nem12Validation<K> implements Validation<K> {

  private Predicate<K> predicate;
  private String errorMessage;

  /**
   * Method which accepts the predicate and error message
   *
   * @param predicate    the predicate
   * @param errorMessage error message if predicate evaluate to false
   * @param <K>          any type
   * @return the Nem12Validation object
   */
  public static <K> Nem12Validation<K> from(Predicate<K> predicate, String errorMessage) {
    return new Nem12Validation<K>(predicate, errorMessage);
  }

  /**
   * Nem12Validation constructor
   *
   * @param predicate    the predicate
   * @param errorMessage the error message
   */
  private Nem12Validation(Predicate<K> predicate, String errorMessage) {
    this.predicate = predicate;
    this.errorMessage = errorMessage;
  }

  /**
   * The method which evaluates the predicate and create Nwm12ValidationResult with valid status
   * or invalid status with error message
   *
   * @param param the input parameter.
   * @return Nwm12ValidationResult object
   */
  @Override
  public Nem12ValidationResult test(K param) {
    return predicate.test(param) ? Nem12ValidationResult.ok() :
        Nem12ValidationResult.fail(errorMessage);
  }
}
