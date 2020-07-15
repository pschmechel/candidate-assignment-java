package ch.aaap.assignment.model;

import ch.aaap.assignment.raw.CSVPoliticalCommunity;
import ch.aaap.assignment.raw.CSVPostalCommunity;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DefaultModelFactory {

  public static Model createModel(
      Set<CSVPoliticalCommunity> politicalCommunities, Set<CSVPostalCommunity> postalCommunities) {

    Set<PostalCommunity> defaultPostalCommunities = postalCommunities.stream()
        .map(DefaultModelFactory::mapToDefaultPostalCommunity)
        .collect(Collectors.toSet());

    Set<PoliticalCommunity> defaultPoliticalCommunities = politicalCommunities.stream()
        .map(DefaultModelFactory::mapToDefaultPoliticalCommunity)
        .collect(Collectors.toSet());

    Set<Canton> cantons = politicalCommunities.stream()
        .map(DefaultModelFactory::mapToDefaultCanton)
        .collect(Collectors.toSet());

    Set<District> districts = politicalCommunities.stream()
        .map(DefaultModelFactory::mapToDefaultDistrict)
        .collect(Collectors.toSet());

    return DefaultModel.builder()
        .politicalCommunities(defaultPoliticalCommunities)
        .postalCommunities(defaultPostalCommunities)
        .cantons(cantons)
        .districts(districts)
        .build();
  }

  private static DefaultPostalCommunity mapToDefaultPostalCommunity(CSVPostalCommunity pc) {
    return DefaultPostalCommunity.builder()
        .zipCode(pc.getZipCode())
        .zipCodeAddition(pc.getZipCodeAddition())
        .name(pc.getName())
        .politicalCommunityNumber(pc.getPoliticalCommunityNumber())
        .build();
  }

  private static DefaultPoliticalCommunity mapToDefaultPoliticalCommunity(
      CSVPoliticalCommunity pc) {
    return DefaultPoliticalCommunity.builder()
        .number(pc.getNumber())
        .name(pc.getName())
        .shortName(pc.getShortName())
        .lastUpdate(pc.getLastUpdate())
        .build();
  }

  private static DefaultCanton mapToDefaultCanton(CSVPoliticalCommunity pc) {
    return DefaultCanton.builder()
        .code(pc.getCantonCode())
        .name(pc.getCantonName())
        .build();
  }

  private static DefaultDistrict mapToDefaultDistrict(CSVPoliticalCommunity pc) {
    return DefaultDistrict.builder()
        .number(pc.getDistrictNumber())
        .name(pc.getDistrictName())
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

  @EqualsAndHashCode(of = "number")
  @ToString
  @Getter
  @Builder
  private static class DefaultPoliticalCommunity implements PoliticalCommunity {

    private final String number;
    private final String name;
    private final String shortName;
    private final LocalDate lastUpdate;
  }

  @EqualsAndHashCode(of = {"zipCode", "zipCodeAddition"})
  @ToString
  @Getter
  @Builder
  private static class DefaultPostalCommunity implements PostalCommunity {

    private final String zipCode;
    private final String zipCodeAddition;
    private final String name;
    private final String politicalCommunityNumber;
  }

  @EqualsAndHashCode(of = "code")
  @ToString
  @Getter
  @Builder
  private static class DefaultCanton implements Canton {

    private final String code;
    private final String name;
  }

  @EqualsAndHashCode(of = "number")
  @ToString
  @Getter
  @Builder
  private static class DefaultDistrict implements District {

    private final String number;
    private final String name;
  }
}
