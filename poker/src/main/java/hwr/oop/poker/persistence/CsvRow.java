package hwr.oop.poker.persistence;

import hwr.oop.poker.application.domain.Converter;
import hwr.oop.poker.application.domain.Deck;
import hwr.oop.poker.application.domain.Hand;
import hwr.oop.poker.application.domain.HandId;
import hwr.oop.poker.application.domain.Player;
import hwr.oop.poker.application.domain.Stacks;
import hwr.oop.poker.application.domain.betting.BettingRound;
import hwr.oop.poker.application.domain.betting.Play.Type;
import hwr.oop.poker.application.domain.blinds.BlindConfiguration;
import hwr.oop.poker.application.domain.blinds.SmallBlind;
import hwr.oop.poker.application.domain.decks.UnshuffledDeck;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public record CsvRow(String idString, String playersAndStacks, String smallBlindString,
                     String deckString, String playsString) {

  public static CsvRow fromHand(Hand hand, HandId handId) {
    final var memento = hand.restoreMemento();
    final var players = memento.players();
    final var stacks = memento.stacks();

    // players and stacks
    final var stackString = players.stream().map(p -> p.id() + "-" + stacks.ofPlayer(p).value())
        .reduce((a, b) -> a + "," + b).orElseThrow();

    // blinds
    final var smallBlind = hand.blindConfiguration().smallBlind().value();
    final var smallBlindString = Long.toString(smallBlind);

    // cards
    final var cards = memento.deck().drawAllCards();
    final var deckString = cards.stream()
        .map(c -> c.symbol().stringRepresentation() + c.color().stringRepresentation())
        .reduce((a, b) -> a + "," + b).orElseThrow();

    // plays
    final String handString = hand.plays()
        .filter(p -> p.type() != Type.SMALL_BLIND && p.type() != Type.BIG_BLIND).map(p -> {
          final Player player = p.player();
          final Type playType = p.type();
          final String suffix = switch (playType) {
            case BET -> "B-" + p.totalChipValue().value();
            case RAISE -> "R-" + p.totalChipValue().value();
            case FOLD -> "F";
            case CHECK -> "CH";
            case CALL -> "CA";
            default -> "";
          };
          return player.id() + "-" + suffix;
        }).reduce((a, b) -> a + "," + b).orElseThrow();

    return new CsvRow(handId.value(), stackString, smallBlindString, deckString, handString);
  }

  public static CsvRow fromHand(Hand hand) {
    return fromHand(hand, new HandId(UUID.randomUUID().toString()));
  }

  public static CsvRow fromString(String row) {
    final var parts = Arrays.stream(row.split(";")).toList();
    assert parts.size() == 5;
    return new CsvRow(parts.get(0), parts.get(1), parts.get(2), parts.get(3), parts.get(4));
  }

  @Override
  public String toString() {
    return idString + ";" + playersAndStacks + ";" + smallBlindString + ";" + deckString + ";"
        + playsString;
  }

  public Hand toHand() {
    // parse players and stacks
    final var builder = Stacks.newBuilder();
    final List<Player> mutableList = new ArrayList<>();
    Arrays.stream(playersAndStacks.split(",")).map(s -> Arrays.stream(s.split("-")).toList())
        .forEach(s -> {
          final var first = s.get(0);
          final var second = s.get(1);
          final var player = new Player(first);
          mutableList.add(player);
          builder.of(player).is(Long.parseLong(second));
        });
    final var stacks = builder.build();
    final var players = Collections.unmodifiableList(mutableList);

    // parse deckString
    final Deck deck = parseDeck();

    // parse blind configuration
    final BlindConfiguration blindConfiguration = parseBlinds();

    // parse plays
    final List<UnaryOperator<BettingRound>> plays = Arrays.stream(playsString.split(","))
        .map(s -> Arrays.stream(s.split("-")).toList()).map(this::parsePlayFunction).toList();

    return createHand(stacks, players, deck, blindConfiguration, plays);
  }

  private BlindConfiguration parseBlinds() {
    final var smallBlindLong = Long.parseLong(smallBlindString);
    final var smallBlind = SmallBlind.of(smallBlindLong);
    return BlindConfiguration.create(smallBlind);
  }

  private Deck parseDeck() {
    final var converter = Converter.create();
    final var cardsInDeck = converter.convert(deckString);
    return new UnshuffledDeck(cardsInDeck);
  }

  private Hand createHand(Stacks stacks, List<Player> players, Deck deck,
      BlindConfiguration blindConfiguration, List<UnaryOperator<BettingRound>> plays) {
    var hand = Hand.newBuilder().deck(deck).players(players).stacks(stacks)
        .blindConfiguration(blindConfiguration).build();
    for (UnaryOperator<BettingRound> play : plays) {
      hand = hand.onCurrentRound(play);
    }
    return hand;
  }

  private UnaryOperator<BettingRound> parsePlayFunction(List<String> s) {
    final var playerId = s.get(0);
    final var player = new Player(playerId);
    final var playId = s.get(1);
    final Supplier<String> thirdSupplier = () -> s.get(2);
    return parseIndividualPlay(thirdSupplier, player, playId);
  }

  private UnaryOperator<BettingRound> parseIndividualPlay(Supplier<String> third, Player player,
      String playId) {
    final Type play = PlayTypeMapping.byCsv(playId);
    return switch (play) {
      case BET -> {
        final var betToString = third.get();
        final var betTo = Long.parseLong(betToString);
        yield r -> r.with(player).bet(betTo);
      }
      case FOLD -> r -> r.with(player).fold();
      case CALL -> r -> r.with(player).call();
      case RAISE -> {
        final var raiseToString = third.get();
        final var raiseTo = Long.parseLong(raiseToString);
        yield r -> r.with(player).raiseTo(raiseTo);
      }
      case CHECK -> r -> r.with(player).check();
      default -> throw buildException(player, playId);
    };
  }

  private RuntimeException buildException(Player player, String playId) {
    return new IllegalArgumentException("Could not parse: " + playId + ", for player: " + player);
  }
}
