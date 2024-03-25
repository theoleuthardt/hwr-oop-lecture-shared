package hwr.oop.poker.application.domain.combinations;

import static hwr.oop.poker.application.domain.combinations.Combination.Label.STRAIGHT;

import hwr.oop.poker.application.domain.Card;
import hwr.oop.poker.application.domain.Color;
import hwr.oop.poker.application.domain.Symbol;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

final class StraightMatchingStrategy implements CombinationDetectionStrategy {

  private final AnalysisFlyweightFactory flyweightFactory;

  public StraightMatchingStrategy(AnalysisFlyweightFactory flyweightFactory) {
    this.flyweightFactory = flyweightFactory;
  }

  @Override
  public CombinationDetectionResult match(List<Card> cards) {
    final var helper = flyweightFactory.get(cards);
    final List<Symbol> symbols = helper.distinctSymbolsDesc();
    final int numberOfSymbols = symbols.size();
    final boolean enoughSymbolsForStraight = numberOfSymbols >= 5;
    if (!enoughSymbolsForStraight) {
      return CombinationDetectionResult.failure(STRAIGHT);
    } else {
      final var candidates = straightCandidates(helper);
      if (candidates.isEmpty()) {
        return CombinationDetectionResult.failure(STRAIGHT);
      } else {
        return CombinationDetectionResult.success(STRAIGHT, candidates);
      }
    }
  }

  private List<List<Card>> straightCandidates(AnalysisFlyweight helper) {
    final List<Symbol> symbols = helper.distinctSymbolsDesc();
    return straightCandidateRanges(symbols, symbols.size())
        .mapToObj(i -> symbols.subList(i, i + 5))
        .map(candidateSymbols -> {
          final var cardsWithSymbols = helper.cardsWith(candidateSymbols);
          final var mostCommonColor = mostCommonColor(cardsWithSymbols);
          return nestedListWithCardsOfSingleSymbol(helper, candidateSymbols)
              .map(list -> pickCardOfCorrectColor(mostCommonColor, list))
              .toList();
        })
        .toList();
  }

  private Color mostCommonColor(List<Card> cardsWithSymbols) {
    final var colorsAndCounts = colorCountMap(cardsWithSymbols);
    return colorsAndCounts.entrySet().stream()
        .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
        .map(Map.Entry::getKey)
        .findFirst().orElseThrow();
  }

  private Card pickCardOfCorrectColor(Color mostCommonColor, List<Card> list) {
    final boolean cardOfPreferredColorAvailable = list.stream()
        .anyMatch(c -> c.color().equals(mostCommonColor));
    if (cardOfPreferredColorAvailable) {
      return list.stream()
          .filter(c -> c.color().equals(mostCommonColor))
          .findFirst().orElseThrow();
    } else {
      return list.getFirst();
    }
  }

  private Stream<List<Card>> nestedListWithCardsOfSingleSymbol(AnalysisFlyweight helper,
      List<Symbol> candidateSymbols) {
    return candidateSymbols.stream()
        .map(helper::cardsWith);
  }

  private Map<Color, Long> colorCountMap(List<Card> cardsWithSymbols) {
    return cardsWithSymbols.stream()
        .map(Card::color)
        .distinct()
        .collect(Collectors.toMap(
            color -> color,
            color -> countOfColorIn(color, cardsWithSymbols)
        ));
  }

  private long countOfColorIn(Color color, List<Card> cards) {
    return cards.stream()
        .filter(card -> card.color().equals(color))
        .count();
  }

  private IntStream straightCandidateRanges(List<Symbol> symbols, int numberOfSymbols) {
    final int top = numberOfSymbols - 4;
    return IntStream.range(0, top)
        .filter(i -> {
          final Symbol big = symbols.get(i);
          final Symbol low = symbols.get(i + 4);
          return big.strength() - low.strength() == 4;
        });
  }
}
