import org.junit.jupiter.api.Test;
import org.pyatkin.Main;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    // Test case for correct input arguments
    @Test
    void testValidArguments() {
        String[] args = {"--code=USD", "--date=2022-10-08"};
        Main.main(args);
        // Since this test runs the program and prints the output,
        // manual verification of the output is required.
        // Check the console output for the expected currency rate.
    }

    // Test case for incorrect number of arguments
    @Test
    void testInvalidNumberOfArguments() {
        String[] args = {"--code=USD"};
        assertEquals("Usage: java -jar quwi-0.1.jar --code=USD --date=2022-10-08", getConsoleOutput(args));
    }

    // Test case for missing code argument
    @Test
    void testMissingCodeArgument() {
        String[] args = {"--date=2022-10-08"};
        assertEquals("Invalid arguments. Usage: currency_rates --code=USD --date=2022-10-08", getConsoleOutput(args));
    }

    // Test case for missing date argument
    @Test
    void testMissingDateArgument() {
        String[] args = {"--code=USD"};
        assertEquals("Invalid arguments. Usage: currency_rates --code=USD --date=2022-10-08", getConsoleOutput(args));
    }

    // Test case for invalid date format
    @Test
    void testInvalidDateFormat() {
        String[] args = {"--code=USD", "--date=2022/10/08"};
        assertEquals("Invalid date format. Use YYYY-MM-DD format.", getConsoleOutput(args));
    }

    // Test case for non-existent currency code
    @Test
    void testNonExistentCurrencyCode() {
        String[] args = {"--code=XYZ", "--date=2022-10-08"};
        assertEquals("Currency code not found or invalid date.", getConsoleOutput(args));
    }

    // Helper method to capture the console output for testing
    private String getConsoleOutput(String[] args) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalOut = System.out;
        System.setOut(printStream);

        Main.main(args);

        System.out.flush();
        System.setOut(originalOut);
        return outputStream.toString().trim();
    }
}
