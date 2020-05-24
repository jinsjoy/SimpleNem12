package com.redenergy.model;

/**
 * Represents record type in SimpleNem12 File.
 */
public enum RecordType {

  RECORD_TYPE_START("100"),
  RECORD_TYPE_PARENT("200"),
  RECORD_TYPE_CHILD("300"),
  RECORD_TYPE_END("900");

  public final String recordType;

  private RecordType(String recordType) {
    this.recordType = recordType;
  }

  @Override
  public String toString() {
    return this.recordType;
  }
}
