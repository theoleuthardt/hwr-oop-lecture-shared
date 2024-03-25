package hwr.oop.poker.tests.application.domain;

import static org.assertj.core.api.Assertions.assertThat;

import hwr.oop.poker.application.domain.Card;
import hwr.oop.poker.application.domain.Color;
import hwr.oop.poker.application.domain.Converter;
import hwr.oop.poker.application.domain.Symbol;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Parse cards from Strings (makes testing easier)")
class EasierCardImportsForTestsTest {

  private Converter converter;

  @BeforeEach
  void setUp() {
    converter = Converter.create();
  }

  @Test
  @DisplayName("7H -> SEVEN of HEARTS")
  void string7H_IsSevenOfHearts() {
    final String sevenOfHeartsString = "7H";
    final Card card = converter.from(sevenOfHeartsString);
    assertThat(card.symbol()).isEqualTo(Symbol.SEVEN);
    assertThat(card.color()).isEqualTo(Color.HEARTS);
  }

  @Test
  @DisplayName("AS -> ACE of SPADES")
  void stringAS_IsSevenOfHearts() {
    final String aceOfSpadesString = "AS";
    final Card card = converter.from(aceOfSpadesString);
    assertThat(card.symbol()).isEqualTo(Symbol.ACE);
    assertThat(card.color()).isEqualTo(Color.SPADES);
  }

  @Test
  @DisplayName("8C -> EIGHT of CLUBS")
  void string8C_IsEightOfClubs() {
    final String aceOfSpadesString = "8C";
    final Card card = converter.from(aceOfSpadesString);
    assertThat(card.symbol()).isEqualTo(Symbol.EIGHT);
    assertThat(card.color()).isEqualTo(Color.CLUBS);
  }

  @Test
  @DisplayName("JD -> JACK of DIAMONDS")
  void stringJD_IsJackOfDiamonds() {
    final String aceOfSpadesString = "JD";
    final Card card = converter.from(aceOfSpadesString);
    assertThat(card.symbol()).isEqualTo(Symbol.JACK);
    assertThat(card.color()).isEqualTo(Color.DIAMONDS);
  }

  @Test
  @DisplayName("JD,JS -> JACK of DIAMONDS and JACK of SPADES")
  void twoJacksCommaSeparated_JacksOfDiamondsAndSpades() {
    final String twoJacks = "JD,JS";
    final List<Card> card = converter.convert(twoJacks);
    assertThat(card).containsExactlyInAnyOrder(
        new Card(Color.DIAMONDS, Symbol.JACK),
        new Card(Color.SPADES, Symbol.JACK)
    );
  }

  @Test
  @DisplayName("TS,2S,4S,6S,8S -> TEN, TWO, FOUR, SIX, EIGHT of SPADES")
  void fiveSpadesCommaSeparated_ConvertedToMatchingCards() {
    final String twoJacks = "TS,2S,4S,6S,8S";
    final List<Card> card = converter.convert(twoJacks);
    assertThat(card).containsExactlyInAnyOrder(
        new Card(Color.SPADES, Symbol.TWO),
        new Card(Color.SPADES, Symbol.FOUR),
        new Card(Color.SPADES, Symbol.SIX),
        new Card(Color.SPADES, Symbol.EIGHT),
        new Card(Color.SPADES, Symbol.TEN)
    );
  }
}
