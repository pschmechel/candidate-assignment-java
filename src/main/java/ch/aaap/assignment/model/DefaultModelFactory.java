package ch.aaap.assignment.model;

import ch.aaap.assignment.raw.CSVPoliticalCommunity;
import ch.aaap.assignment.raw.CSVPostalCommunity;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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

    Map<String, DefaultPoliticalCommunity> defaultPoliticalCommunities = politicalCommunities
        .stream()
        .map(DefaultModelFactory::mapToDefaultPoliticalCommunity)
        .collect(Collectors.toMap(PoliticalCommunity::getNumber, Function.identity()));

    Set<District> districts = defaultPoliticalCommunities.values().stream()
        .map(PoliticalCommunity::getDistrict)
        .collect(Collectors.toSet());

    Set<Canton> cantons = districts.stream()
        .map(District::getCanton)
        .collect(Collectors.toSet());

    Set<PostalCommunity> defaultPostalCommunities = postalCommunities.stream()
        .map(pc -> mapToDefaultPostalCommunity(pc,
            defaultPoliticalCommunities.get(pc.getPoliticalCommunityNumber())))
        .collect(Collectors.toSet());

    return DefaultModel.builder()
        .politicalCommunities(new HashSet<>(defaultPoliticalCommunities.values()))
        .postalCommunities(defaultPostalCommunities)
        .cantons(cantons)
        .districts(districts)
        .build();
  }

  private static DefaultPostalCommunity mapToDefaultPostalCommunity(
      CSVPostalCommunity pc, PoliticalCommunity politicalCommunity) {
    return DefaultPostalCommunity.builder()
        .zipCode(pc.getZipCode())
        .zipCodeAddition(pc.getZipCodeAddition())
        .name(pc.getName())
        .politicalCommunity(politicalCommunity)
        .build();
  }

  private static DefaultPoliticalCommunity mapToDefaultPoliticalCommunity(
      CSVPoliticalCommunity pc) {
    return DefaultPoliticalCommunity.builder()
        .number(pc.getNumber())
        .name(pc.getName())
        .shortName(pc.getShortName())
        .lastUpdate(pc.getLastUpdate())
        .district(mapToDefaultDistrict(pc))
        .build();
  }

  private static DefaultDistrict mapToDefaultDistrict(CSVPoliticalCommunity pc) {
    return DefaultDistrict.builder()
        .number(pc.getDistrictNumber())
        .name(pc.getDistrictName())
        .canton(mapToDefaultCanton(pc))
        .build();
  }

  private static DefaultCanton mapToDefaultCanton(CSVPoliticalCommunity pc) {
    return DefaultCanton.builder()
        .code(pc.getCantonCode())
        .name(pc.getCantonName())
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
    private final Canton canton;
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
    private final District district;
  }

  //  @EqualsAndHashCode(of = {"name", "zipCode"})
  @ToString
  @Getter
  @Builder
  private static class DefaultPostalCommunity implements PostalCommunity {

    private final String zipCode;
    private final String zipCodeAddition;
    private final String name;
    private final PoliticalCommunity politicalCommunity;
  }
}
