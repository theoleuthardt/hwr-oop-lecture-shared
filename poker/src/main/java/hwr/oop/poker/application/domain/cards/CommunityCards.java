package hwr.oop.poker.application.domain.cards;

import hwr.oop.poker.application.domain.Card;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class CommunityCards implements CommunityCardsProvider {

  private final Flop flop;
  private final Turn turn;
  private final River river;

  public static CommunityCardsProvider empty() {
    return new CommunityCards();
  }

  public static CommunityCardBuilder flop(List<Card> cards) {
    return flop(Flop.of(cards));
  }

  public static CommunityCardBuilder flop(Card... cards) {
    return flop(Flop.of(Arrays.asList(cards)));
  }

  public static CommunityCardBuilder flop(Flop flop) {
    return new CommunityCardBuilder(flop);
  }


  private CommunityCards() {
    this(null, null, null);
  }

  private CommunityCards(Flop flop) {
    this(flop, null, null);
  }

  private CommunityCards(Flop flop, Turn turn) {
    this(flop, turn, null);
  }

  private CommunityCards(Flop flop, Turn turn, River river) {
    this.flop = flop;
    this.turn = turn;
    this.river = river;
  }

  @Override
  public Collection<Card> cardsDealt() {
    return Stream.of(flop, turn, river)
        .filter(Objects::nonNull)
        .flatMap(Card.Provider::cards)
        .toList();
  }

  @Override
  public Optional<Flop> flop() {
    return Optional.ofNullable(flop);
  }

  @Override
  public Optional<Turn> turn() {
    return Optional.ofNullable(turn);
  }

  @Override
  public Optional<River> river() {
    return Optional.ofNullable(river);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommunityCards that = (CommunityCards) o;
    return Objects.equals(flop, that.flop) && Objects.equals(turn, that.turn)
        && Objects.equals(river, that.river);
  }

  @Override
  public int hashCode() {
    return Objects.hash(flop, turn, river);
  }

  public static class CommunityCardBuilder {

    private final Flop flop;
    private Turn turn;

    CommunityCardBuilder(Flop flop) {
      assert flop != null;
      this.flop = flop;
    }

    public CommunityCardBuilder turn(Card card) {
      this.turn = Turn.of(card);
      return this;
    }

    public CommunityCardBuilder turn(Turn turn) {
      this.turn = turn;
      return this;
    }

    public CommunityCardsProvider noTurnNoRiver() {
      return new CommunityCards(flop);
    }

    public CommunityCardsProvider noRiver() {
      return new CommunityCards(flop, turn);
    }

    public CommunityCardsProvider river(Card card) {
      final River river = River.of(card);
      return new CommunityCards(flop, turn, river);
    }
  }

}
