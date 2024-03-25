package hwr.oop.poker.tests.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import hwr.oop.poker.application.CreateGameService;
import hwr.oop.poker.application.domain.HandId;
import hwr.oop.poker.application.ports.in.CreateGameUseCase;
import hwr.oop.poker.application.ports.out.SaveHandPort;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CreateGameUseCaseTest {

  private SaveHandPort saveHandPortMock;
  private CreateGameUseCase createGameUseCase;

  @BeforeEach
  void setUp() {
    saveHandPortMock = Mockito.spy();
    createGameUseCase = new CreateGameService(saveHandPortMock);
  }

  @Test
  void createHand1337_SaveOfHandOnPersistenceIsCalled() {
    final var command = CreateGameUseCase.newCommandBuilder()
        .playersAndStacks(Map.of("1", 10_000, "2", 30_000))
        .smallBlind(100)
        .gameId("1337")
        .build();
    createGameUseCase.createGame(command);
    verify(saveHandPortMock)
        .saveHand(eq(new HandId("1337")), any());
  }
}
