package hwr.oop.poker.application.domain;

import hwr.oop.poker.application.domain.blinds.BlindConfiguration;
import java.util.Collections;
import java.util.List;

public class HandMemento {

  private final Deck deck;
  private final List<Player> players;
  private final BlindConfiguration blinds;
  private final Stacks stacks;

  public HandMemento(
      Deck deck, List<Player> players, BlindConfiguration blindConfiguration, Stacks stacks
  ) {
    this.deck = deck;
    this.players = players;
    this.blinds = blindConfiguration;
    this.stacks = stacks;
  }

  public Hand ascend() {
    return Hand.newBuilder()
        .blindConfiguration(blinds)
        .players(players)
        .stacks(stacks)
        .deck(deck)
        .build();
  }

  public Deck deck() {
    return deck.copy();
  }

  public Stacks stacks() {
    return stacks;
  }

  public List<Player> players() {
    return Collections.unmodifiableList(players);
  }

  @Override
  public String toString() {
    return "HandMemento{" +
        "players=" + players +
        ", blinds=" + blinds +
        ", stacks=" + stacks +
        '}';
  }
}
