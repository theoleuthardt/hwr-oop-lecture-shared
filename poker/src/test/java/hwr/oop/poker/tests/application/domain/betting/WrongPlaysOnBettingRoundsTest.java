package hwr.oop.poker.tests.application.domain.betting;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hwr.oop.poker.application.domain.Player;
import hwr.oop.poker.application.domain.Stacks;
import hwr.oop.poker.application.domain.betting.BettingRound;
import hwr.oop.poker.application.domain.betting.RoundInContext;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WrongPlaysOnBettingRoundsTest {

  private Player firstPlayer;
  private Player secondPlayer;
  private Player thirdPlayer;
  private BettingRound round;

  @BeforeEach
  void setUp() {
    firstPlayer = new Player("1");
    secondPlayer = new Player("2");
    thirdPlayer = new Player("3");
    List<Player> players = List.of(
        firstPlayer,
        secondPlayer,
        thirdPlayer
    );
    Stacks stacks = Stacks.newBuilder()
        .of(firstPlayer).is(100000)
        .of(secondPlayer).is(100000)
        .of(thirdPlayer).is(100000)
        .build();
    round = BettingRound.create(stacks, players);
  }

  @Test
  void callBeforeBet_RaisesException() {
    final RoundInContext first = round
        .with(firstPlayer);

    assertThatThrownBy(first::call)
        .isInstanceOf(BettingRound.InvalidPlayOnStateException.class)
        .hasMessageContainingAll(
            "Cannot CALL",
            "no BET to CALL/RAISE/FOLD on"
        );
  }

  @Test
  void raiseBeforeBet_RaisesException() {
    final RoundInContext first = round
        .with(firstPlayer);

    assertThatThrownBy(() -> first.raiseTo(1337))
        .isInstanceOf(BettingRound.InvalidPlayOnStateException.class)
        .hasMessageContainingAll(
            "Cannot RAISE",
            "no BET to CALL/RAISE/FOLD on"
        );
  }

  @Test
  void checkAfterBet_RaisesException() {
    final RoundInContext second = round
        .with(firstPlayer).bet(42)
        .with(secondPlayer);

    assertThatThrownBy(second::check)
        .isInstanceOf(BettingRound.InvalidPlayOnStateException.class)
        .hasMessageContainingAll(
            "Cannot CHECK",
            "need to CALL/RAISE/FOLD to",
            firstPlayer.toString()
        );
  }

  @Test
  void betAfterBet_RaisesException() {
    final RoundInContext second = round
        .with(firstPlayer).bet(42)
        .with(secondPlayer);

    assertThatThrownBy(() -> second.bet(82))
        .isInstanceOf(BettingRound.InvalidPlayOnStateException.class)
        .hasMessageContainingAll(
            "Cannot BET",
            "need to CALL/RAISE/FOLD to",
            firstPlayer.toString()
        );
  }

  @Test
  void startingWithSecondInsteadOfFirstPlayer_RaisesException() {
    final RoundInContext second = round
        .with(secondPlayer);

    assertThatThrownBy(() -> second.bet(1337))
        .isInstanceOf(BettingRound.InvalidPlayOnStateException.class)
        .hasMessageContainingAll(
            "wrong player: " + secondPlayer,
            "next player is: " + firstPlayer
        );
  }

  @Test
  void incorrectlySkippingSecondPlayer_RaisesException() {
    final RoundInContext third = round
        .with(firstPlayer).check()
        .with(thirdPlayer);

    assertThatThrownBy(third::check)
        .isInstanceOf(BettingRound.InvalidPlayOnStateException.class)
        .hasMessageContainingAll(
            "wrong player: " + thirdPlayer,
            "next player is: " + secondPlayer
        );
  }

  @Test
  void raisingToLessThanTheDoubleOfTheBet_RaisesException() {
    final RoundInContext third = round
        .with(firstPlayer).bet(42)
        .with(secondPlayer);

    assertThatThrownBy(() -> third.raiseTo(60))
        .isInstanceOf(BettingRound.InvalidPlayOnStateException.class)
        .hasMessageContainingAll(
            "Cannot RAISE",
            "BET is 42",
            "expected RAISE to 82 or higher",
            "got 60"
        );
  }
}
