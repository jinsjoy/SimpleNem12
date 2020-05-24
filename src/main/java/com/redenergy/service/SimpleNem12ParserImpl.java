package com.redenergy.service;

import com.redenergy.exception.SimpleNem12ParserException;
import com.redenergy.model.*;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import com.redenergy.repository.MeterRecordsReader;
import com.redenergy.validation.Nem12ValidationResult;
import com.redenergy.validation.Nem12Validators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Implementation of Simple Nem12 parser, which reads SimpleNem12 csv file and
 * returns the collection of MeterRead.
 */
public class SimpleNem12ParserImpl implements SimpleNem12Parser {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNem12ParserImpl.class);
  private static final String COMMA = ",";
  private static final Integer NMI_LENGTH = 10;

  private MeterRecordsReader meterRecordsReader;

  public SimpleNem12ParserImpl() {
    this.meterRecordsReader = new MeterRecordsReader();
  }

  /**
   * Parses Simple Nem12 CSV file and creates Collection of MeterRead records.
   *
   * @param simpleNem12File file in Simple NEM12 format
   * @return the collection of MeterRead records.
   */
  @Override
  public Collection<MeterRead> parseSimpleNem12(File simpleNem12File) {
    Collection<MeterRead> meterReadItems = new ArrayList<>();

    try {
      //validate the input file
      validateInputCsvFile(simpleNem12File);

      //read the list of meter records from the input csv file.
      List<String> meterRecords = meterRecordsReader.readLines(simpleNem12File);

      //validate the input file contents
      validateInputCsvFileData(meterRecords);

      List<String> validMeterRecords =
          meterRecords.stream().filter(
              line -> !(line.startsWith(RecordType.RECORD_TYPE_START.recordType)
                  || line.startsWith(RecordType.RECORD_TYPE_END.recordType)))
              .collect(Collectors.toList());

      //parse and build MeterRead collection from valid meter records.
      meterReadItems = parseMeteringData(validMeterRecords);

    } catch (SimpleNem12ParserException ex) {
      LOGGER.error("An Exception of type {} occurred while running SimpleNem12Parser. Cause : {}",
          ex.getClass(), ex.getMessage());
    }

    return meterReadItems;
  }

  /**
   * Validates the input csv file.
   *
   * @param simpleNem12File
   * @throws SimpleNem12ParserException
   */
  private void validateInputCsvFile(File simpleNem12File) throws SimpleNem12ParserException {
    Nem12ValidationResult nem12ValidationResult =
        Nem12Validators.isFileExists().test(simpleNem12File);

    throwExceptionIfInvalid(nem12ValidationResult);
  }


  /**
   * Validates the contents of Input CSV File.
   *
   * @param meterRecords
   * @throws SimpleNem12ParserException
   */
  private void validateInputCsvFileData(List<String> meterRecords)
      throws SimpleNem12ParserException {

    //validate if the file is empty
    Nem12ValidationResult nem12ValidationResult = Nem12Validators.isFileEmpty().test(meterRecords);
    throwExceptionIfInvalid(nem12ValidationResult);

    //validate if the start of csv is 100
    nem12ValidationResult = Nem12Validators.isValidStartLine(RecordType.RECORD_TYPE_START)
        .test(meterRecords.stream().findFirst()
            .filter(elem -> elem.split(COMMA)[0].equals(RecordType.RECORD_TYPE_START.recordType)));

    throwExceptionIfInvalid(nem12ValidationResult);

    //validate if the end of csv is 900
    nem12ValidationResult = Nem12Validators.isValidEndLine(RecordType.RECORD_TYPE_END)
        .test(meterRecords.stream().reduce((first, second) -> second)
            .filter(elem -> elem.split(COMMA)[0].equals(RecordType.RECORD_TYPE_END.recordType)));

    throwExceptionIfInvalid(nem12ValidationResult);
  }


  /**
   * Parse valid Metering data and create a collection of MeterRead records.
   *
   * @param validMeterRecords
   * @return collection of MeterRead records.
   * @throws SimpleNem12ParserException
   */
  private Collection<MeterRead> parseMeteringData(List<String> validMeterRecords)
      throws SimpleNem12ParserException {
    List<MeterRead> meterReadList = new ArrayList<>();
    for (String meterRecord : validMeterRecords) {
      try {
        parseMeterRecord(meterRecord, meterReadList);
      } catch (SimpleNem12ParserException ex) {
        throw new SimpleNem12ParserException(ex.getMessage());
      }
    }

    return meterReadList;
  }

  /**
   * Parse a single meter record and create parent MeterRead record if its top level
   * and create child MeterVolume and append to parent MeterRead record if the meter
   * record is at child level.
   *
   * @param meterRecord   a single line representing a single meter record
   * @param meterReadList list of meter reads
   * @throws SimpleNem12ParserException
   */
  private void parseMeterRecord(String meterRecord, List<MeterRead> meterReadList)
      throws SimpleNem12ParserException {

    String[] elements = meterRecord.split(COMMA);

    MeterRead meterRead;

    if (elements[0].equals(RecordType.RECORD_TYPE_PARENT.recordType)) {
      meterRead = validateAndCreateMeterRead(elements[1], elements[2]);
      meterReadList.add(meterRead);
    } else if (elements[0].equals(RecordType.RECORD_TYPE_CHILD.recordType)
        && !meterReadList.isEmpty()) {
      //list maintains the order,
      // so get the previous element and append the child records
      meterRead = meterReadList.get(meterReadList.size() - 1);
      createAndAppendVolume(meterRead, elements[1], elements[2], elements[3]);
    }
  }

  /**
   * Validates and create MeterRead Record using nmi and energyUnit.
   *
   * @param nmi        the input nmi
   * @param energyUnit the input energyUnit
   * @return
   * @throws SimpleNem12ParserException
   */
  private MeterRead validateAndCreateMeterRead(String nmi, String energyUnit)
      throws SimpleNem12ParserException {

    validateMeterRead(nmi, energyUnit);
    MeterRead meterRead = new MeterRead(nmi, EnergyUnit.KWH);
    return meterRead;
  }

  private void validateMeterRead(String nmi, String energyUnit) throws SimpleNem12ParserException {

    //validates the input NMI
    Nem12ValidationResult nem12ValidationResult = Nem12Validators.isValidNmi(NMI_LENGTH).test(nmi);
    if (!nem12ValidationResult.isValid()) {
      throw new SimpleNem12ParserException(nem12ValidationResult.getReason());
    }

    //validates the input EnergyUnit
    nem12ValidationResult = Nem12Validators.isValidEnergyUnit().test(energyUnit);
    throwExceptionIfInvalid(nem12ValidationResult);
  }

  /**
   * create and Append Meter Volume to the parent MeterRead record
   *
   * @param meterRead parent meterRead record
   * @param date      the date
   * @param volume    the volume
   * @param quality   the quality
   * @throws SimpleNem12ParserException
   */
  private void createAndAppendVolume(MeterRead meterRead, String date,
                                     String volume, String quality)
      throws SimpleNem12ParserException {
    meterRead.appendVolume(parseDate(date), createMeterVolume(volume, quality));
  }

  /**
   * Parse the meter volume date to local date.
   *
   * @param date the meter volume date
   * @return the LocalDate
   * @throws SimpleNem12ParserException
   */
  private LocalDate parseDate(String date) throws SimpleNem12ParserException {
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
      return LocalDate.parse(date, formatter);
    } catch (DateTimeParseException e) {
      throw new SimpleNem12ParserException(format("Input date %s cannot be parsed", date));
    }
  }


  /**
   * Create Meter Volume using volume and quality.
   *
   * @param volume  the volume
   * @param quality the quality
   * @return MeterVolume child record
   * @throws SimpleNem12ParserException
   */
  private MeterVolume createMeterVolume(String volume, String quality)
      throws SimpleNem12ParserException {
    validateQuality(quality);
    return new MeterVolume(new BigDecimal(volume),
        quality.equals("A") ? Quality.A : Quality.E);
  }

  /**
   * Validate the quality with permissible values.
   *
   * @param quality input quality
   * @throws SimpleNem12ParserException
   */
  private void validateQuality(String quality) throws SimpleNem12ParserException {
    Nem12ValidationResult nem12ValidationResult =
        Nem12Validators.isValidQuality().test(quality);
    throwExceptionIfInvalid(nem12ValidationResult);
  }

  /**
   * Throw SimpleNem12ParserException for invalid cases.
   *
   * @param nem12ValidationResult the result of validation.
   * @throws SimpleNem12ParserException
   */
  private void throwExceptionIfInvalid(
      Nem12ValidationResult nem12ValidationResult) throws SimpleNem12ParserException {
    if (!nem12ValidationResult.isValid()) {
      throw new SimpleNem12ParserException(nem12ValidationResult.getReason());
    }
  }

}
