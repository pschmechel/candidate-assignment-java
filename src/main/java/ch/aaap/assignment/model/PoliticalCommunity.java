package ch.aaap.assignment.model;

import java.time.LocalDate;

public interface PoliticalCommunity {

  public String getNumber();

  public String getName();

  public String getShortName();

  public LocalDate getLastUpdate();

  public District getDistrict();

  public default Canton getCanton() {
    return getDistrict().getCanton();
  }
}
