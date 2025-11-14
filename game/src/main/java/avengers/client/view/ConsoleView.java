package avengers.client.view;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * A simple console view for reading input and writing output to the console. Uses System.in for
 * input and System.out for output.
 */
public class ConsoleView {
  private final Scanner in;
  private final PrintStream out;

  /** Construct a ConsoleView using System.in and System.out. */
  public ConsoleView() {
    this.in = new Scanner(System.in);
    this.out = System.out;
  }

  /**
   * Read a line from the console. Returns null if end of input is reached or an error occurs.
   *
   * @return the read line, or null if no more input
   * @throws IOException if an I/O error occurs
   */
  public String readLine() throws IOException {
    try {
      if (in.hasNextLine()) {
        return in.nextLine();
      }
      // done here; nothing left to read
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Print a line to the console. If the string is null, prints an empty line. Flushes the output
   * after printing.
   *
   * @param s - the string to print, can be null
   */
  public void println(String s) {
    out.println(s == null ? "" : s);
    out.flush();
  }

  /**
   * Print a formatted line to the console using String.format. Flushes the output after printing.
   * No automatic newline is added.
   *
   * @param format - the format string
   * @param args - the arguments for the format string
   */
  public void printf(String format, Object... args) {
    out.printf(format, args);
    out.flush();
  }

  /**
   * Close the console view, releasing resources. Closes the input scanner but does not close
   * System.out.
   */
  public void close() {
    try {
      in.close();
    } catch (Exception ignored) {
      // checkstyle: stop IllegalCatch
    }
    out.flush();
  }
}
