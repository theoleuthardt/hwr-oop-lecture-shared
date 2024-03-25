package hwr.oop.poker.persistence;

import hwr.oop.poker.application.domain.Hand;
import hwr.oop.poker.application.domain.HandId;
import hwr.oop.poker.application.ports.out.CouldNotLoadHandException;
import hwr.oop.poker.application.ports.out.LoadHandPort;

public class CsvFilePersistenceAdapter implements LoadHandPort {

    private final Configuration configuration;

    public CsvFilePersistenceAdapter(Configuration configuration) {
        this.configuration = configuration;
    }

    public static Configuration.Builder newConfigBuilder() {
        return new Configuration.Builder();
    }

    @Override
    public Hand loadById(HandId id) {
        if (id.value().equals("1337")) {
            return null;
        } else {
            throw new CouldNotLoadHandException("idString is not available: " + id);
        }
    }

}
