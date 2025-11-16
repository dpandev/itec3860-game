package avengers.service;

import avengers.domain.model.Entity;

/** Example service class. Replace with actual game services (GameService, SaveService, etc.) */
public class GameService {
  //
  public Entity createEntity(String id, String name) {
    return new Entity(id, name);
  }

  public String processEntity(Entity entity) {
    return "Processing: " + entity.getName();
  }
}
