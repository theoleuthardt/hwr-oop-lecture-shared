package hwr.oop.poker.tests.persistence;

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
import hwr.oop.poker.application.ports.out.LoadHandPort;
import hwr.oop.poker.application.ports.out.SaveHandPort;
import hwr.oop.poker.persistence.CsvFilePersistenceAdapter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NewFilePersistenceTest {

  private static final String FILE_NAME = "src/test/resources/newFile.csv";

  private Path path;
  private LoadHandPort loadHandPort;
  private SaveHandPort saveHandPort;

  @BeforeEach
  void setUp() throws IOException {
    final var file = new File(FILE_NAME);
    file.createNewFile();
    this.path = file.toPath();
    final var configuration = CsvFilePersistenceAdapter.newConfigBuilder()
        .csvFile(path)
        .build();
    final var adapter = new CsvFilePersistenceAdapter(configuration);
    this.loadHandPort = adapter;
    this.saveHandPort = adapter;

    try (var stuff = Files.newBufferedWriter(path)) {
      stuff.append("handId;playersAndStacks;smallBlindString;deckString;playsString");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @AfterEach
  void tearDown() {
    try {
      Files.delete(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void saveLoad_LoadedIsEqualToExampleHand() {
    final var hand = exampleHand();
    final var id = new HandId("1");
    saveHandPort.saveHand(id, hand);
    final var retrieved = loadHandPort.loadById(id);
    final SoftAssertions soft = new SoftAssertions();
    soft.assertThat(retrieved.plays().toList()).containsSequence(hand.plays().toList());
    soft.assertThat(retrieved.blindConfiguration()).isEqualTo(hand.blindConfiguration());
    soft.assertThat(retrieved.remainingPlayers()).isEqualTo(hand.remainingPlayers());
    soft.assertThat(retrieved.stacks()).isEqualTo(hand.stacks());
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
