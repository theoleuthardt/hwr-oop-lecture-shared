package hwr.oop.examples;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

class ExampleConsoleUserInterfaceTest {

    @Test
    void consoleUI_TypeThreeAndFour_OutputIsSeven() {
        // given
        InputStream inputStream = createInputStreamForInput("3\n4\n");
        OutputStream outputStream = new ByteArrayOutputStream();
        ConsoleUserInterface consoleUI = new ConsoleUserInterface(outputStream, inputStream);
        // when
        consoleUI.start();
        // then
        String output = retrieveResultFrom(outputStream);
        assertThat(output).isEqualTo("7");
    }

    @Test
    @Disabled("This is a manual tests, thus it should not be part of our test suite")
    void manualTest() {
        ConsoleUserInterface ui = new ConsoleUserInterface(System.out, System.in);
        ui.start();
    }

    private String retrieveResultFrom(OutputStream outputStream) {
        String outputText = outputStream.toString();
        String key = "output:";
        return outputText.substring(outputText.indexOf(key) + key.length()).trim();
    }

    private InputStream createInputStreamForInput(String input) {
        byte[] inputInBytes = input.getBytes();
        return new ByteArrayInputStream(inputInBytes);
    }

    // Just as a static class, to have a gist one pager.
    // This is SUT. Thus, it should be under src/main!
    static class ConsoleUserInterface {

        private final PrintStream out;
        private final Scanner in;

        public ConsoleUserInterface(OutputStream out, InputStream in) {
            this.out = new PrintStream(out);
            this.in = new Scanner(in);
        }

        public void start() {
            out.println("Enter number 1");
            int x = in.nextInt();
            out.println("Enter number 2");
            int y = in.nextInt();
            out.println("output: " + (x + y));
        }
    }
}