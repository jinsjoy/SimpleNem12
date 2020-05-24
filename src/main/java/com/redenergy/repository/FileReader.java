package com.redenergy.repository;

import com.redenergy.exception.SimpleNem12ParserException;

import java.io.File;
import java.util.List;

/**
 * Contract for a file Reader.
 */
public interface FileReader {

  /**
   * Read all the lines from the input file provided.
   * @param fileName the input file name.
   * @return list of String.
   * @throws SimpleNem12ParserException
   */
  public List<String> readLines(File fileName) throws SimpleNem12ParserException;

}
