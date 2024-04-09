package hwr.oop.poker.application.domain;

import hwr.oop.poker.application.domain.blinds.BlindConfiguration;
import java.util.Collections;
import java.util.List;

public record HandMemento(Deck deck,
                          List<Player> players,
                          BlindConfiguration blinds,
                          Stacks stacks) {


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

  public List<Player> players() {
    return Collections.unmodifiableList(players);
  }

}
