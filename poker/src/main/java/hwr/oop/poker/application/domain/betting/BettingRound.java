package hwr.oop.poker.application.domain.betting;

import hwr.oop.poker.application.domain.ChipValue;
import hwr.oop.poker.application.domain.Player;
import hwr.oop.poker.application.domain.Stacks;
import hwr.oop.poker.application.domain.blinds.BlindConfiguration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class BettingRound {

  private final List<Player> players;
  private final Stacks stacks;
  private final List<Play> plays;
  private final Player turn;

  public static BettingRound create(Stacks stacks, Player... players) {
    return create(stacks, Arrays.asList(players));
  }

  public static BettingRound create(Stacks stacks, List<Player> players) {
    return new BettingRound(stacks, players);
  }

  public BettingRound(Stacks stacks, List<Player> players) {
    this.players = players;
    this.stacks = stacks;
    this.plays = new ArrayList<>();
    this.turn = players.getFirst();
  }

  private BettingRound(Stacks stacks, List<Player> players, Stream<Play> plays, Player turn) {
    this.stacks = stacks;
    this.players = players;
    this.turn = turn;
    this.plays = plays.toList();
  }

  public static BettingRound createPreFlop(Stacks stacks, BlindConfiguration blindConfig,
      List<Player> players) {
    return new BettingRound(stacks, players).apply(blindConfig);
  }

  private BettingRound apply(BlindConfiguration blindConfig) {
    final var sbApplied = nextState(Play.smallBlind(this.turn, blindConfig.smallBlind()));
    return sbApplied.nextState(Play.bigBlind(sbApplied.turn, blindConfig.bigBlind()));
  }

  public RoundInContext with(Player player) {
    return new RoundInContext(player, this);
  }

  public boolean isFinished() {
    final Optional<Play> optional = lastTargetValueIncreasingPlay();
    if (optional.isPresent()) {
      final var increasingPlay = optional.get();
      if (increasingPlay.isBigBlind()) {
        boolean stuff;
        final Collection<Player> remainingPlayers = remainingPlayers();
        final Player bigBlindPlayer = increasingPlay.player();
        if (remainingPlayers.size() == 1) {
          stuff = true;
        } else {
          final Play lastPlay = lastPlay().orElseThrow();
          stuff = lastPlay.player().equals(bigBlindPlayer) && lastPlay.isCheck();
        }
        return stuff;
      }
    }

    if (allPlayersHavePlayed() && allPlaysAreChecks()) {
      return true;
    }
    if (isOnlyOnePlayerRemaining()) {
      return true;
    }
    final List<ChipValue> distinctValues = players.stream()
        .filter(player -> playersThatHaveFolded().noneMatch(p -> p.equals(player)))
        .map(this::chipsPutIntoPotBy)
        .distinct()
        .toList();
    final int numberOfDistinctValues = distinctValues.size();
    final boolean containsZero = distinctValues.contains(ChipValue.zero());
    return numberOfDistinctValues == 1 && !containsZero;
  }

  public Optional<Player> turn() {
    if (isFinished()) {
      return Optional.empty();
    } else {
      return Optional.of(turn);
    }
  }

  public Optional<Play> lastPlay() {
    if (plays.isEmpty()) {
      return Optional.empty();
    } else {
      final Play lastPlay = plays.getLast();
      return Optional.of(lastPlay);
    }
  }

  public Optional<Play> lastPotSizeIncreasingPlay() {
    return plays.stream()
        .filter(Play::increasedChipsInPot)
        .reduce((a, b) -> b);
  }

  public Optional<Play> lastTargetValueIncreasingPlay() {
    return plays.stream()
        .filter(Play::hasIncreasedTargetValue)
        .reduce((a, b) -> b);
  }

  public ChipValue pot() {
    final var sumOfAllPlays = plays.stream()
        .map(Play::chipValue)
        .map(ChipValue::value)
        .reduce(Long::sum)
        .orElse(0L);
    return ChipValue.of(sumOfAllPlays);
  }

  public ChipValue chipsPutIntoPotBy(Player player) {
    return plays.stream()
        .filter(play -> play.playedBy(player))
        .map(Play::chipValue)
        .reduce(ChipValue::plus)
        .orElse(ChipValue.zero());
  }

  public BettingRound nextState(Play play) {
    assertCorrectPlayer(play);
    final var updatedStacks = stacks.apply(play);
    final var playsIncludingNewOne = Stream.concat(plays.stream(), Stream.of(play));
    return new BettingRound(
        updatedStacks, players,
        playsIncludingNewOne,
        next(turn)
    );
  }

  public ChipValue remainingChips(Player player) {
    return stacks.ofPlayer(player);
  }

  public Stacks stacks() {
    return stacks;
  }

  public List<Player> remainingPlayers() {
    return players.stream()
        .filter(this::hasPlayerNotFolded)
        .toList();
  }

  private boolean isOnlyOnePlayerRemaining() {
    final long numberOfPlayersThatHaveNotFolded = players.stream()
        .filter(player -> playersThatHaveFolded().noneMatch(p -> p.equals(player)))
        .count();
    return numberOfPlayersThatHaveNotFolded < 2;
  }

  private Stream<Player> playersThatHaveFolded() {
    return plays.stream()
        .filter(p -> p.type() == Play.Type.FOLD)
        .map(Play::player);
  }

  private boolean allPlaysAreChecks() {
    return plays.stream().allMatch(Play::isCheck);
  }

  private boolean allPlayersHavePlayed() {
    return players.stream().allMatch(
        player -> plays.stream()
            .anyMatch(play -> play.playedBy(player))
    );
  }


  private void assertCorrectPlayer(Play play) {
    final boolean correctPlayer = play.playedBy(turn);
    if (!correctPlayer) {
      throw new InvalidPlayOnStateException(
          "Cannot play " + play +
              ", wrong player: " + play.player() +
              ", next player is: " + turn);
    }
  }

  private Player next(Player current) {
    final Player candidate = nextCandidatePlayer(current);
    final boolean candidateFolded = playerHasFolded(candidate);
    if (candidateFolded) {
      return next(candidate);
    } else {
      return candidate;
    }
  }

  private Player nextCandidatePlayer(Player current) {
    final int indexOfCandidatePlayer =
        (players.indexOf(current) + 1) % players.size();
    return players.get(indexOfCandidatePlayer);
  }

  private boolean playerHasFolded(Player candidate) {
    return plays.stream()
        .filter(Play::isFold)
        .anyMatch(play -> play.playedBy(candidate));
  }

  private boolean hasPlayerNotFolded(Player player) {
    return plays.stream().noneMatch(play -> play.playedBy(player) && play.isFold());
  }

  public Stream<Play> plays() {
    return plays.stream();
  }

  public static class InvalidPlayOnStateException extends RuntimeException {

    public InvalidPlayOnStateException(String message) {
      super(message);
    }
  }
}
