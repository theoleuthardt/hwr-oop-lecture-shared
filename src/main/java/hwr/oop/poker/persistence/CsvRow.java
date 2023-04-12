package hwr.oop.poker.persistence;

import hwr.oop.poker.application.domain.*;
import hwr.oop.poker.application.domain.betting.BettingRound;
import hwr.oop.poker.application.domain.blinds.BlindConfiguration;
import hwr.oop.poker.application.domain.blinds.SmallBlind;
import hwr.oop.poker.application.domain.decks.TestDoubleDeck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public record CsvRow(
        String idString,
        String playersAndStacks,
        String smallBlindString,
        String deckString,
        String playsString
) {

    @Override
    public String toString() {
        return idString + ";" +
                playersAndStacks + ";" +
                smallBlindString + ";" +
                deckString + ";" +
                playsString;
    }

    public Hand toHand() {
        // parse players and stacks
        final var builder = Stacks.newBuilder();
        final List<Player> mutableList = new ArrayList<>();
        Arrays.stream(playersAndStacks.split(","))
                .map(s -> Arrays.stream(s.split("-")).toList())
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
                .map(s -> Arrays.stream(s.split("-")).toList())
                .map(this::parsePlayFunction)
                .toList();

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
        return new TestDoubleDeck(cardsInDeck);
    }

    private Hand createHand(Stacks stacks,
                            List<Player> players,
                            Deck deck,
                            BlindConfiguration blindConfiguration,
                            List<UnaryOperator<BettingRound>> plays) {
        Hand hand = Hand.newBuilder()
                .deck(deck)
                .players(players)
                .stacks(stacks)
                .blindConfiguration(blindConfiguration)
                .build();
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

    private UnaryOperator<BettingRound> parseIndividualPlay(Supplier<String> third, Player player, String playId) {
        return switch (playId) {
            case "B" -> {
                final var betToString = third.get();
                final var betTo = Long.parseLong(betToString);
                yield r -> r.with(player).bet(betTo);
            }
            case "F" -> r -> r.with(player).fold();
            case "CA" -> r -> r.with(player).call();
            case "R" -> {
                final var raiseToString = third.get();
                final var raiseTo = Long.parseLong(raiseToString);
                yield r -> r.with(player).raiseTo(raiseTo);
            }
            case "CH" -> r -> r.with(player).check();
            default -> throw buildException(player, playId);
        };
    }

    private RuntimeException buildException(Player player, String playId) {
        return new IllegalArgumentException("Could not parse: " + playId + ", for player: " + player);
    }
}
