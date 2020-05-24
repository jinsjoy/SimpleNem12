package com.redenergy.repository;

import com.redenergy.exception.SimpleNem12ParserException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Meter Records reader which reads date from CSV file.
 */
public class MeterRecordsReader implements FileReader {

  /**
   * Read the data from csv line by line.
   * @param simpleNem12File the input file.
   * @return list of String
   * @throws SimpleNem12ParserException
   */
  @Override
  public List<String> readLines(File simpleNem12File) throws SimpleNem12ParserException {
    try (Stream<String> lines = Files.lines(simpleNem12File.toPath())) {
      List<String> meterRecords =
          lines.map(String::trim).collect(Collectors.toList());
      return meterRecords;
    } catch (IOException exception) {
      throw new SimpleNem12ParserException(" Error reading meter records from csv file", exception);
    }
  }
}
