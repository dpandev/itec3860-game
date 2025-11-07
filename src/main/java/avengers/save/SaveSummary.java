package avengers.save;

/** Represents a summary of a game save. */
public final class SaveSummary {
  private final int slot;
  private final String timestamp;
  private final String roomName;
  private final int hp;

  /**
   * Constructor for SaveSummary
   *
   * @param slot The save slot number
   * @param timestamp The timestamp of the save
   * @param roomName The name of the current room
   * @param hp The player's health points
   */
  public SaveSummary(int slot, String timestamp, String roomName, int hp) {
    this.slot = slot;
    this.timestamp = timestamp;
    this.roomName = roomName;
    this.hp = hp;
  }

  public int getSlot() {
    return slot;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public String getRoomName() {
    return roomName;
  }

  public int getHp() {
    return hp;
  }
}
