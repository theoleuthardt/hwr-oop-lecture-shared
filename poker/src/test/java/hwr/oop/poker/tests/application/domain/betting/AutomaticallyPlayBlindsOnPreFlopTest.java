package hwr.oop.poker.tests.application.domain.betting;

import static org.assertj.core.api.Assertions.assertThat;

import hwr.oop.poker.application.domain.Player;
import hwr.oop.poker.application.domain.Stacks;
import hwr.oop.poker.application.domain.betting.BettingRound;
import hwr.oop.poker.application.domain.blinds.BlindConfiguration;
import hwr.oop.poker.application.domain.blinds.SmallBlind;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AutomaticallyPlayBlindsOnPreFlopTest {

  private Player firstPlayer;
  private Player secondPlayer;
  private Player thirdPlayer;
  private List<Player> allPlayers;
  private BettingRound round;
  private BlindConfiguration blindConfiguration;

  @BeforeEach
  void setUp() {
    firstPlayer = new Player("1");
    secondPlayer = new Player("2");
    thirdPlayer = new Player("3");
    allPlayers = List.of(
        firstPlayer,
        secondPlayer,
        thirdPlayer
    );
    Stacks stacks = Stacks.newBuilder()
        .of(firstPlayer).is(100000)
        .of(secondPlayer).is(100000)
        .of(thirdPlayer).is(100000)
        .build();
    blindConfiguration = BlindConfiguration.create(SmallBlind.of(1));
    round = BettingRound.createPreFlop(stacks, blindConfiguration, allPlayers);
  }

  @Test
  void potSizeConsistsOfBlinds() {
    final var potSize = round.pot();
    final var blindsValue = blindConfiguration.blindsValue();
    assertThat(potSize).isEqualTo(blindsValue);
  }

  @Test
  void thirdAndFirstPlayersCall_RoundIsNotFinished() {
    final var afterFirstPlayerCallsBigBlind = round
        .with(thirdPlayer).call()
        .with(firstPlayer).call();
    final var finished = afterFirstPlayerCallsBigBlind.isFinished();
    assertThat(finished).isFalse();
  }

  @Test
  void allPlayersCall_BigBlindChecks_RoundIsFinished() {
    final var afterBigBlindChecks = round
        .with(thirdPlayer).call()
        .with(firstPlayer).call()
        .with(secondPlayer).check();
    final var finished = afterBigBlindChecks.isFinished();
    assertThat(finished).isTrue();
  }

  @Test
  void allPlayersFold_BigBlindWins() {
    final var afterFirstPlayerFold = round
        .with(thirdPlayer).fold()
        .with(firstPlayer).fold();
    final var finished = afterFirstPlayerFold.isFinished();
    assertThat(finished).isTrue();
  }
}
