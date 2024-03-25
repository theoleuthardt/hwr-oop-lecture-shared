package hwr.oop.poker.tests.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hwr.oop.poker.application.domain.HandId;
import hwr.oop.poker.application.domain.Player;
import hwr.oop.poker.application.ports.out.CouldNotLoadHandException;
import hwr.oop.poker.application.ports.out.LoadHandPort;
import hwr.oop.poker.persistence.CsvFilePersistenceAdapter;
import hwr.oop.poker.persistence.CsvRow;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FilePersistenceTest {

  private LoadHandPort loadHandPort;

  @BeforeEach
  void setUp() {
    final var configuration = CsvFilePersistenceAdapter.newConfigBuilder()
        .directory("test.csv")
        .build();
    loadHandPort = new CsvFilePersistenceAdapter(configuration);
  }

  @Test
  void loadExampleTestDataHand_ResultIsNotNull() {
    final var id = new HandId("1337");
    final var hand = loadHandPort.loadById(id);
    assertThat(hand).isNull();
  }

  @Test
  void loadUnavailableHand_ThrowsException() {
    final var handId = new HandId("13337");
    assertThatThrownBy(() -> loadHandPort.loadById(handId))
        .isInstanceOf(CouldNotLoadHandException.class)
        .hasMessageContainingAll(
            "idString is not available",
            handId.toString()
        );
  }

  @Test
  void canCreateCSVRow() {
    // given
    final var row = new CsvRow(
        UUID.randomUUID().toString(),
        "1-10000,2-10000,3-10000",
        "1",
        "AS,AD,AH,AC,KS,KD,KH,KC,QS,QD,QH,QC,JS,JD,JH,JC,TS,TD,TH,TC",
        "3-F,1-CA,2-R-4,1-CA,1-B-2,2-CA,1-CH"
    );
    // when
    final var hand = row.toHand();
    // then
    final var remainingPlayers = hand.remainingPlayers();
    assertThat(remainingPlayers)
        .contains(new Player("1"), new Player("2"))
        .doesNotContain(new Player("3"));

    final var preFlopPlayed = hand.preFlopRoundPlayed();
    assertThat(preFlopPlayed).isTrue();

    final var flopPlayed = hand.flopRoundPlayed();
    assertThat(flopPlayed).isTrue();

    final var turnPlayed = hand.turnRoundPlayed();
    assertThat(turnPlayed).isFalse();
  }
}
