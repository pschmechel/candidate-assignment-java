package ch.aaap.assignment.model;

import ch.aaap.assignment.raw.CSVPoliticalCommunity;
import ch.aaap.assignment.raw.CSVPostalCommunity;
import java.time.LocalDate;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DefaultModelFactory {

  public static Model createModel(
      Set<CSVPoliticalCommunity> politicalCommunities, Set<CSVPostalCommunity> postalCommunities) {

    return DefaultModel.builder()
        .build();
  }

  @ToString
  @Getter
  @Builder
  private static class DefaultModel implements Model {

    private final Set<PoliticalCommunity> politicalCommunities;
    private final Set<PostalCommunity> postalCommunities;
    private final Set<Canton> cantons;
    private final Set<District> districts;
  }

  @ToString
  @Getter
  @Builder
  private static class DefaultPoliticalCommunity implements PoliticalCommunity {

    private final String number;
    private final String name;
    private final String shortName;
    private final LocalDate lastUpdate;
  }

  @ToString
  @Getter
  @Builder
  private static class DefaultPostalCommunity implements PostalCommunity {

    private final String zipCode;
    private final String zipCodeAddition;
    private final String name;
  }

  @ToString
  @Getter
  @Builder
  private static class DefaultCanton implements Canton {

    private final String code;
    private final String name;
  }

  @ToString
  @Getter
  @Builder
  private static class DefaultDistrict implements District {

    private final String number;
    private final String name;
  }
}
