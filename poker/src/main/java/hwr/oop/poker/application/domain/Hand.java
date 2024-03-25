package hwr.oop.poker.application.domain;

import hwr.oop.poker.application.domain.betting.BettingRound;
import hwr.oop.poker.application.domain.betting.Play;
import hwr.oop.poker.application.domain.betting.positions.RoundPosition;
import hwr.oop.poker.application.domain.blinds.BlindConfiguration;
import hwr.oop.poker.application.domain.cards.CommunityCards;
import hwr.oop.poker.application.domain.cards.CommunityCardsProvider;
import hwr.oop.poker.application.domain.cards.Flop;
import hwr.oop.poker.application.domain.cards.River;
import hwr.oop.poker.application.domain.cards.Turn;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class Hand implements CommunityCardsProvider {

  private final Deck deck;
  private final List<Player> players;
  private final BlindConfiguration blindConfiguration;
  private final HoleCards holeCards;
  private final Map<RoundPosition, BettingRound> rounds;
  private final CommunityCardsProvider communityCards;
  private final HandMemento memento;

  public static Builder newBuilder() {
    return new Builder();
  }

  private static Hand createInitially(Deck deck, List<Player> players,
      BlindConfiguration blindConfiguration, Stacks stacks) {
    return new Hand(deck, players, blindConfiguration, stacks);
  }

  private static Hand createBasedOnOlderHand(Deck deck,
      List<Player> players,
      BlindConfiguration blindConfiguration,
      HoleCards holeCards,
      Map<RoundPosition, BettingRound> rounds,
      CommunityCardsProvider communityCards,
      HandMemento memento) {
    return new Hand(deck, players, blindConfiguration, holeCards, rounds, communityCards, memento);
  }

  private Hand(Deck deck, List<Player> players, BlindConfiguration blindConfiguration,
      Stacks stacks) {
    this.memento = new HandMemento(deck.copy(), players, blindConfiguration, stacks);
    this.deck = deck;
    this.players = players;
    this.blindConfiguration = blindConfiguration;
    this.holeCards = HoleCards.createByDrawingFromDeck(deck, players);
    this.communityCards = CommunityCards.empty();
    final var preFlopRound = BettingRound.createPreFlop(stacks, blindConfiguration, players);
    this.rounds = Map.of(RoundPosition.PRE_FLOP, preFlopRound);
  }

  private Hand(
      Deck deck,
      List<Player> players,
      BlindConfiguration blindConfiguration,
      HoleCards holeCards,
      Map<RoundPosition, BettingRound> rounds,
      CommunityCardsProvider communityCards,
      HandMemento memento
  ) {
    this.deck = deck;
    this.players = players;
    this.blindConfiguration = blindConfiguration;
    this.holeCards = holeCards;
    this.rounds = createMapBasedOn(rounds);
    this.communityCards = buildCommunityCards(deck, communityCards);
    this.memento = memento;
  }

  private CommunityCardsProvider buildCommunityCards(Deck deck,
      CommunityCardsProvider oldCommunityCards) {
    final var position = currentPosition();
    final var currentRound = rounds.get(position);
    if (!currentRound.isFinished()) {
      position.ifRequiresBurn(deck::burn);
      return position.buildCardsFor(deck, oldCommunityCards);
    } else {
      return oldCommunityCards;
    }
  }

  public List<Card> holeCards(Player player) {
    return holeCards.of(player);
  }

  public Player smallBlind() {
    return players.getFirst();
  }

  public Player bigBlind() {
    return players.get(1);
  }

  public Player button() {
    return players.get(1);
  }

  public Player underTheGun() {
    return players.getFirst();
  }

  public BlindConfiguration blindConfiguration() {
    return blindConfiguration;
  }

  public ChipValue potSize() {
    return () -> blindConfiguration.bigBlind().value() + blindConfiguration.smallBlind().value();
  }

  @Override
  public Optional<Flop> flop() {
    return communityCards.flop();
  }

  @Override
  public Optional<Turn> turn() {
    return communityCards.turn();
  }

  @Override
  public Optional<River> river() {
    return communityCards.river();
  }

  @Override
  public Collection<Card> cardsDealt() {
    return communityCards.cardsDealt();
  }

  public boolean preFlopRoundPlayed() {
    return isRoundPlayed(RoundPosition.PRE_FLOP);
  }

  public boolean flopRoundPlayed() {
    return isRoundPlayed(RoundPosition.FLOP);
  }

  public boolean turnRoundPlayed() {
    return isRoundPlayed(RoundPosition.TURN);
  }

  public boolean riverRoundPlayed() {
    return isRoundPlayed(RoundPosition.RIVER);
  }

  public Hand onCurrentRound(UnaryOperator<BettingRound> function) {
    final var position = currentPosition();
    final var currentRound = rounds.get(position);
    if (!currentRound.isFinished()) {
      final var round = roundOn(position).orElseThrow();
      return accept(function.apply(round));
    } else {
      throw new PlayOnOnFinishedHandException();
    }
  }

  public boolean isFinished() {
    final var position = currentPosition();
    final var currentRound = rounds.get(position);
    return currentRound.isFinished();
  }

  public Stacks stacks() {
    final var position = currentPosition();
    final var round = rounds.get(position);
    return round.stacks();
  }

  public RoundPosition currentPosition() {
    return currentPosition(rounds);
  }

  private Hand accept(BettingRound round) {
    if (!isFinished()) {
      final var position = currentPosition();
      final var copy = copy();
      final var unmodifiableMap = createNewMapWith(round, position);
      copy.rounds(unmodifiableMap);
      return copy.build();
    } else {
      throw new PlayOnOnFinishedHandException();
    }
  }

  private Map<RoundPosition, BettingRound> createNewMapWith(BettingRound round,
      RoundPosition currentPosition) {
    final Map<RoundPosition, BettingRound> mutableMap = new HashMap<>(rounds);
    mutableMap.put(currentPosition, round);
    return Collections.unmodifiableMap(mutableMap);
  }

  private Builder copy() {
    return newBuilder()
        .deck(deck)
        .players(players)
        .blindConfiguration(blindConfiguration)
        .holeCards(holeCards)
        .rounds(rounds)
        .memento(memento)
        .communityCards(communityCards);
  }

  private boolean isRoundPlayed(RoundPosition roundPosition) {
    final Optional<BettingRound> optional = roundOn(roundPosition);
    return optional.isPresent() && optional.orElseThrow().isFinished();
  }

  private Optional<BettingRound> roundOn(RoundPosition position) {
    final boolean containsKey = rounds.containsKey(position);
    if (!containsKey) {
      return Optional.empty();
    } else {
      final BettingRound round = rounds.get(position);
      return Optional.of(round);
    }
  }

  private Map<RoundPosition, BettingRound> createMapBasedOn(
      Map<RoundPosition, BettingRound> rounds) {
    Map<RoundPosition, BettingRound> modifiableMap = new HashMap<>(rounds);
    final boolean riverPresentAndFinished =
        rounds.containsKey(RoundPosition.RIVER) && rounds.get(RoundPosition.RIVER).isFinished();
    if (!riverPresentAndFinished) {
      final var roundPosition = currentPosition(rounds);
      final var playersForNewRound = playersForNewRound(rounds, roundPosition);
      final var stacksFromPreviousRound = stacksForNewRound(rounds, roundPosition);
      final var freshBettingRound = BettingRound.create(stacksFromPreviousRound,
          playersForNewRound);
      modifiableMap.putIfAbsent(roundPosition, freshBettingRound);
    }
    return Collections.unmodifiableMap(modifiableMap);
  }

  private Stacks stacksForNewRound(Map<RoundPosition, BettingRound> rounds,
      RoundPosition roundPosition) {
    final Optional<RoundPosition> optional = roundPosition.previous();
    if (optional.isPresent()) {
      final var previous = optional.orElseThrow();
      final var round = rounds.get(previous);
      return round.stacks();
    } else {
      return rounds.get(roundPosition).stacks();
    }
  }

  private List<Player> playersForNewRound(Map<RoundPosition, BettingRound> rounds,
      RoundPosition roundPosition) {
    final var optional = roundPosition.previous();
    if (optional.isPresent()) {
      final var previous = optional.orElseThrow();
      final var round = rounds.get(previous);
      return round.remainingPlayers();
    } else {
      return rounds.get(roundPosition).remainingPlayers();
    }
  }

  private RoundPosition currentPosition(Map<RoundPosition, BettingRound> someRounds) {
    final var candidatePosition = latestRound(someRounds);
    final BettingRound candidateRound = someRounds.get(candidatePosition);
    if (candidateRound.isFinished()) {
      final var nextPosition = candidatePosition.nextPosition();
      return nextPosition.orElse(candidatePosition);  // stay on finished round (in case of river)
    } else {
      return candidatePosition;
    }
  }

  private RoundPosition latestRound(Map<RoundPosition, BettingRound> someRounds) {
    return someRounds.keySet().stream()
        .reduce(RoundPosition::latest)
        .orElseThrow();
  }

  public Optional<ShowDown> showDown() {
    if (isFinished()) {
      final ShowDown showDown = ShowDown.create(communityCards, holeCards, players);
      return Optional.of(showDown);
    } else {
      return Optional.empty();
    }
  }

  public List<Player> remainingPlayers() {
    final var position = currentPosition();
    assert rounds.containsKey(position);
    final BettingRound currentRound = rounds.get(position);
    return currentRound.remainingPlayers();
  }

  public HandMemento restoreMemento() {
    return memento;
  }

  public Stream<Play> plays() {
    return RoundPosition.all()
        .flatMap(p -> rounds.containsKey(p) ? rounds.get(p).plays() : Stream.empty());
  }

  public static class Builder {

    private Deck deck;
    private List<Player> players;
    private BlindConfiguration blindConfiguration;
    private HoleCards holeCards;
    private Map<RoundPosition, BettingRound> rounds;
    private CommunityCardsProvider communityCards;
    private Stacks stacks;
    private HandMemento memento;

    private Builder() {
      this.deck = null;
      this.players = null;
      this.blindConfiguration = null;
      this.holeCards = null;
      this.communityCards = null;
      this.rounds = null;
      this.stacks = null;
      this.memento = null;
    }

    public Hand build() {
      final var hasIncompleteInfo = Stream.of(holeCards, rounds, communityCards, memento)
          .anyMatch(Objects::isNull);
      if (hasIncompleteInfo) {
        return Hand.createInitially(deck, players, blindConfiguration, stacks);
      } else {
        return Hand.createBasedOnOlderHand(
            deck, players, blindConfiguration, holeCards, rounds,
            communityCards, memento
        );
      }
    }

    public Builder deck(Deck deck) {
      this.deck = deck;
      return this;
    }

    public Builder players(List<Player> players) {
      this.players = players;
      return this;
    }

    public Builder blindConfiguration(BlindConfiguration smallBlind) {
      this.blindConfiguration = smallBlind;
      return this;
    }

    private Builder holeCards(HoleCards holeCards) {
      this.holeCards = holeCards;
      return this;
    }

    private Builder rounds(Map<RoundPosition, BettingRound> rounds) {
      this.rounds = rounds;
      return this;
    }

    private Builder communityCards(CommunityCardsProvider communityCards) {
      this.communityCards = communityCards;
      return this;
    }

    public Builder stacks(Stacks stacks) {
      this.stacks = stacks;
      return this;
    }

    public Builder memento(HandMemento memento) {
      this.memento = memento;
      return this;
    }
  }

  public static class PlayOnOnFinishedHandException extends RuntimeException {
    // nothing to do
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Hand hand = (Hand) o;
    return Objects.equals(players, hand.players) &&
        Objects.equals(memento, hand.memento) &&
        Objects.equals(plays().toList(), hand.plays().toList());
  }

  @Override
  public int hashCode() {
    return Objects.hash(players, memento, plays().toList());
  }

  @Override
  public String toString() {
    return "Hand{" +
        "players=" + players +
        ", memento=" + memento +
        ", plays=" + plays().map(Play::toString).toList() +
        '}';
  }
}
