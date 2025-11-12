package avengers.model;

//  import avengers.model.item.Equipment;
import avengers.model.item.Inventory;
import java.util.ArrayList;
import java.util.List;

/** Represents the player character in the game. */
public final class Player extends Character {
  private final Inventory inventory;
  //  private final Equipment equipment;
  private String currentRoomId;
  private final List<Ally> allies;

  /**
   * Constructor for Player.
   *
   * @param id The unique identifier for the player.
   * @param name The name of the player.
   * @param description The description of the player.
   * @param hp The current health points of the player.
   * @param maxHp The maximum health points of the player.
   * @param baseDamage The base damage the player can deal.
   * @param defense The defense value of the player.
   */
  public Player(
      String id, String name, String description, int hp, int maxHp, int baseDamage, int defense) {
    super(id, name, description, hp, maxHp, baseDamage, defense);
    this.inventory = new Inventory();
    //    this.equipment = new Equipment();
    this.currentRoomId = null;
    this.allies = new ArrayList<>();
  }

  public String getCurrentRoomId() {
    return currentRoomId;
  }

  public Inventory getInventory() {
    return inventory;
  }

  //  public Equipment getEquipment() {
  //    return equipment;
  //  }

  public List<Ally> getAllies() {
    return List.copyOf(allies);
  }

  /**
   * Method to move the player to a different room.
   *
   * @param roomId The ID of the room to move to.
   */
  public void moveTo(String roomId) {
    this.currentRoomId = roomId;
  }

  /**
   * Adds an ally to the player's party.
   *
   * @param ally The ally to add.
   */
  public void addAlly(Ally ally) {
    allies.add(ally);
  }

  /**
   * Removes an ally from the player's party.
   *
   * @param ally The ally to remove.
   * @return true if the ally was removed, false otherwise.
   */
  public boolean removeAlly(Ally ally) {
    return allies.remove(ally);
  }

  /** Method to handle losing critical key items. */
  public void loseCriticalKeyItems() {
    // TODO: implement logic to lose critical key items
  }
}
