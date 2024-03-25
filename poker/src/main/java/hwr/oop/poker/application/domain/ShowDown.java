package hwr.oop.poker.application.domain;

import hwr.oop.poker.application.domain.cards.CommunityCardsProvider;
import hwr.oop.poker.application.domain.combinations.Combination;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShowDown {

    private final CommunityCardsProvider communityCardsProvider;
    private final HoleCards holeCards;
    private final Map<Player, Combination> combinationMap;
    private final Player winner;

    public static ShowDown create(CommunityCardsProvider communityCardsProvider, HoleCards holeCards, List<Player> players) {
        return new ShowDown(communityCardsProvider, holeCards, players);
    }

    private ShowDown(CommunityCardsProvider communityCardsProvider, HoleCards holeCards, List<Player> players) {
        this.communityCardsProvider = communityCardsProvider;
        this.holeCards = holeCards;
        this.combinationMap = createCombinationMap(players);
        this.winner = queryWinner();
    }

    public Combination combination(Player player) {
        final boolean mapContainsPlayer = combinationMap.containsKey(player);
        if (mapContainsPlayer) {
            return combinationMap.get(player);
        } else {
            throw new InvalidPlayerException(
                    "Queried player does not take part in show down," +
                            " expected: " + combinationMap.keySet() + ", got: " + player
            );
        }
    }

    public Player winner() {
        return winner;
    }

    private Map<Player, Combination> createCombinationMap(List<Player> players) {
        return players.stream().collect(Collectors.toMap(p -> p, this::createCombinationFor));
    }

    private Combination createCombinationFor(Player player) {
        final var allCards = combineBothCardSources(
                holeCards.of(player),
                communityCardsProvider.cardsDealt()
        );
        return Combination.of(allCards);
    }

    private List<Card> combineBothCardSources(List<Card> first, Collection<Card> second) {
        return Stream.concat(first.stream(), second.stream())
                .toList();
    }

    private Player queryWinner() {
        return combinationMap.entrySet().stream()
                .reduce(this::reduceByValue)
                .orElseThrow()
                .getKey();
    }

    private Map.Entry<Player, Combination> reduceByValue(Map.Entry<Player, Combination> first,
                                                         Map.Entry<Player, Combination> second) {
        return first.getValue().over(second.getValue()) ? first : second;
    }

    public static class InvalidPlayerException extends RuntimeException {
        public InvalidPlayerException(String message) {
            super(message);
        }
    }
}
