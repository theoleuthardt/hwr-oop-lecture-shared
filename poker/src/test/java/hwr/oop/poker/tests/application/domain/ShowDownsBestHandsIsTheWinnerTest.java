package hwr.oop.poker.tests.application.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hwr.oop.poker.application.domain.Converter;
import hwr.oop.poker.application.domain.HoleCards;
import hwr.oop.poker.application.domain.Player;
import hwr.oop.poker.application.domain.ShowDown;
import hwr.oop.poker.application.domain.cards.CommunityCards;
import hwr.oop.poker.application.domain.combinations.Combination;
import hwr.oop.poker.application.domain.decks.UnshuffledDeck;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ShowDown (isolated) Test")
class ShowDownsBestHandsIsTheWinnerTest {

  private ShowDown showDown;
  private Player firstPlayer;
  private Player secondPlayer;

  @BeforeEach
  void setUp() {
    final var converter = Converter.create();
    firstPlayer = new Player("1");
    secondPlayer = new Player("2");
    final var deck = new UnshuffledDeck(
        converter.convert("AH,TS,AC,2S")
    );
    final var players = List.of(firstPlayer, secondPlayer);
    final var holeCards = HoleCards.createByDrawingFromDeck(deck, players);
    final var communityCards = CommunityCards
        .flop(converter.convert("TC,TH,2H"))
        .turn(converter.from("KS"))
        .river(converter.from("AS"));
    showDown = ShowDown.create(communityCards, holeCards, players);
  }

  @Test
  void firstPlayer_FullHouse() {
    final var combo = showDown.combination(firstPlayer);
    final var firstPlayerLabel = combo.label();
    assertThat(firstPlayerLabel).isEqualTo(Combination.Label.FULL_HOUSE);
  }

  @Test
  void secondPlayer_FullHouse() {
    final var combo = showDown.combination(secondPlayer);
    final var secondPlayerLabel = combo.label();
    assertThat(secondPlayerLabel).isEqualTo(Combination.Label.FULL_HOUSE);
  }

  @Test
  void firstCombo_IsGreaterThanSecondCombo() {
    final var firstCombo = showDown.combination(firstPlayer);
    final var secondCombo = showDown.combination(secondPlayer);
    assertThat(firstCombo).isGreaterThan(secondCombo);
  }

  @Test
  void winner_FirstPlayer() {
    final var winner = showDown.winner();
    assertThat(winner).isEqualTo(firstPlayer);
  }

  @Test
  void queryingUnsupportedPlayer_ThrowsRuntimeException() {
    final var otherPlayer = new Player("3");
    assertThatThrownBy(() -> showDown.combination(otherPlayer))
        .isInstanceOf(ShowDown.InvalidPlayerException.class)
        .hasMessageContainingAll(
            otherPlayer.toString(), "expected:",
            firstPlayer.toString(), secondPlayer.toString()
        );
  }
}
