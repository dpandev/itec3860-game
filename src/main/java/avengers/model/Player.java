package avengers.model;

/** Represents the player character in the game. */
public final class Player extends Character {
  private String currentRoomId;

  /**
   * Constructor for Player.
   *
   * @param name The name of the player.
   * @param description The description of the player.
   * @param hp The current health points of the player.
   * @param maxHp The maximum health points of the player.
   * @param baseDamage The base damage the player can deal.
   * @param defense The defense value of the player.
   */
  public Player(String name, String description, int hp, int maxHp, int baseDamage, int defense) {
    super(name, description, hp, maxHp, baseDamage, defense);
    this.currentRoomId = null;
  }

  public String getCurrentRoomId() {
    return currentRoomId;
  }

  /**
   * Method to move the player to a different room.
   *
   * @param roomId The ID of the room to move to.
   */
  public void moveToRoom(String roomId) {
    this.currentRoomId = roomId;
  }

  /** Method to handle losing critical key items. */
  public void loseCritialKeyItems() {
    // TODO: implement logic to lose critical key items
  }
}
