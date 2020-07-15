package ch.aaap.assignment.model;

import ch.aaap.assignment.raw.CSVPoliticalCommunity;
import ch.aaap.assignment.raw.CSVPostalCommunity;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DefaultModelFactory {

  public static Model createModel(
      Set<CSVPoliticalCommunity> politicalCommunities, Set<CSVPostalCommunity> postalCommunities) {

    Map<String, Set<PostalCommunity>> postalCommunitiesByPoliticalNumber = new HashMap<>();

    for (CSVPostalCommunity postalCommunity : postalCommunities) {
      PostalCommunity defaultPostalCommunity = mapToDefaultPostalCommunity(postalCommunity);
      String key = postalCommunity.getPoliticalCommunityNumber();

      if (postalCommunitiesByPoliticalNumber.containsKey(key)) {
        postalCommunitiesByPoliticalNumber.put(
            key,
            Stream.concat(List.of(defaultPostalCommunity).stream(),
                postalCommunitiesByPoliticalNumber.get(key).stream())
                .collect(Collectors.toSet()));
      } else {
        postalCommunitiesByPoliticalNumber.put(key, Set.of(defaultPostalCommunity));
      }
    }

    Set<PoliticalCommunity> defaultPoliticalCommunities = politicalCommunities.stream()
        .map(pc -> mapToDefaultPoliticalCommunity(pc,
            postalCommunitiesByPoliticalNumber.getOrDefault(pc.getNumber(), Set.of())))
        .collect(Collectors.toSet());

    Set<District> districts = defaultPoliticalCommunities.stream()
        .map(PoliticalCommunity::getDistrict)
        .collect(Collectors.toSet());

    Set<Canton> cantons = districts.stream()
        .map(District::getCanton)
        .collect(Collectors.toSet());

    return DefaultModel.builder()
        .politicalCommunities(defaultPoliticalCommunities)
        .postalCommunities(
            postalCommunitiesByPoliticalNumber.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()))
        .cantons(cantons)
        .districts(districts)
        .build();
  }

  private static DefaultPostalCommunity mapToDefaultPostalCommunity(CSVPostalCommunity pc) {
    return DefaultPostalCommunity.builder()
        .zipCode(pc.getZipCode())
        .zipCodeAddition(pc.getZipCodeAddition())
        .name(pc.getName())
        .build();
  }

  private static DefaultPoliticalCommunity mapToDefaultPoliticalCommunity(
      CSVPoliticalCommunity pc,
      Set<PostalCommunity> postalCommunities) {
    return DefaultPoliticalCommunity.builder()
        .number(pc.getNumber())
        .name(pc.getName())
        .shortName(pc.getShortName())
        .lastUpdate(pc.getLastUpdate())
        .district(mapToDefaultDistrict(pc))
        .postalCommunities(postalCommunities)
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
    private final Set<PostalCommunity> postalCommunities;
  }

  @EqualsAndHashCode(of = {"name", "zipCode"})
  @ToString
  @Getter
  @Builder
  private static class DefaultPostalCommunity implements PostalCommunity {

    private final String zipCode;
    private final String zipCodeAddition;
    private final String name;
  }
}
