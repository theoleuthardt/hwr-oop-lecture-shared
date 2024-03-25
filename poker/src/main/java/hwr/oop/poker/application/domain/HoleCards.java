package hwr.oop.poker.application.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class HoleCards {

  private final Map<Player, List<Card>> assignment;

  public static HoleCards createByDrawingFromDeck(Deck deck, List<Player> players) {
    return new HoleCards(deck, players);
  }

  private HoleCards(Deck deck, List<Player> players) {
    this.assignment = drawHoleCardsFromDeck(deck::draw, players);
  }

  public List<Card> of(Player player) {
    return assignment.get(player);
  }

  private Map<Player, List<Card>> drawHoleCardsFromDeck(Supplier<Card> cardSupplier,
      List<Player> players) {
    final var mutableMap = drawCardsToMutableMap(cardSupplier, players);
    return convertToImmutableMap(mutableMap);
  }

  private Map<Player, List<Card>> drawCardsToMutableMap(Supplier<Card> cardSupplier,
      List<Player> players) {
    final Map<Player, List<Card>> mutableMap = new HashMap<>();
    for (int i = 0; i < 2; i++) {
      for (Player player : players) {
        final var cards = mutableMap
            .computeIfAbsent(player, p -> new ArrayList<>());
        final Card drawnCard = cardSupplier.get();
        cards.add(drawnCard);
      }
    }
    return mutableMap;
  }

  private Map<Player, List<Card>> convertToImmutableMap(Map<Player, List<Card>> mutableMap) {
    final var mutableMapOfImmutableLists = convertToImmutableLists(mutableMap);
    return Collections.unmodifiableMap(mutableMapOfImmutableLists);
  }

  private Map<Player, List<Card>> convertToImmutableLists(Map<Player, List<Card>> mutableMap) {
    for (var entry : mutableMap.entrySet()) {
      final var player = entry.getKey();
      final var cards = entry.getValue();
      final var immutableLIst = List.copyOf(cards);
      mutableMap.put(player, immutableLIst);
    }
    return mutableMap;
  }

}
