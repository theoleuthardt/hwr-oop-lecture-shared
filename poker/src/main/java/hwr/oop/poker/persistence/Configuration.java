package hwr.oop.poker.persistence;

public record Configuration(String directory) {
    // nothing else to do here!

    public static class Builder {
        private String directory;

        Builder() {
            this.directory = null;
        }

        public Builder directory(String directory) {
            this.directory = directory;
            return this;
        }

        public Configuration build() {
            return new Configuration(directory);
        }
    }
}
