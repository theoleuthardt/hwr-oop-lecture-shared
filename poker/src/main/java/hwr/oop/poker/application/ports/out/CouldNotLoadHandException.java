package hwr.oop.poker.application.ports.out;

public class CouldNotLoadHandException extends RuntimeException {

    public CouldNotLoadHandException(String message) {
        super(message);
    }
}
