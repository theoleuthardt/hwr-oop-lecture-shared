package hwr.oop.poker.tests.domain.hands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import hwr.oop.poker.application.domain.Deck;
import hwr.oop.poker.application.domain.Hand;
import hwr.oop.poker.application.domain.Player;
import hwr.oop.poker.application.domain.Stacks;
import hwr.oop.poker.application.domain.betting.BettingRound;
import hwr.oop.poker.application.domain.blinds.BlindConfiguration;
import hwr.oop.poker.application.domain.blinds.SmallBlind;
import hwr.oop.poker.application.domain.decks.RandomDeck;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FoldedPlayersAreOutTest {

  private Hand hand;
  private Player firstPlayer;
  private Player secondPlayer;
  private Player thirdPlayer;


  @BeforeEach
  void setUp() {
    firstPlayer = new Player("1");
    secondPlayer = new Player("2");
    thirdPlayer = new Player("3");
    final Deck deck = new RandomDeck();
    final var stacks = Stacks.newBuilder()
        .of(firstPlayer).is(100)
        .of(secondPlayer).is(200)
        .of(thirdPlayer).is(300)
        .build();
    final var players = List.of(firstPlayer, secondPlayer, thirdPlayer);
    final var smallBlind = SmallBlind.of(1);
    final var blindConfiguration = BlindConfiguration.create(smallBlind);
    this.hand = Hand.newBuilder()
        .deck(deck)
        .players(players)
        .stacks(stacks)
        .blindConfiguration(blindConfiguration)
        .build();
  }

  @Test
  void foldCallCheck_ThirdPlayerIsNotPlayingAnymore() {
    final var afterFirstRound = hand.onCurrentRound(this::firstRoundThirdPlayerFolds);
    final var remainingPlayers = afterFirstRound.remainingPlayers();
    assertThat(remainingPlayers)
        .isNotEmpty()
        .doesNotContain(thirdPlayer);
  }

  @Test
  void foldCallCheck_CheckBet_CallOnFirstPlayer_DoesNotThrowException() {
    final var firstPlayerToCallTen = hand
        .onCurrentRound(this::firstRoundThirdPlayerFolds)
        .onCurrentRound(r -> r.with(firstPlayer).check().with(secondPlayer).bet(10));
    assertDoesNotThrow(
        () -> firstPlayerToCallTen.onCurrentRound(r -> r.with(firstPlayer).call())
    );
  }

  @Test
  void foldCallCheck_CheckBet_CallOnThirdPlayer_ThrowsException() {
    final var firstPlayerToCallTen = hand
        .onCurrentRound(this::firstRoundThirdPlayerFolds)
        .onCurrentRound(r -> r.with(firstPlayer).check().with(secondPlayer).bet(10));
    assertThatThrownBy(() -> firstPlayerToCallTen.onCurrentRound(r -> r.with(thirdPlayer).call()))
        .isInstanceOf(BettingRound.InvalidPlayOnStateException.class)
        .hasMessageContainingAll(
            "wrong player: " + thirdPlayer,
            "next player is: " + firstPlayer
        );
  }

  private BettingRound firstRoundThirdPlayerFolds(BettingRound round) {
    return round
        .with(thirdPlayer).fold()
        .with(firstPlayer).call()
        .with(secondPlayer).check();
  }
}
