package hwr.oop.poker.application.domain.combinations;

import static hwr.oop.poker.application.domain.combinations.Combination.Label.FLUSH;

import hwr.oop.poker.application.domain.Card;
import hwr.oop.poker.application.domain.Color;
import hwr.oop.poker.application.domain.Symbol;
import java.util.List;
import java.util.stream.IntStream;

final class FlushMatchingStrategy implements CombinationDetectionStrategy {

  private final AnalysisFlyweightFactory flyweightFactory;

  public FlushMatchingStrategy(AnalysisFlyweightFactory flyweightFactory) {

    this.flyweightFactory = flyweightFactory;
  }

  @Override
  public CombinationDetectionResult match(List<Card> cards) {
    final var helper = flyweightFactory.get(cards);
    final var mostCommonColor = helper.mostCommonColor();
    final var cardsOfColor = cardsOfColor(mostCommonColor, cards);
    if (cardsOfColor.size() >= 5) {
      final int range = cardsOfColor.size() - 4;
      final List<List<Card>> candidates = IntStream.range(0, range)
          .mapToObj(i -> cardsOfColor.subList(i, i + 5))
          .toList();
      assertCombinationIsValidFlush(mostCommonColor, candidates);
      return CombinationDetectionResult.success(FLUSH, candidates);
    } else {
      return CombinationDetectionResult.failure(FLUSH);
    }
  }

  private void assertCombinationIsValidFlush(Color mostCommonColor, List<List<Card>> candidates) {
    assert candidates.stream()
        .allMatch(candidate -> candidate.size() == 5 &&
            candidate.stream().allMatch(card -> card.color().equals(mostCommonColor)));
  }

  private List<Card> cardsOfColor(Color mostCommonColor, List<Card> cards) {
    return cards.stream()
        .filter(c -> c.color().equals(mostCommonColor))
        .sorted(FlushMatchingStrategy::compareCardsBySymbol)
        .toList();
  }

  private static int compareCardsBySymbol(Card o1, Card o2) {
    return Symbol.DESCENDING_BY_STRENGTH.compare(o1.symbol(), o2.symbol());
  }

}
