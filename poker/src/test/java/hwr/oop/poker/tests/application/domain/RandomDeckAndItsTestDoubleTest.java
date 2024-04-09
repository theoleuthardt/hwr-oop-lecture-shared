package hwr.oop.poker.tests.application.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import hwr.oop.poker.application.domain.Card;
import hwr.oop.poker.application.domain.Color;
import hwr.oop.poker.application.domain.Deck;
import hwr.oop.poker.application.domain.Symbol;
import hwr.oop.poker.application.domain.decks.RandomDeck;
import hwr.oop.poker.application.domain.decks.UnshuffledDeck;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Provide a Deck that Cards can be drawn from")
class RandomDeckAndItsTestDoubleTest {

  @Test
  @DisplayName("new Deck, contains each card exactly once")
  void newDeck_ContainsEachCardExactlyOnce() {
    final Deck deck = new RandomDeck();
    final List<Card> cards = deck.drawAllCards();
    assertThat(cards).allMatch(c -> {
      final long numberOfEqualCards = cards.stream().filter(c::equals).count();
      return numberOfEqualCards == 1;
    });
  }

  @Test
  @DisplayName("new Deck, contains 52 cards")
  void newDeck_52Cards() {
    final Deck deck = new RandomDeck();
    final List<Card> cards = deck.drawAllCards();
    assertThat(cards).size().isEqualTo(52);
  }

  @Test
  @DisplayName("draw all cards, draw once more, throws exception")
  void emptyDeck_DrawOnceMore_ThrowsException() {
    final Deck deck = new RandomDeck();
    deck.drawAllCards();
    assertThrows(Deck.DrawFromEmptyDeckException.class, deck::draw);
  }

  @Test
  @DisplayName("draw all cards, draw once more, throws exception")
  void emptyDeck_PeekOnTopCard_ThrowsException() {
    final Deck deck = new RandomDeck();
    deck.drawAllCards();
    assertThrows(Deck.DrawFromEmptyDeckException.class, deck::top);
  }

  @Test
  @DisplayName("draw all cards, remove top card, throws exception")
  void emptyDeck_RemoveFirstCard_ThrowsException() {
    final Deck deck = new RandomDeck();
    deck.drawAllCards();
    assertThrows(Deck.DrawFromEmptyDeckException.class, deck::burn);
  }

  @Nested
  @DisplayName("Provide a test double Deck used for tests")
  class TestDoubleDeckTest {

    @Test
    @DisplayName("only (H7,JS,AC) in deckString, exactly (H7,JS,AC) can be drawn")
    void testDouble_ThreeCards_ExactlyTheseCardsPresent() {
      final Deck deck = new UnshuffledDeck(
          new Card(Color.HEARTS, Symbol.SEVEN),
          new Card(Color.SPADES, Symbol.JACK),
          new Card(Color.CLUBS, Symbol.ACE)
      );
      final List<Card> cards = deck.drawAllCards();
      assertThat(cards).containsExactly(
          new Card(Color.HEARTS, Symbol.SEVEN),
          new Card(Color.SPADES, Symbol.JACK),
          new Card(Color.CLUBS, Symbol.ACE)
      );
    }

    @Test
    @DisplayName("only (H7,H8) in deckString, exactly (H7,H8) can be drawn")
    void testDoubleDeckWithFiveCards_AllDrawnCardsAreTheOnesProvided() {
      final Deck deck = new UnshuffledDeck(
          new Card(Color.HEARTS, Symbol.SEVEN),
          new Card(Color.HEARTS, Symbol.EIGHT)
      );
      final List<Card> cards = deck.drawAllCards();
      assertThat(cards).containsExactly(
          new Card(Color.HEARTS, Symbol.SEVEN),
          new Card(Color.HEARTS, Symbol.EIGHT)
      );
    }

    @Test
    @DisplayName("draw all cards, draw once more, throws exception")
    void emptyDeck_DrawOnceMore_ThrowsException() {
      final Deck deck = new UnshuffledDeck(
          new Card(Color.HEARTS, Symbol.SEVEN)
      );
      deck.drawAllCards();
      assertThrows(Deck.DrawFromEmptyDeckException.class, deck::draw);
    }

    @Test
    @DisplayName("draw all cards, draw once more, throws exception")
    void emptyDeck_PeekOnTopCard_ThrowsException() {
      final Deck deck = new UnshuffledDeck(
          new Card(Color.HEARTS, Symbol.SEVEN)
      );
      deck.drawAllCards();
      assertThrows(Deck.DrawFromEmptyDeckException.class, deck::top);
    }

    @Test
    @DisplayName("draw all cards, remove top card, throws exception")
    void emptyDeck_RemoveFirstCard_ThrowsException() {
      final Deck deck = new UnshuffledDeck(
          new Card(Color.HEARTS, Symbol.SEVEN)
      );
      deck.drawAllCards();
      assertThrows(Deck.DrawFromEmptyDeckException.class, deck::burn);
    }

  }
}
