package hwr.oop.poker.tests.persistence;

import static hwr.oop.poker.tests.Utils.resourceAsPath;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hwr.oop.poker.application.domain.Card;
import hwr.oop.poker.application.domain.Color;
import hwr.oop.poker.application.domain.Hand;
import hwr.oop.poker.application.domain.HandId;
import hwr.oop.poker.application.domain.Player;
import hwr.oop.poker.application.domain.Symbol;
import hwr.oop.poker.application.ports.out.CouldNotLoadHandException;
import hwr.oop.poker.application.ports.out.LoadHandPort;
import hwr.oop.poker.persistence.CsvFilePersistenceAdapter;
import hwr.oop.poker.persistence.CsvRow;
import java.util.UUID;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CsvFilePersistenceTest {

  private LoadHandPort loadHandPort;

  @BeforeEach
  void setUp() {
    final var configuration = CsvFilePersistenceAdapter.newConfigBuilder()
        .csvFile(resourceAsPath("loadExampleTest.csv"))
        .build();
    loadHandPort = new CsvFilePersistenceAdapter(configuration);
  }

  @Test
  void loadById_UnavailableHand_ThrowsException() {
    final var handId = new HandId("13337");
    assertThatThrownBy(() -> loadHandPort.loadById(handId))
        .isInstanceOf(CouldNotLoadHandException.class)
        .hasMessageContainingAll(
            "id", "not available",
            handId.toString()
        );
  }

  @Test
  void loadHandById_ValidHandIdOfTestDataHand_ReturnsHand() {
    final var handId = new HandId("d6f92a0a-f5e1-42a8-9432-42c4221abb8b");
    final var hand = loadHandPort.loadById(handId);
    assertHandIsValidExample(hand);
  }

  @Test
  void exampleCsvRow_ConvertsToValidExampleHand() {
    final var row = exampleCsvRow();
    final var hand = row.toHand();
    assertHandIsValidExample(hand);
  }

  @Test
  void csvRowFromValidExampleFileRow_SeparatesOnSemicolons() {
    final var stuff = "d6f92a0a-f5e1-42a8-9432-42c4221abb8b;1-30000,2-20000,3-10000;1;AS,AD,AH,AC,KS,KD,KH,KC,QS,QD,QH,QC,JS,JD,JH,JC,TS,TD,TH,TC;3-F,1-CA,2-R-4,1-CA,1-B-2,2-CA,1-CH";
    final var row = CsvRow.fromString(stuff);
    final var soft = new SoftAssertions();
    soft.assertThat(row.idString()).isEqualTo("d6f92a0a-f5e1-42a8-9432-42c4221abb8b");
    soft.assertThat(row.playersAndStacks()).isEqualTo("1-30000,2-20000,3-10000");
    soft.assertThat(row.deckString()).startsWith("AS,AD,AH,AC,KS,KD,KH,KC,QS,QD,QH,QC");
    soft.assertThat(row.playsString()).isEqualTo("3-F,1-CA,2-R-4,1-CA,1-B-2,2-CA,1-CH");
    soft.assertThat(row.smallBlindString()).isEqualTo("1");
    soft.assertAll();
  }

  @Test
  void handFromExampleCsvRow_ParsedToCsv_SameCsv() {
    // given
    final var row = exampleCsvRow();
    final var hand = row.toHand();
    // when
    final var parsedRow = CsvRow.fromHand(hand);
    // then
    final var soft = new SoftAssertions();
    soft.assertThat(parsedRow.playersAndStacks()).isEqualTo("1-30000,2-20000,3-10000");
    soft.assertThat(parsedRow.deckString()).startsWith("AS,AD,AH,AC,KS,KD,KH,KC,QS,QD,QH,QC");
    soft.assertThat(parsedRow.playsString()).isEqualTo("3-F,1-CA,2-R-4,1-CA,1-B-2,2-CA,1-CH");
    soft.assertThat(parsedRow.smallBlindString()).isEqualTo("1");
    soft.assertAll();
  }

  private CsvRow exampleCsvRow() {
    return new CsvRow(
        UUID.randomUUID().toString(),
        "1-30000,2-20000,3-10000",
        "1",
        "AS,AD,AH,AC,KS,KD,KH,KC,QS,QD,QH,QC,JS,JD,JH,JC,TS,TD,TH,TC",
        "3-F,1-CA,2-R-4,1-CA,1-B-2,2-CA,1-CH"
    );
  }

  private void assertHandIsValidExample(Hand hand) {
    final Player firstPlayer = new Player("1");
    final Player secondPlayer = new Player("2");
    final Player thirdPlayer = new Player("3");

    final SoftAssertions soft = new SoftAssertions();

    soft.assertThat(hand.preFlopRoundPlayed()).isTrue();
    soft.assertThat(hand.flopRoundPlayed()).isTrue();
    soft.assertThat(hand.turnRoundPlayed()).isFalse();

    soft.assertThat(hand.remainingPlayers())
        .contains(firstPlayer, secondPlayer)
        .doesNotContain(thirdPlayer);

    soft.assertThat(hand.smallBlind()).isEqualTo(firstPlayer);
    soft.assertThat(hand.bigBlind()).isEqualTo(secondPlayer);
    soft.assertThat(hand.blindConfiguration().bigBlind().value()).isEqualTo(2);
    soft.assertThat(hand.blindConfiguration().smallBlind().value()).isEqualTo(1);

    soft.assertThat(hand.holeCards(firstPlayer)).contains(
        new Card(Color.SPADES, Symbol.ACE),
        new Card(Color.CLUBS, Symbol.ACE)
    );
    soft.assertThat(hand.holeCards(secondPlayer)).contains(
        new Card(Color.DIAMONDS, Symbol.ACE),
        new Card(Color.SPADES, Symbol.KING)
    );
    soft.assertThat(hand.holeCards(thirdPlayer)).contains(
        new Card(Color.HEARTS, Symbol.ACE),
        new Card(Color.DIAMONDS, Symbol.KING)
    );
    soft.assertAll();
  }

}
