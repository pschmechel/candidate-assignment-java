package ch.aaap.assignment.model;

import java.time.LocalDate;

public interface PoliticalCommunity {

  public String getNumber();

  public String getName();

  public String getShortName();

  public LocalDate getLastUpdate();
}
