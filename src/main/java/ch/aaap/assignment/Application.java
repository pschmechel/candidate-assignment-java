package ch.aaap.assignment;

import ch.aaap.assignment.model.DefaultModelFactory;
import ch.aaap.assignment.model.Model;
import ch.aaap.assignment.model.PoliticalCommunity;
import ch.aaap.assignment.raw.CSVPoliticalCommunity;
import ch.aaap.assignment.raw.CSVPostalCommunity;
import ch.aaap.assignment.raw.CSVUtil;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Application {

  private Model model = null;

  public Application() {
    initModel();
  }

  public static void main(String[] args) {
    new Application();
  }

  /**
   * Reads the CSVs and initializes a in memory model
   */
  private void initModel() {
    Set<CSVPoliticalCommunity> politicalCommunities = CSVUtil.getPoliticalCommunities();
    Set<CSVPostalCommunity> postalCommunities = CSVUtil.getPostalCommunities();

    model = DefaultModelFactory.createModel(politicalCommunities, postalCommunities);
  }

  /**
   * @return model
   */
  public Model getModel() {
    return model;
  }

  /**
   * @param cantonCode of a canton (e.g. ZH)
   * @return amount of political communities in given canton
   */
  public long getAmountOfPoliticalCommunitiesInCanton(String cantonCode) {
    return countOrThrow(
        model.getPoliticalCommunities(),
        pc -> cantonCode.equals(pc.getCanton().getCode()));
  }

  /**
   * @param cantonCode of a canton (e.g. ZH)
   * @return amount of districts in given canton
   */
  public long getAmountOfDistrictsInCanton(String cantonCode) {
    return countOrThrow(
        model.getDistricts(),
        pc -> cantonCode.equals(pc.getCanton().getCode()));
  }

  /**
   * @param districtNumber of a district (e.g. 101)
   * @return amount of districts in given canton
   */
  public long getAmountOfPoliticalCommunitiesInDistrict(String districtNumber) {
    return countOrThrow(
        model.getPoliticalCommunities(),
        pc -> districtNumber.equals(pc.getDistrict().getNumber()));
  }

  private static <T> long countOrThrow(Collection<T> list, Predicate<T> filter) {
    return list.stream()
        .filter(filter)
        .mapToInt(pc -> 1)
        .reduce(Integer::sum)
        .orElseThrow(() -> new IllegalArgumentException("Nothing found, value expected"));
  }

  /**
   * @param zipCode 4 digit zip code
   * @return district that belongs to specified zip code
   */
  public Set<String> getDistrictsForZipCode(String zipCode) {
    return model.getPoliticalCommunities().stream()
        .filter(pc -> pc.getPostalCommunities().stream()
            .anyMatch(post -> zipCode.equals(post.getZipCode())))
        .map(politicalCommunity -> politicalCommunity.getDistrict().getName())
        .collect(Collectors.toSet());
  }

  /**
   * @param postalCommunityName name
   * @return lastUpdate of the political community by a given postal community name
   */
  public LocalDate getLastUpdateOfPoliticalCommunityByPostalCommunityName(
      String postalCommunityName) {
    return model.getPoliticalCommunities().stream()
        .filter(pc -> pc.getPostalCommunities().stream()
            .anyMatch(postalCommunity -> postalCommunityName.equals(postalCommunity.getName())))
        .map(PoliticalCommunity::getLastUpdate)
        .findFirst()
        .orElse(null);
  }

  /**
   * https://de.wikipedia.org/wiki/Kanton_(Schweiz)
   *
   * @return amount of canton
   */
  public long getAmountOfCantons() {
    return model.getCantons().size();
  }

  /**
   * https://de.wikipedia.org/wiki/Kommunanz
   *
   * @return amount of political communities without postal communities
   */
  public long getAmountOfPoliticalCommunityWithoutPostalCommunities() {
    return model.getPoliticalCommunities().stream()
        .filter(pc -> pc.getPostalCommunities().isEmpty())
        .count();
  }
}
