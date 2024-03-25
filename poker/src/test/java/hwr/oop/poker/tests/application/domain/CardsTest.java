package hwr.oop.poker.tests.application.domain;

import static org.assertj.core.api.Assertions.assertThat;

import hwr.oop.poker.application.domain.Card;
import hwr.oop.poker.application.domain.Color;
import hwr.oop.poker.application.domain.Converter;
import hwr.oop.poker.application.domain.Symbol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Creating Cards")
class CardsTest {

  @ParameterizedTest
  @DisplayName("create all 4 cards with symbol SEVEN")
  @ValueSource(strings = {"SPADES", "HEARTS", "DIAMONDS", "CLUBS"})
  void canCreateSevenForEachOfTheFourColors(String colorString) {
    final Symbol seven = Symbol.SEVEN;
    final Color expectedColor = Color.valueOf(colorString);

    final Card card = new Card(expectedColor, seven);
    final Color color = card.color();
    final Symbol number = card.symbol();

    assertThat(color).isEqualTo(expectedColor);
    assertThat(number).isEqualTo(seven);
  }


  @ParameterizedTest
  @DisplayName("create all cards with color HEARTS")
  @ValueSource(strings = {
      "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "JACK", "QUEEN",
      "KING", "ACE"
  })
  void canCreateAllCardsWithHearts(String numberString) {
    final Symbol expectedNumber = Symbol.valueOf(numberString);
    final Color hearts = Color.HEARTS;

    final Card card = new Card(hearts, expectedNumber);
    final Color color = card.color();
    final Symbol number = card.symbol();

    assertThat(color).isEqualTo(hearts);
    assertThat(number).isEqualTo(expectedNumber);
  }

  @ParameterizedTest
  @DisplayName("#toString: {number} of {color}")
  @CsvSource({
      "7H, SEVEN of HEARTS",
      "JH,  JACK of HEARTS",
      "3S, THREE of SPADES",
      "AS,   ACE of SPADES",
      "QD, QUEEN of DIAMONDS",
      "TD,   TEN of DIAMONDS",
      "2C,   TWO of CLUBS",
      "KC,  KING of CLUBS"
  })
  void exampleCards_ToString_IsLikeSymbolOfColor(String card, String expectedToString) {
    final Card parsedCard = Converter.create().from(card);
    final String toString = parsedCard.toString();
    assertThat(toString).isEqualTo(expectedToString);
  }

  @ParameterizedTest
  @DisplayName("#equals: equal cards, true")
  @ValueSource(strings = {"TH", "AS", "2D", "JC"})
  void equals_EqualCards_true(String card) {
    Converter converter = Converter.create();
    final Card first = converter.from(card);
    final Card second = converter.from(card);
    assertThat(first).isEqualTo(second);
  }

  @ParameterizedTest
  @DisplayName("#equals: non-equal cards, false")
  @CsvSource({
      "TH, JH",
      "AH, KH",
      "TH, TC",
      "7D, 7S",
      "9H, TC",
      "2D, KS"
  })
  void equals_NonEqualCards_false(String firstCard, String secondCard) {
    Converter converter = Converter.create();
    final Card first = converter.from(firstCard);
    final Card second = converter.from(secondCard);
    assertThat(first).isNotEqualTo(second);
  }
}
