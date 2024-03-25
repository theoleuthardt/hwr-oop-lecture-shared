package hwr.oop.poker.tests.application.domain;

import static org.assertj.core.api.Assertions.assertThat;

import hwr.oop.poker.application.domain.Card;
import hwr.oop.poker.application.domain.Color;
import hwr.oop.poker.application.domain.Hand;
import hwr.oop.poker.application.domain.Player;
import hwr.oop.poker.application.domain.Stacks;
import hwr.oop.poker.application.domain.Symbol;
import hwr.oop.poker.application.domain.blinds.BlindConfiguration;
import hwr.oop.poker.application.domain.blinds.SmallBlind;
import hwr.oop.poker.application.domain.decks.TestDoubleDeck;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class HandRemembersStartingPositionTest {

  @Test
  void mementoOfHand_IsValidStartingHand() {
    // given
    final var hand = exampleHand();
    final var memento = hand.restoreMemento();
    // when
    final var startingHand = memento.ascend();
    // then
    final var firstPlayer = new Player("1");
    final var secondPlayer = new Player("2");
    final var thirdPlayer = new Player("3");

    final var soft = new SoftAssertions();

    soft.assertThat(startingHand.preFlopRoundPlayed()).isFalse();
    soft.assertThat(startingHand.flopRoundPlayed()).isFalse();
    soft.assertThat(startingHand.turnRoundPlayed()).isFalse();

    soft.assertThat(startingHand.remainingPlayers())
        .contains(firstPlayer, secondPlayer, thirdPlayer)
        .hasSize(3);

    soft.assertThat(startingHand.smallBlind()).isEqualTo(firstPlayer);
    soft.assertThat(startingHand.bigBlind()).isEqualTo(secondPlayer);
    soft.assertThat(startingHand.blindConfiguration().bigBlind().value()).isEqualTo(2);
    soft.assertThat(startingHand.blindConfiguration().smallBlind().value()).isEqualTo(1);

    soft.assertThat(startingHand.holeCards(firstPlayer)).contains(
        new Card(Color.SPADES, Symbol.ACE),
        new Card(Color.CLUBS, Symbol.ACE)
    );
    soft.assertThat(startingHand.holeCards(secondPlayer)).contains(
        new Card(Color.DIAMONDS, Symbol.ACE),
        new Card(Color.SPADES, Symbol.KING)
    );
    soft.assertThat(startingHand.holeCards(thirdPlayer)).contains(
        new Card(Color.HEARTS, Symbol.ACE),
        new Card(Color.DIAMONDS, Symbol.KING)
    );
    soft.assertAll();
  }

  @Test
  void memento_ContainsInitialDeck() {
    // given
    final var hand = exampleHand();
    final var memento = hand.restoreMemento();
    // when
    final var deck = memento.deck();
    // then
    final var cards = deck.drawAllCards();
    assertThat(cards).startsWith(
        new Card(Color.SPADES, Symbol.ACE),
        new Card(Color.DIAMONDS, Symbol.ACE),
        new Card(Color.HEARTS, Symbol.ACE),
        new Card(Color.CLUBS, Symbol.ACE),
        new Card(Color.SPADES, Symbol.KING)
    );
  }

  @Test
  void memento_ContainsInitialPlayerInformation() {
    // given
    final var firstPlayer = new Player("1");
    final var secondPlayer = new Player("2");
    final var thirdPlayer = new Player("3");
    final var hand = exampleHand();
    // when
    final var memento = hand.restoreMemento();
    // then
    final var players = memento.players();
    final var stacks = memento.stacks();

    final var soft = new SoftAssertions();
    soft.assertThat(players).containsOnly(firstPlayer, secondPlayer, thirdPlayer);
    soft.assertThat(stacks.ofPlayer(firstPlayer).value()).isEqualTo(30_000);
    soft.assertThat(stacks.ofPlayer(secondPlayer).value()).isEqualTo(20_000);
    soft.assertThat(stacks.ofPlayer(thirdPlayer).value()).isEqualTo(10_000);
    soft.assertAll();
  }

  private Hand exampleHand() {
    final var first = new Player("1");
    final var second = new Player("2");
    final var third = new Player("3");
    final var players = List.of(first, second, third);
    final var stacks = Stacks.newBuilder()
        .of(first).is(30_000)
        .of(second).is(20_000)
        .of(third).is(10_000)
        .build();
    final var deck = new TestDoubleDeck(
        new Card(Color.SPADES, Symbol.ACE),
        new Card(Color.DIAMONDS, Symbol.ACE),
        new Card(Color.HEARTS, Symbol.ACE),
        new Card(Color.CLUBS, Symbol.ACE),
        new Card(Color.SPADES, Symbol.KING),
        new Card(Color.DIAMONDS, Symbol.KING),
        new Card(Color.HEARTS, Symbol.KING),
        new Card(Color.CLUBS, Symbol.KING),
        new Card(Color.SPADES, Symbol.QUEEN),
        new Card(Color.DIAMONDS, Symbol.QUEEN),
        new Card(Color.HEARTS, Symbol.QUEEN),
        new Card(Color.CLUBS, Symbol.QUEEN),
        new Card(Color.SPADES, Symbol.JACK),
        new Card(Color.DIAMONDS, Symbol.JACK),
        new Card(Color.HEARTS, Symbol.JACK),
        new Card(Color.CLUBS, Symbol.JACK),
        new Card(Color.SPADES, Symbol.TEN),
        new Card(Color.DIAMONDS, Symbol.TEN),
        new Card(Color.HEARTS, Symbol.TEN),
        new Card(Color.CLUBS, Symbol.TEN)
    );
    final var hand = Hand.newBuilder()
        .players(players)
        .stacks(stacks)
        .blindConfiguration(BlindConfiguration.create(SmallBlind.of(1)))
        .deck(deck)
        .build();
    return hand
        .onCurrentRound(b ->
            b.with(third).fold()
                .with(first).call()
                .with(second).raiseTo(4)
                .with(first).call()
        )
        .onCurrentRound(b -> b.with(first).bet(2).with(second).call())
        .onCurrentRound(b -> b.with(first).check());
  }

}
