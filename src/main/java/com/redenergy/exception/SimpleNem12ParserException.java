package com.redenergy.exception;

/**
 * For Exceptions thrown while parsing Interval Metering Data.
 */
public class SimpleNem12ParserException extends Exception{

  public SimpleNem12ParserException(String message) {
    super(message);
  }

  public SimpleNem12ParserException(String message, Throwable cause) {
    super(message, cause);
  }

}
