package hwr.oop.poker.tests.application;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hwr.oop.poker.application.GameActionService;
import hwr.oop.poker.application.domain.Card;
import hwr.oop.poker.application.domain.Color;
import hwr.oop.poker.application.domain.Hand;
import hwr.oop.poker.application.domain.HandId;
import hwr.oop.poker.application.domain.Player;
import hwr.oop.poker.application.domain.Stacks;
import hwr.oop.poker.application.domain.Symbol;
import hwr.oop.poker.application.domain.blinds.BlindConfiguration;
import hwr.oop.poker.application.domain.blinds.SmallBlind;
import hwr.oop.poker.application.domain.decks.TestDoubleDeck;
import hwr.oop.poker.application.ports.in.GameActionUseCase;
import hwr.oop.poker.application.ports.out.LoadHandPort;
import hwr.oop.poker.application.ports.out.SaveHandPort;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GameActionUseCaseTest {

  private LoadHandPort loadHandPortMock;
  private SaveHandPort saveHandPortMock;
  private GameActionUseCase gameActionUseCase;

  @BeforeEach
  void setUp() {
    loadHandPortMock = Mockito.mock();
    saveHandPortMock = Mockito.spy();
    gameActionUseCase = new GameActionService(loadHandPortMock, saveHandPortMock);
  }

  @Test
  void flopPlayed_SmallBlindBets3_LoadsHand_AndSavesUpdatedHand() {
    // given
    final var handId = new HandId("1337");
    when(loadHandPortMock.loadById(handId)).thenReturn(exampleHand());
    final var command = GameActionUseCase.newCommandBuilder()
        .handId("1337")
        .playerId("1")
        .type("BET")
        .toChips(3)
        .build();
    // when
    gameActionUseCase.gameAction(command);
    // then
    verify(saveHandPortMock).saveHand(eq(handId), isNotNull());
  }

  @Test
  void continuingHand_LoadsHand_AndSavesUpdatedHand() {
    // given
    final var handId = new HandId("1337");
    when(loadHandPortMock.loadById(handId)).thenReturn(exampleHand());
    final var command = GameActionUseCase.newCommandBuilder()
        .handId("1337")
        .playerId("1")
        .type("BET")
        .toChips(3)
        .build();
    // when
    gameActionUseCase.gameAction(command);
    // then
    final var updatedHand = exampleHand()
        .onCurrentRound(r -> r.with(new Player("1")).bet(3));
    verify(saveHandPortMock).saveHand(eq(handId), isNotNull());
  }

  private Hand exampleHand() {
    final var first = new Player("1");
    final var second = new Player("2");
    final var third = new Player("3");
    final var players = List.of(first, second, third);
    final var stacks = Stacks.newBuilder().of(first).is(30_000).of(second).is(20_000).of(third)
        .is(10_000).build();
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
    final var hand = Hand.newBuilder().players(players).stacks(stacks)
        .blindConfiguration(BlindConfiguration.create(SmallBlind.of(1))).deck(deck).build();
    return hand.onCurrentRound(
        b -> b.with(third).fold().with(first).call().with(second).raiseTo(4).with(first).call());
  }
}
