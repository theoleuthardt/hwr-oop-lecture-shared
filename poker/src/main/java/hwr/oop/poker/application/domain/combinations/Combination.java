package hwr.oop.poker.application.domain.combinations;

import hwr.oop.poker.application.domain.Card;
import java.util.List;
import java.util.stream.Stream;

public final class Combination implements Comparable<Combination> {

  private final List<Card> nonKickers;
  private final List<Card> kickers;
  private final List<Card> cards;
  private Label label;

  public static Combination of(List<Card> cards) {
    return new Combination(cards);
  }

  public Combination(List<Card> cards) {
    this.nonKickers = selectNonKickerCards(cards);
    this.kickers = selectKickers(cards);
    this.cards = Stream.concat(nonKickers.stream(), kickers.stream()).toList();
  }

  public Combination.Label label() {
    return label;
  }

  public List<Card> cards() {
    return cards;
  }

  public List<Card> kickers() {
    return kickers;
  }

  private List<Card> selectNonKickerCards(List<Card> cards) {
    final var factory = CombinationDetectionStrategyFactory.create();
    final var strategies = factory.createAll();
    for (var strategy : strategies) {
      final var result = strategy.match(cards);
      if (result.successful()) {
        this.label = result.label();
        return result.winner();
      }
    }
    this.label = Label.HIGH_CARD;
    return List.of();
  }

  private List<Card> selectKickers(List<Card> cards) {
    final List<Card> kickerCandidates = cards.stream()
        .filter(c -> !nonKickers.contains(c))
        .sorted(Card.DESCENDING_BY_SYMBOL_STRENGTH)
        .toList();
    return kickerCandidates.subList(0, numberOfKickersRequired());
  }

  private int numberOfKickersRequired() {
    return 5 - nonKickers.size();
  }

  public boolean over(Combination other) {
    return compareTo(other) > 0;
  }

  @Override
  public int compareTo(Combination other) {
    final int strengthComparatorResult = Integer.compare(this.label().strength(),
        other.label().strength());
    if (strengthComparatorResult != 0) {
      return strengthComparatorResult;
    }
    final var comparator = Card.ASCENDING_BY_SYMBOL_STRENGTH;
    final var instanceCards = this.cards();
    final var otherCards = other.cards();
    for (int i = 0; i < instanceCards.size(); i++) {
      final var card = instanceCards.get(i);
      final var otherCard = otherCards.get(i);
      final int result = comparator.compare(card, otherCard);
      if (result != 0) {
        return result;
      }
    }
    return 0;
  }

  public enum Label {
    HIGH_CARD(0), PAIR(1), TWO_PAIRS(2), TRIPS(3), STRAIGHT(4), FLUSH(5), FULL_HOUSE(6), QUADS(
        7), STRAIGHT_FLUSH(8);

    private final int strength;

    Label(int strength) {
      this.strength = strength;
    }

    public int strength() {
      return strength;
    }
  }
}
