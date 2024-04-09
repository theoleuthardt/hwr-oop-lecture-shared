package hwr.oop.huzur.tests;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hwr.oop.huzur.cards.CardConverter;
import hwr.oop.huzur.Color;
import hwr.oop.huzur.cards.Joker;
import hwr.oop.huzur.Sign;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StringShortcutsForCardsTest {

  private CardConverter converter;

  @BeforeEach
  void setUp() {
    converter = new CardConverter();
  }

  @ParameterizedTest
  @CsvSource(delimiter = ',', value = {
      "C,CLUBS",
      "H,HEARTS",
      "D,DIAMONDS",
      "S,SPADES",
  })
  void convertColors(String string, Color color) {
    final var cardString = string + "A";
    final var card = converter.convert(cardString);
    final var soft = new SoftAssertions();
    soft.assertThat(card.hasColor(color)).isTrue();
    soft.assertThat(card.hasSign(Sign.ACE)).isTrue();
    soft.assertAll();
  }

  @ParameterizedTest
  @CsvSource(delimiter = ',', value = {
      "7,SEVEN",
      "8,EIGHT",
      "9,NINE",
      "T,TEN",
      "J,JACK",
      "Q,QUEEN",
      "K,KING",
      "3,THREE",
      "2,TWO",
      "A,ACE",
  })
  void convertSigns(String string, Sign sign) {
    final var cardString = "H" + string;
    final var card = converter.convert(cardString);
    final var soft = new SoftAssertions();
    soft.assertThat(card.hasColor(Color.HEARTS)).isTrue();
    soft.assertThat(card.hasSign(sign)).isTrue();
    soft.assertAll();
  }

  @Test
  void convert_J1_FirstJoker() {
    final var card = converter.convert("J1");
    final var soft = new SoftAssertions();
    soft.assertThat(card).isEqualTo(Joker.first());
    soft.assertThat(card).isNotEqualTo(Joker.second());
    soft.assertAll();
  }

  @Test
  void convert_J2_SecondJoker() {
    final var card = converter.convert("J2");
    final var soft = new SoftAssertions();
    soft.assertThat(card).isEqualTo(Joker.second());
    soft.assertThat(card).isNotEqualTo(Joker.first());
    soft.assertAll();
  }

  @Test
  void convert_ThreeSignString_Exception() {
    assertThatThrownBy(() -> converter.convert("AHH"))
        .hasMessageContainingAll("AHH", "expected two", "got");
  }

  @Test
  void convert_SingleSignString_Exception() {
    assertThatThrownBy(() -> converter.convert("A"))
        .hasMessageContainingAll("A", "expected two", "got");
  }

  @Test
  void convert_ColorL_UnknownColor_Exception() {
    assertThatThrownBy(() -> converter.convert("LA"))
        .hasMessageContainingAll("L", "Cannot convert Color");
  }

  @Test
  void convert_SignL_UnknownSign_Exception() {
    assertThatThrownBy(() -> converter.convert("DL"))
        .hasMessageContainingAll("L", "Cannot convert Sign");
  }
}
