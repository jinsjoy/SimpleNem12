package com.redenergy.validation;

import com.redenergy.model.EnergyUnit;
import com.redenergy.model.Quality;
import com.redenergy.model.RecordType;


import java.io.File;
import java.util.List;
import java.util.Optional;


import static java.lang.String.format;

/**
 * The list of validators with SimpleNem12Parsing.
 * This gives flexibility for a single place to evaluate and produce error message.
 */
public class Nem12Validators {

  /**
   * Validates if file exists
   *
   * @return Core Validation Object with predicate and error message.
   */
  public static Validation<File> isFileExists() {
    return Nem12Validation.from(elem -> elem.exists(), format("The File Does Not Exists"));
  }

  /**
   * Validates if file is empty
   *
   * @return Core Validation Object with predicate and error message.
   */
  public static Validation<List<String>> isFileEmpty() {
    return Nem12Validation.from(elem -> (elem != null && !elem.isEmpty()),
        format("The input file doesn't have any meter records"));
  }

  /**
   * Validates if NMI is valid
   *
   * @return Core Validation Object with predicate and error message.
   */
  public static Validation<String> isValidNmi(Integer nmiLength) {
    return Nem12Validation.from(elem -> (elem.length() == nmiLength),
        format("the number of characters should be %s", nmiLength));
  }

  /**
   * Validates the energy unit
   *
   * @return Core Validation Object with predicate and error message.
   */
  public static Validation<String> isValidEnergyUnit() {
    return Nem12Validation.from(elem -> (elem.equals(EnergyUnit.KWH.toString())),
        format("EnergyUnit value should be %s", EnergyUnit.KWH));
  }

  /**
   * Validates the quality
   *
   * @return Core Validation Object with predicate and error message.
   */
  public static Validation<String> isValidQuality() {
    return Nem12Validation.from(elem -> (elem.equals(Quality.A.toString())
            || elem.equals(Quality.E.toString())),
        format("Quality should be either %s or %s", Quality.A, Quality.E));
  }

  /**
   * Validates the Start line is of RECORD_TYPE_START("100)
   *
   * @return Core Validation Object with predicate and error message.
   */
  public static Validation<Optional<String>> isValidStartLine(
      RecordType recordTypeStart) {
    return Nem12Validation.from(elem -> elem.isPresent(),
        format("RecordType %s must be the first line in the file", recordTypeStart));
  }

  /**
   * Validates the end line is of RECORD_TYPE_END("200)
   *
   * @return Core Validation Object with predicate and error message.
   */
  public static Validation<Optional<String>> isValidEndLine(
      RecordType recordTypeEnd) {
    return Nem12Validation.from(elem -> elem.isPresent(),
        format("RecordType %s must be the last line in the file", recordTypeEnd));
  }
}
