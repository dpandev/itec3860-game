package avengers.client.command;

/** Example command class. Replace with actual commands (MoveCommand, AttackCommand, etc.) */
public class Command {
  private String action;

  public Command(String action) {
    this.action = action;
  }

  public void execute() {
    System.out.println("Executing: " + action);
  }
}
