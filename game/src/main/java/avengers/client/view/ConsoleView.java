package avengers.client.view;

/** Example view class. Replace with actual view implementations (ConsoleView, MenuView, etc.) */
public class ConsoleView {
  public void display(String message) {
    System.out.println(message);
  }

  public void displayError(String error) {
    System.err.println("ERROR: " + error);
  }
}
