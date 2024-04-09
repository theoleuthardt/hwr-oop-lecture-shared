package hwr.oop.poker.application.domain;

import java.util.Objects;

public record Player(String id) {

  public Player {
    Objects.requireNonNull(id);
  }

  @Override
  public String toString() {
    return "Player " + id;
  }

}
