package hwr.oop.poker.persistence;

import hwr.oop.poker.application.domain.Hand;
import hwr.oop.poker.application.domain.HandId;
import hwr.oop.poker.application.ports.out.CouldNotLoadHandException;
import hwr.oop.poker.application.ports.out.LoadHandPort;
import hwr.oop.poker.application.ports.out.SaveHandPort;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class CsvFilePersistenceAdapter implements LoadHandPort, SaveHandPort {

  private final Configuration configuration;

  public CsvFilePersistenceAdapter(Configuration configuration) {
    this.configuration = configuration;
  }

  public static Configuration.Builder newConfigBuilder() {
    return new Configuration.Builder();
  }

  @Override
  public Hand loadById(HandId id) {
    final var match = searchFileForId(id);
    if (match.isPresent()) {
      final var row = match.get();
      final var csv = CsvRow.fromString(row);
      return csv.toHand();
    } else {
      throw new CouldNotLoadHandException("id is not available, " + id);
    }
  }

  @Override
  public void saveHand(HandId id, Hand hand) {
    try (final var writer = Files.newBufferedWriter(configuration.csvFilePath())) {
      writer.append(CsvRow.fromHand(hand, id).toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Optional<String> searchFileForId(HandId id) {
    final var file = configuration.csvFilePath();
    try (var stuff = Files.newBufferedReader(file)) {
      return stuff.lines()
          .filter(l -> l.startsWith(id.value()))
          .findFirst();
    } catch (Exception e) {
      throw new CouldNotLoadHandException("idString is not available, " + id);
    }
  }
}
