package hwr.oop.poker.application;

import hwr.oop.poker.application.domain.Hand;
import hwr.oop.poker.application.domain.HandId;
import hwr.oop.poker.application.domain.Player;
import hwr.oop.poker.application.domain.Stacks;
import hwr.oop.poker.application.domain.Stacks.StacksBuilder;
import hwr.oop.poker.application.domain.blinds.BlindConfiguration;
import hwr.oop.poker.application.domain.blinds.SmallBlind;
import hwr.oop.poker.application.domain.decks.RandomDeck;
import hwr.oop.poker.application.ports.in.CreateGameUseCase;
import hwr.oop.poker.application.ports.out.SaveHandPort;
import java.util.List;
import java.util.Map;

public class CreateGameService implements CreateGameUseCase {

  private final SaveHandPort saveHandPort;

  public CreateGameService(SaveHandPort saveHandPort) {

    this.saveHandPort = saveHandPort;
  }

  @Override
  public void createGame(CreateGameCommand command) {
    final var handId = convertHandId(command);
    final var convertPlayers = convertPlayers(command.playersAndStacks());
    final var stacks = convertStack(command.playersAndStacks());
    final var blinds = convertBlinds(command);
    final var hand = Hand.newBuilder()
        .deck(new RandomDeck())
        .players(convertPlayers)
        .stacks(stacks)
        .blindConfiguration(blinds)
        .build();
    saveHandPort.saveHand(handId, hand);
  }

  private BlindConfiguration convertBlinds(CreateGameCommand command) {
    return BlindConfiguration.create(
        SmallBlind.of(command.smallBlind()));
  }

  private HandId convertHandId(CreateGameCommand command) {
    return new HandId(command.gameId());
  }

  private List<Player> convertPlayers(Map<String, Integer> map) {
    return map.keySet().stream().map(Player::new).toList();
  }

  private Stacks convertStack(Map<String, Integer> map) {
    final StacksBuilder stackBuilder = Stacks.newBuilder();
    map.forEach((k, v) -> stackBuilder.of(new Player(k)).is(v));
    return stackBuilder.build();
  }
}
