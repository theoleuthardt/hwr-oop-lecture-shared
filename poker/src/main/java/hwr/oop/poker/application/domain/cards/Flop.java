package hwr.oop.poker.application.domain.cards;

import hwr.oop.poker.application.domain.Card;
import java.util.List;
import java.util.stream.Stream;

public class Flop implements Card.Provider {

  private final List<Card> cards;

  public static Flop of(List<Card> list) {
    return new Flop(list);
  }

  private Flop(List<Card> cards) {
    assert cards != null && cards.size() == 3;
    this.cards = cards;
  }

  @Override
  public Stream<Card> cards() {
    return cards.stream();
  }

  @Override
  public String toString() {
    return "Flop{" + cards + '}';
  }

}
