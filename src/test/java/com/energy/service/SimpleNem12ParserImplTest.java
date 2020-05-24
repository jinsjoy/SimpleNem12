package com.energy.service;

import com.redenergy.model.MeterRead;
import com.redenergy.service.SimpleNem12ParserImpl;
import nl.altindag.log.LogCaptor;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Unit tests for SimpleNem12Parser implementation.
 */
public class SimpleNem12ParserImplTest extends SimpleNem12ParserImplTestBase {

  private SimpleNem12ParserImpl simpleNem12Parser;
  private LogCaptor<SimpleNem12ParserImpl> logCaptor;

  @Before
  public void setUp()  {
    logCaptor = LogCaptor.forClass(SimpleNem12ParserImpl.class);
    simpleNem12Parser = new SimpleNem12ParserImpl();
  }

  /**
   * Verify file exists.
   */
  @Test
  public void testReadFile(){
    assertNotNull(simpleNem12Parser.parseSimpleNem12(getFile("SimpleNem12.csv")));
  }

  /**
   * Verify the MeterReads records after being parsed.
   */
  @Test
  public void testReadMeterRecords(){
    Collection<MeterRead> meterReadCollection = simpleNem12Parser.parseSimpleNem12(new File("src/main/resources/SimpleNem12.csv"));
     assertNotNull(meterReadCollection);
     assertEquals(meterReadCollection, getMeterReadRecords());
  }

  /**
   * Test for invalid first line in csv
   */
  @Test
  public void testInvalidFirstLineInCsv(){
    assertNotNull(simpleNem12Parser.parseSimpleNem12(getFile("SimpleNem12_InvalidFirstLine.csv")));
    assertTrue(logCaptor.getErrorLogs().toString().trim()
        .contains("RecordType 100 must be the first line in the file"));
  }

  /**
   * Test for invalid last line in csv
   */
  @Test
  public void testInvalidLastLineInCsv(){
    assertNotNull(simpleNem12Parser.parseSimpleNem12(getFile("SimpleNem12_InvalidLastLine.csv")));
    assertTrue(logCaptor.getErrorLogs().toString().trim()
        .contains("RecordType 900 must be the last line in the file"));
  }

  /**
   * Test for empty csv file
   */
  @Test
  public void testEmptyCsvFile() {
    assertNotNull(simpleNem12Parser.parseSimpleNem12(getFile("SimpleNem12_Empty.csv")));
    assertTrue(logCaptor.getErrorLogs().toString().trim()
        .contains("The input file doesn't have any meter records"));
  }

  /**
   * Test for invalid date
   */
  @Test
  public void testInvalidDate(){
    assertNotNull(simpleNem12Parser.parseSimpleNem12(getFile("SimpleNem12_InvalidDate.csv")));
    assertTrue(logCaptor.getErrorLogs().toString().trim()
        .contains("Input date 2016111113 cannot be parsed"));
  }

  /**
   * Test for invalid energy unit
   */
  @Test
  public void testInvalidEnergyUnit(){
    assertNotNull(simpleNem12Parser.parseSimpleNem12(getFile("SimpleNem12_InvalidEnergyUnit.csv")));
    assertTrue(logCaptor.getErrorLogs().toString().trim()
        .contains("EnergyUnit value should be KWH"));
  }
}
