package avengers.domain.model;

/** Example domain model class. Replace this with actual game entities (Player, Room, Item, etc.) */
public class Entity {
  private String id;
  private String name;

  public Entity(String id, String name) {
    this.id = id;
    this.name = name;
  }

  //
  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
