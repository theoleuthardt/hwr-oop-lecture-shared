package hwr.oop.huzur.tests;

import static hwr.oop.huzur.tests.HuzurTestUtil.allCardsOfColor;
import static hwr.oop.huzur.tests.HuzurTestUtil.allCardsOfSign;
import static org.assertj.core.api.Assertions.assertThat;

import hwr.oop.huzur.Color;
import hwr.oop.huzur.Sign;
import hwr.oop.huzur.cards.CardFactory;
import hwr.oop.huzur.cards.Joker;
import java.util.stream.Stream;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class CardsColorsSignsAndJokersTest {

  @Test
  void allHuzurCards_ContainsAll42HuzurCards() {
    final var factory = new CardFactory();
    final var cards = factory.createAllCards();
    assertThat(cards)
        .hasSize(42)
        // all colors
        .containsAll(allCardsOfColor(Color.CLUBS))
        .containsAll(allCardsOfColor(Color.SPADES))
        .containsAll(allCardsOfColor(Color.HEARTS))
        .containsAll(allCardsOfColor(Color.DIAMONDS))
        // all signs
        .containsAll(allCardsOfSign(Sign.TWO))
        .containsAll(allCardsOfSign(Sign.THREE))
        .containsAll(allCardsOfSign(Sign.SEVEN))
        .containsAll(allCardsOfSign(Sign.EIGHT))
        .containsAll(allCardsOfSign(Sign.NINE))
        .containsAll(allCardsOfSign(Sign.TEN))
        .containsAll(allCardsOfSign(Sign.JACK))
        .containsAll(allCardsOfSign(Sign.QUEEN))
        .containsAll(allCardsOfSign(Sign.KING))
        .containsAll(allCardsOfSign(Sign.ACE))
        // all jokers
        .contains(Joker.first(), Joker.second());
  }

  @Test
  void firstAndSecondJoker_HaveNoColor() {
    final var soft = new SoftAssertions();
    Stream.of(Joker.first(), Joker.second()).forEach(card -> {
      soft.assertThat(card.hasColor(Color.CLUBS)).isFalse();
      soft.assertThat(card.hasColor(Color.HEARTS)).isFalse();
      soft.assertThat(card.hasColor(Color.SPADES)).isFalse();
      soft.assertThat(card.hasColor(Color.DIAMONDS)).isFalse();
    });
    soft.assertAll();
  }

  @Test
  void firstAndSecondJoker_HaveNoSign() {
    final var soft = new SoftAssertions();
    Stream.of(Joker.first(), Joker.second()).forEach(card -> {
      soft.assertThat(card.hasSign(Sign.SEVEN)).isFalse();
      soft.assertThat(card.hasSign(Sign.EIGHT)).isFalse();
      soft.assertThat(card.hasSign(Sign.NINE)).isFalse();
      soft.assertThat(card.hasSign(Sign.TEN)).isFalse();
      soft.assertThat(card.hasSign(Sign.JACK)).isFalse();
      soft.assertThat(card.hasSign(Sign.QUEEN)).isFalse();
      soft.assertThat(card.hasSign(Sign.KING)).isFalse();
      soft.assertThat(card.hasSign(Sign.THREE)).isFalse();
      soft.assertThat(card.hasSign(Sign.TWO)).isFalse();
      soft.assertThat(card.hasSign(Sign.ACE)).isFalse();
    });
    soft.assertAll();
  }

  @Test
  void firstAndSecondJoker_DoNotHaveSameColor() {
    final var first = Joker.first();
    final var second = Joker.second();
    final var soft = new SoftAssertions();
    soft.assertThat(first.sameColorAs(second)).isFalse();
    soft.assertThat(second.sameColorAs(first)).isFalse();
    soft.assertAll();
  }
}
