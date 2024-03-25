package hwr.oop.poker.application.domain.combinations;

import hwr.oop.poker.application.domain.Card;
import hwr.oop.poker.application.domain.Color;
import hwr.oop.poker.application.domain.Symbol;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class AnalysisFlyweight {

  private final List<Card> cards;
  private final Map<Symbol, List<Card>> symbolMap;
  private final Map<Color, Long> colorCountMap;

  public static AnalysisFlyweight create(List<Card> cards) {
    return new AnalysisFlyweight(cards);
  }

  public AnalysisFlyweight(List<Card> cards) {
    this.cards = cards;
    this.symbolMap = createSymbolMap();
    this.colorCountMap = createColorCountMap();
  }

  public Stream<Symbol> symbolsWithPairs() {
    return symbolsThatArePresent(2);
  }

  public Stream<Symbol> symbolsWithTrips() {
    return symbolsThatArePresent(3);
  }

  public Stream<Symbol> symbolsWithQuads() {
    return symbolsThatArePresent(4);
  }

  public List<Card> cardsWith(Symbol... symbols) {
    return cardsWith(Arrays.asList(symbols));
  }

  public List<Card> cardsWith(List<Symbol> symbols) {
    return symbols.stream()
        .map(symbolMap::get)
        .flatMap(Collection::stream)
        .toList();
  }

  public List<Symbol> distinctSymbolsDesc() {
    return symbolMap.keySet().stream()
        .sorted(Symbol.DESCENDING_BY_STRENGTH)
        .toList();
  }

  private Stream<Symbol> distinctSymbols() {
    return cards.stream()
        .map(Card::symbol)
        .distinct();
  }

  public Color mostCommonColor() {
    return colorCountMap.entrySet().stream()
        .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
        .map(Map.Entry::getKey)
        .findFirst().orElseThrow();
  }

  private Stream<Symbol> symbolsThatArePresent(int numberOfTimesPresent) {
    return symbolMap.entrySet().stream()
        .filter(e -> hasValueOfSize(e, numberOfTimesPresent))
        .map(Map.Entry::getKey);
  }

  private boolean hasValueOfSize(Map.Entry<Symbol, List<Card>> entry, int numberOfTimesPresent) {
    final int value = entry.getValue().size();
    return value == numberOfTimesPresent;
  }

  private Map<Symbol, List<Card>> createSymbolMap() {
    final Map<Symbol, List<Card>> mutableMap = new EnumMap<>(Symbol.class);
    final var symbols = distinctSymbols();
    symbols.forEach(s -> mutableMap.put(s, new ArrayList<>()));
    cards.forEach(c -> mutableMap.get(c.symbol()).add(c));
    return Collections.unmodifiableMap(mutableMap);
  }

  private Map<Color, Long> createColorCountMap() {
    return distinctColors(cards)
        .collect(Collectors.toMap(
            color -> color,
            this::countOfColorIn
        ));
  }

  private Stream<Color> distinctColors(List<Card> cards) {
    return cards.stream()
        .map(Card::color)
        .distinct();
  }

  private long countOfColorIn(Color color) {
    return cards.stream()
        .filter(card -> card.color().equals(color))
        .count();
  }
}
