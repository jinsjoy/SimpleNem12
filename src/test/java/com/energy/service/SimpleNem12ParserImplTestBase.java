package com.energy.service;

import com.redenergy.model.EnergyUnit;
import com.redenergy.model.MeterRead;
import com.redenergy.model.MeterVolume;
import com.redenergy.model.Quality;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * SimpleNem12ParserImpl test base class.
 */
public abstract class SimpleNem12ParserImplTestBase {

  /**
   * Get the file using file name
   *
   * @param fileName filename
   * @return File
   */
  protected File getFile(String fileName) {
    ClassLoader classLoader = getClass().getClassLoader();
    return new File(classLoader.getResource(fileName).getFile());
  }

  /**
   * Method which returns collection of MeterRead Records
   * equivalent to that present in SimpleNem12.csv
   *
   * @return collection of MeterRead records.
   */
  protected Collection<MeterRead> getMeterReadRecords() {
    List<MeterRead> meterReadList = new ArrayList<>();
    MeterRead meterRead1 = new MeterRead("6123456789", EnergyUnit.KWH);
    meterRead1.appendVolume(parseDate("20161113"), createMeterVolume("-50.8", "A"));
    meterRead1.appendVolume(parseDate("20161114"), createMeterVolume("23.96", "A"));
    meterRead1.appendVolume(parseDate("20161115"), createMeterVolume("32.0", "A"));
    meterRead1.appendVolume(parseDate("20161116"), createMeterVolume("-33", "A"));
    meterRead1.appendVolume(parseDate("20161117"), createMeterVolume("0", "A"));
    meterRead1.appendVolume(parseDate("20161118"), createMeterVolume("0", "E"));
    meterRead1.appendVolume(parseDate("20161119"), createMeterVolume("-9", "A"));
    meterReadList.add(meterRead1);
    MeterRead meterRead2 = new MeterRead("6987654321", EnergyUnit.KWH);
    meterRead2.appendVolume(parseDate("20161215"), createMeterVolume("-3.8", "A"));
    meterRead2.appendVolume(parseDate("20161216"), createMeterVolume("0", "A"));
    meterRead2.appendVolume(parseDate("20161217"), createMeterVolume("3.0", "E"));
    meterRead2.appendVolume(parseDate("20161218"), createMeterVolume("-12.8", "A"));
    meterRead2.appendVolume(parseDate("20161219"), createMeterVolume("23.43", "E"));
    meterRead2.appendVolume(parseDate("20161220"), createMeterVolume("4.5", "A"));
    meterReadList.add(meterRead2);
    return meterReadList;
  }

  /**
   * Create MeterVolume
   *
   * @param volume  volume
   * @param quality quality
   * @return MeterVolume
   */
  private MeterVolume createMeterVolume(String volume, String quality) {
    return new MeterVolume(new BigDecimal(volume),
        quality.equals("A") ? Quality.A : Quality.E);
  }

  /**
   * Get the LocalDate from input date
   *
   * @param date input date
   * @return LocalDate object.
   */
  private LocalDate parseDate(String date) {
    return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
  }

}
