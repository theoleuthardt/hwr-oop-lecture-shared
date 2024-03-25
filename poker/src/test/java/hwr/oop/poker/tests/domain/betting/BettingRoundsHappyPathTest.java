package hwr.oop.poker.tests.domain.betting;

import hwr.oop.poker.application.domain.ChipValue;
import hwr.oop.poker.application.domain.Player;
import hwr.oop.poker.application.domain.Stacks;
import hwr.oop.poker.application.domain.betting.BettingRound;
import hwr.oop.poker.application.domain.betting.Play;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BettingRoundsHappyPathTest {

    private Player firstPlayer;
    private Player secondPlayer;
    private Player thirdPlayer;
    private List<Player> allPlayers;
    private BettingRound round;

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
        round = BettingRound.create(stacks, allPlayers);
    }

    @Test
    void newBettingRound_AllPlayersPlaying() {
        final Collection<Player> remainingPlayers = round.remainingPlayers();
        assertThat(remainingPlayers).containsExactlyInAnyOrderElementsOf(allPlayers);
    }

    @Test
    void newBettingRound_PodIsEmpty() {
        final ChipValue potSize = round.pot();
        assertThat(potSize).isEqualTo(ChipValue.of(0));
    }

    @Test
    void newBettingRound_LastPlayIsEmpty() {
        final Optional<Play> play = round.lastPlay();
        assertThat(play).isEmpty();
    }

    @Test
    void newBettingRound_IsNotFinished_ActionIsOnFirstPlayer() {
        final boolean finished = round.isFinished();
        assertThat(finished).isFalse();
        final Optional<Player> player = round.turn();
        assertThat(player).isPresent().get().isSameAs(firstPlayer);
    }

    @Test
    void firstPlayerBets10Chips_RoundIsNotFinished_ActionOnSecondPlayer() {
        final BettingRound updatedBettingRound =
                round.with(firstPlayer).bet(10);

        final boolean finished = updatedBettingRound.isFinished();
        assertThat(finished).isFalse();
        final Optional<Player> turn = updatedBettingRound.turn();
        assertThat(turn).isPresent().get().isSameAs(secondPlayer);
    }

    @Test
    void firstPlayerBets10Chips_LastPlayIsBetOf10Chips() {
        final BettingRound updatedBettingRound =
                round.with(firstPlayer).bet(10);

        final Optional<Play> play = updatedBettingRound.lastPlay();
        assertThat(play).isPresent();
        final Play revealedPlay = play.get();
        final Player player = revealedPlay.player();
        assertThat(player).isEqualTo(firstPlayer);
        final ChipValue value = revealedPlay.chipValue();
        assertThat(value).isEqualTo(ChipValue.of(10));
        final Play.Type type = revealedPlay.type();
        assertThat(type).isEqualTo(Play.Type.BET);
    }

    @Test
    void firstPlayerBets10Chips_PodSize_10Chips() {
        final BettingRound updatedBettingRound =
                round.with(firstPlayer).bet(10);

        final ChipValue potSize = updatedBettingRound.pot();
        assertThat(potSize).isEqualTo(ChipValue.of(10));
    }

    @Test
    void firstPlayerBets10Chips_RemainingPlayers_AllStillPresent() {
        final BettingRound updatedBettingRound =
                round.with(firstPlayer).bet(10);

        final Collection<Player> remainingPlayers = updatedBettingRound.remainingPlayers();
        assertThat(remainingPlayers).containsExactlyInAnyOrderElementsOf(allPlayers);
    }

    @Test
    void secondPlayerCallsFirstPlayersBet_RoundIsNotFinished_ActionIsOnThirdPlayer() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call();

        final boolean finished = updatedBettingRound.isFinished();
        assertThat(finished).isFalse();

        final Optional<Player> turn = updatedBettingRound.turn();
        assertThat(turn).isPresent().get().isSameAs(thirdPlayer);
    }

    @Test
    void secondPlayerCallsFirstPlayersBet_LastPlayIsCallOf10Chips() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call();

        final Optional<Play> play = updatedBettingRound.lastPlay();
        assertThat(play).isPresent();
        final Play revealedPlay = play.get();
        final Player player = revealedPlay.player();
        assertThat(player).isEqualTo(secondPlayer);
        final ChipValue value = revealedPlay.chipValue();
        assertThat(value).isEqualTo(ChipValue.of(10));
        final Play.Type type = revealedPlay.type();
        assertThat(type).isEqualTo(Play.Type.CALL);
    }

    @Test
    void secondPlayerCallsFirstPlayersBet_PodSizeOf20Chips() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call();

        final ChipValue potSize = updatedBettingRound.pot();
        assertThat(potSize).isEqualTo(ChipValue.of(20));
    }

    @Test
    void secondPlayerCallsFirstPlayersBet_AllPlayersStillPlaying() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call();

        final Collection<Player> remainingPlayers = updatedBettingRound.remainingPlayers();
        assertThat(remainingPlayers).containsExactlyInAnyOrderElementsOf(allPlayers);
    }

    @Test
    void thirdAndSecondPlayerCalled_RoundIsFinished_NoMoreActionRequired() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call()
                .with(thirdPlayer).call();

        final boolean finished = updatedBettingRound.isFinished();
        assertThat(finished).isTrue();
        final Optional<Player> turn = updatedBettingRound.turn();
        assertThat(turn).isNotPresent();
    }

    @Test
    void thirdAndSecondPlayerCalled_LastPlayIsCallOf10Chips() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call()
                .with(thirdPlayer).call();

        final Optional<Play> play = updatedBettingRound.lastPlay();
        assertThat(play).isPresent();

        final Play revealedPlay = play.get();
        final Player player = revealedPlay.player();
        assertThat(player).isEqualTo(thirdPlayer);
        final ChipValue value = revealedPlay.chipValue();
        assertThat(value).isEqualTo(ChipValue.of(10));
        final Play.Type type = revealedPlay.type();
        assertThat(type).isEqualTo(Play.Type.CALL);
    }

    @Test
    void thirdAndSecondPlayerCalled_PotSizeOf30Chips() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call()
                .with(thirdPlayer).call();

        final ChipValue potSize = updatedBettingRound.pot();
        assertThat(potSize).isEqualTo(ChipValue.of(30));
    }

    @Test
    void thirdAndSecondPlayerCalled_AllPlayersStillPlaying() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).call()
                .with(thirdPlayer).call();

        final Collection<Player> remainingPlayers = updatedBettingRound.remainingPlayers();
        assertThat(remainingPlayers).containsExactlyInAnyOrderElementsOf(allPlayers);
    }

    @Test
    void betFoldRaise50Call_PotSizeOf100Chips() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).fold()
                .with(thirdPlayer).raiseTo(50)
                .with(firstPlayer).call();

        final ChipValue potSize = updatedBettingRound.pot();
        assertThat(potSize).isEqualTo(ChipValue.of(100));
    }

    @Test
    void betFoldRaise50Call_SecondPlayerIsNotPlayingAnymore() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).fold()
                .with(thirdPlayer).raiseTo(50)
                .with(firstPlayer).call();

        final Collection<Player> remainingPlayers = updatedBettingRound.remainingPlayers();
        assertThat(remainingPlayers)
                .doesNotContain(secondPlayer)
                .containsExactlyInAnyOrder(firstPlayer, thirdPlayer);
    }

    @Test
    void betFoldRaiseCall_RoundFinished_NoMoreActionRequired() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).fold()
                .with(thirdPlayer).raiseTo(50)
                .with(firstPlayer).call();

        final boolean finished = updatedBettingRound.isFinished();
        assertThat(finished).isTrue();
        final Optional<Player> next = updatedBettingRound.turn();
        assertThat(next).isEmpty();
    }

    @Test
    void betFoldRaiseCall_LastPlayIsCallOf40MoreChips() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).fold()
                .with(thirdPlayer).raiseTo(50)
                .with(firstPlayer).call();

        final Optional<Play> optional = updatedBettingRound.lastPlay();
        assertThat(optional).isPresent();

        final Play play = optional.get();
        final ChipValue chipValue = play.chipValue();
        assertThat(chipValue).isEqualTo(ChipValue.of(40));
        final Player player = play.player();
        assertThat(player).isEqualTo(firstPlayer);
        final Play.Type type = play.type();
        assertThat(type).isEqualTo(Play.Type.CALL);
    }

    @Test
    void allThreePlayersCheck_LastPlayIsCheckWithZeroChips() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).check()
                .with(secondPlayer).check()
                .with(thirdPlayer).check();

        final Optional<Play> optional = updatedBettingRound.lastPlay();
        assertThat(optional).isPresent();

        final Play play = optional.get();
        final ChipValue chipValue = play.chipValue();
        assertThat(chipValue).isEqualTo(ChipValue.zero());
        final Player player = play.player();
        assertThat(player).isEqualTo(thirdPlayer);
        final Play.Type type = play.type();
        assertThat(type).isEqualTo(Play.Type.CHECK);
    }

    @Test
    void allThreePlayersCheck_RoundFinished_NoMoreActionRequired() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).check()
                .with(secondPlayer).check()
                .with(thirdPlayer).check();

        final boolean finished = updatedBettingRound.isFinished();
        assertThat(finished).isTrue();
        final Optional<Player> next = updatedBettingRound.turn();
        assertThat(next).isEmpty();
    }

    @Test
    void allThreePlayersCheck_PotSizeOf0Chips() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).check()
                .with(secondPlayer).check()
                .with(thirdPlayer).check();

        final ChipValue potSize = updatedBettingRound.pot();
        assertThat(potSize).isEqualTo(ChipValue.zero());
    }

    @Test
    void allThreePlayersCheck_AllPlayersStillPlaying() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).check()
                .with(secondPlayer).check()
                .with(thirdPlayer).check();

        final Collection<Player> remainingPlayers = updatedBettingRound.remainingPlayers();
        assertThat(remainingPlayers).containsExactlyInAnyOrderElementsOf(allPlayers);
    }

    @Test
    void checkFoldFold_LastPlayIsCheckWithZeroChips() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).check()
                .with(secondPlayer).fold()
                .with(thirdPlayer).fold();

        final Optional<Play> optional = updatedBettingRound.lastPlay();
        assertThat(optional).isPresent();

        final Play play = optional.get();
        final ChipValue chipValue = play.chipValue();
        assertThat(chipValue).isEqualTo(ChipValue.zero());
        final Player player = play.player();
        assertThat(player).isEqualTo(thirdPlayer);
        final Play.Type type = play.type();
        assertThat(type).isEqualTo(Play.Type.FOLD);
    }

    @Test
    void checkFoldFold_RoundFinished_NoMoreActionRequired() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).check()
                .with(secondPlayer).fold()
                .with(thirdPlayer).fold();

        final boolean finished = updatedBettingRound.isFinished();
        assertThat(finished).isTrue();
        final Optional<Player> next = updatedBettingRound.turn();
        assertThat(next).isEmpty();
    }

    @Test
    void checkFoldFold_PotSizeOf0Chips() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).check()
                .with(secondPlayer).fold()
                .with(thirdPlayer).fold();

        final ChipValue potSize = updatedBettingRound.pot();
        assertThat(potSize).isEqualTo(ChipValue.zero());
    }

    @Test
    void checkFoldFold_OnlyFirstPlayerStillPlaying() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).check()
                .with(secondPlayer).fold()
                .with(thirdPlayer).fold();

        final Collection<Player> remainingPlayers = updatedBettingRound.remainingPlayers();
        assertThat(remainingPlayers)
                .doesNotContain(secondPlayer, thirdPlayer)
                .containsExactlyInAnyOrder(firstPlayer);
    }

    @Test
    void betFoldRaise3Bet_SecondPlayerFoldedAndIsThusSkipped() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).fold()
                .with(thirdPlayer).raiseTo(50)
                .with(firstPlayer).raiseTo(120);

        final Optional<Player> optional = updatedBettingRound.turn();
        assertThat(optional).isPresent();
        final Player player = optional.get();
        assertThat(player).isEqualTo(thirdPlayer);
    }

    @Test
    void betFoldRaise3BetCall_LastPlayIsCallForMissing70Chips() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).fold()
                .with(thirdPlayer).raiseTo(50)
                .with(firstPlayer).raiseTo(120)
                .with(thirdPlayer).call();

        final Optional<Play> optional = updatedBettingRound.lastPlay();
        assertThat(optional).isPresent();

        final Play play = optional.get();
        final ChipValue chipValue = play.chipValue();
        assertThat(chipValue).isEqualTo(ChipValue.of(70));
        final Player player = play.player();
        assertThat(player).isEqualTo(thirdPlayer);
        final Play.Type type = play.type();
        assertThat(type).isEqualTo(Play.Type.CALL);
    }

    @Test
    void betFoldRaise3BetCall_RoundFinished_NoMoreActionRequired() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).fold()
                .with(thirdPlayer).raiseTo(50)
                .with(firstPlayer).raiseTo(120)
                .with(thirdPlayer).call();

        final boolean finished = updatedBettingRound.isFinished();
        assertThat(finished).isTrue();
        final Optional<Player> next = updatedBettingRound.turn();
        assertThat(next).isEmpty();
    }

    @Test
    void betFoldRaise3BetCall_PotSizeOf0Chips() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).fold()
                .with(thirdPlayer).raiseTo(50)
                .with(firstPlayer).raiseTo(120)
                .with(thirdPlayer).call();

        final ChipValue potSize = updatedBettingRound.pot();
        assertThat(potSize).isEqualTo(ChipValue.of(240));
    }

    @Test
    void betFoldRaise3BetCall_SecondPlayerIsNotPlayingAnymore() {
        final BettingRound updatedBettingRound = round
                .with(firstPlayer).bet(10)
                .with(secondPlayer).fold()
                .with(thirdPlayer).raiseTo(50)
                .with(firstPlayer).raiseTo(120)
                .with(thirdPlayer).call();

        final Collection<Player> remainingPlayers = updatedBettingRound.remainingPlayers();
        assertThat(remainingPlayers)
                .containsExactlyInAnyOrder(firstPlayer, thirdPlayer)
                .doesNotContain(secondPlayer);
    }
}
