package hwr.oop.huzur.cards;

import hwr.oop.huzur.Card;
import hwr.oop.huzur.Color;
import hwr.oop.huzur.Sign;
import java.util.Arrays;
import java.util.stream.Stream;

public class CardFactory {

  public Stream<Card> createNormalCards() {
    return Arrays.stream(Color.values())
        .flatMap(this::createCard);
  }

  public Stream<Card> createAllCards() {
    return Stream.concat(createNormalCards(), Stream.of(Joker.first(), Joker.second()));
  }

  private Stream<Card> createCard(Color color) {
    return Arrays.stream(Sign.values())
        .map(sign -> new NormalCard(color, sign));
  }
}
