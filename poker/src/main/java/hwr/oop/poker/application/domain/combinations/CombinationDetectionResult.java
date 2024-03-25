package hwr.oop.poker.application.domain.combinations;

import hwr.oop.poker.application.domain.Card;
import java.util.List;

final class CombinationDetectionResult {

  private final List<List<Card>> candidates;
  private final Combination.Label label;

  static CombinationDetectionResult success(Combination.Label label,
      List<List<Card>> alternatives) {
    return new CombinationDetectionResult(alternatives, label);
  }

  static CombinationDetectionResult failure(Combination.Label label) {
    return new CombinationDetectionResult(null, label);
  }


  private CombinationDetectionResult(List<List<Card>> candidates, Combination.Label label) {
    this.candidates = candidates;
    this.label = label;
  }

  public Combination.Label label() {
    return label;
  }

  public List<List<Card>> alternatives() {
    return candidates;
  }

  public List<Card> winner() {
    if (successful()) {
      return candidates.getFirst();
    } else {
      throw new IllegalStateException("Cannot retrieve Cards from unsuccessful show down matching");
    }
  }

  public boolean successful() {
    return candidates != null;
  }

}
