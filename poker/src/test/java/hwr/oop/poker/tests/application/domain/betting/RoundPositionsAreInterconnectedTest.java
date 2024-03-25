package hwr.oop.poker.tests.application.domain.betting;

import static org.assertj.core.api.Assertions.assertThat;

import hwr.oop.poker.application.domain.betting.positions.RoundPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoundPositionsAreInterconnectedTest {

  private RoundPosition preFlop;
  private RoundPosition flop;
  private RoundPosition turn;
  private RoundPosition river;

  @BeforeEach
  void setUp() {
    preFlop = RoundPosition.PRE_FLOP;
    flop = RoundPosition.FLOP;
    turn = RoundPosition.TURN;
    river = RoundPosition.RIVER;
  }

  @Test
  void preFlop_HasLowestPosition() {
    final int position = preFlop.position();
    assertThat(position)
        .isLessThan(flop.position())
        .isLessThan(turn.position())
        .isLessThan(river.position());
  }

  @Test
  void preFlop_IsDistinctPosition() {
    assertThat(preFlop)
        .isNotEqualTo(flop)
        .isNotEqualTo(turn)
        .isNotEqualTo(river);
  }

  @Test
  void preFlop_ShouldNotCauseBurn() {
    final var shouldCauseBurn = preFlop.shouldCauseBurn();
    assertThat(shouldCauseBurn).isFalse();
  }

  @Test
  void preFlop_BuildsEmptyCommunityCards() {
    final var communityCards = preFlop.buildCardsFor(null, null);
    final var cardsBuildForPreFlop = communityCards.cardsDealt();
    assertThat(cardsBuildForPreFlop).isEmpty();
  }

  @Test
  void preFlop_PreviousIsEmpty() {
    final var previous = preFlop.previous();
    assertThat(previous).isEmpty();
  }

  @Test
  void preFlop_NextIsFlop() {
    final var nextPosition = preFlop.nextPosition();
    assertThat(nextPosition).isPresent().get().isEqualTo(flop);
  }

  @Test
  void flop_HasSecondLowestPosition() {
    final int position = flop.position();
    assertThat(position)
        .isGreaterThan(preFlop.position())
        .isLessThan(turn.position())
        .isLessThan(river.position());
  }

  @Test
  void flop_IsDistinctPosition() {
    assertThat(flop)
        .isNotEqualTo(preFlop)
        .isNotEqualTo(turn)
        .isNotEqualTo(river);
  }

  @Test
  void flop_PreviousIsPreFlop() {
    final var previous = flop.previous();
    assertThat(previous).isPresent().get().isEqualTo(preFlop);
  }

  @Test
  void flop_NextIsTurn() {
    final var nextPosition = flop.nextPosition();
    assertThat(nextPosition).isPresent().get().isEqualTo(turn);
  }

  @Test
  void turn_HasSecondHighestPosition() {
    final int position = turn.position();
    assertThat(position)
        .isGreaterThan(preFlop.position())
        .isGreaterThan(flop.position())
        .isLessThan(river.position());
  }

  @Test
  void turn_IsDistinctPosition() {
    assertThat(turn)
        .isNotEqualTo(preFlop)
        .isNotEqualTo(flop)
        .isNotEqualTo(river);
  }

  @Test
  void turn_PreviousIsFlop() {
    final var previous = turn.previous();
    assertThat(previous).isPresent().get().isEqualTo(flop);
  }

  @Test
  void turn_NextIsRiver() {
    final var nextPosition = turn.nextPosition();
    assertThat(nextPosition).isPresent().get().isEqualTo(river);
  }

  @Test
  void river_HasHighestPosition() {
    final int position = river.position();
    assertThat(position)
        .isGreaterThan(preFlop.position())
        .isGreaterThan(flop.position())
        .isGreaterThan(turn.position());
  }

  @Test
  void river_IsDistinctPosition() {
    assertThat(river)
        .isNotEqualTo(preFlop)
        .isNotEqualTo(flop)
        .isNotEqualTo(turn);
  }

  @Test
  void river_PreviousIsTurn() {
    final var previous = river.previous();
    assertThat(previous).isPresent().get().isEqualTo(turn);
  }

  @Test
  void river_NoNextPosition() {
    final var nextPosition = river.nextPosition();
    assertThat(nextPosition).isEmpty();
  }
}
