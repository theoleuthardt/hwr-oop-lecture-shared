package hwr.oop.poker.application.domain;

import java.util.Objects;

public class Player {

  private final String identifier;

  public Player(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Player player = (Player) o;
    return Objects.equals(identifier, player.identifier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(identifier);
  }

  @Override
  public String toString() {
    return "Player " + identifier;
  }

  public String id() {
    return identifier;
  }
}
