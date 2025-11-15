package avengers.client.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import avengers.domain.utils.CommandToken;
import avengers.domain.utils.Verb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InputCommandParserTest {
  private InputCommandParser parser;

  @BeforeEach
  void setUp() {
    parser = new InputCommandParser();
  }

  @Test
  void testParserCreation() {
    assertNotNull(parser);
  }

  // Empty/null input tests
  @Test
  void testParseNullInput() {
    CommandToken token = parser.parse(null);
    assertEquals(Verb.UNKNOWN, token.verb());
    assertNull(token.target());
    assertTrue(token.args().isEmpty());
  }

  @Test
  void testParseEmptyInput() {
    CommandToken token = parser.parse("");
    assertEquals(Verb.UNKNOWN, token.verb());
    assertNull(token.target());
    assertTrue(token.args().isEmpty());
  }

  @Test
  void testParseWhitespaceOnly() {
    CommandToken token = parser.parse("   ");
    assertEquals(Verb.UNKNOWN, token.verb());
    assertNull(token.target());
  }

  // Movement verbs (GO, EXPLORE, MAP)
  @Test
  void testParseGoWithDirection() {
    CommandToken token = parser.parse("go north");
    assertEquals(Verb.GO, token.verb());
    assertEquals("north", token.target());
    assertEquals(1, token.args().size());
  }

  @Test
  void testParseGoWithShortcut() {
    CommandToken token = parser.parse("go n");
    assertEquals(Verb.GO, token.verb());
    assertEquals("north", token.target());
  }

  @Test
  void testParseSingleDirectionShortcut() {
    CommandToken token = parser.parse("n");
    assertEquals(Verb.GO, token.verb());
    assertEquals("north", token.target());
  }

  @Test
  void testParseSingleDirectionFull() {
    CommandToken token = parser.parse("south");
    assertEquals(Verb.GO, token.verb());
    assertEquals("south", token.target());
  }

  @Test
  void testParseGoWithoutDirection() {
    CommandToken token = parser.parse("go");
    assertEquals(Verb.GO, token.verb());
    assertNull(token.target());
  }

  @Test
  void testParseExplore() {
    CommandToken token = parser.parse("explore");
    assertEquals(Verb.EXPLORE, token.verb());
    assertNull(token.target());
  }

  @Test
  void testParseMap() {
    CommandToken token = parser.parse("map");
    assertEquals(Verb.MAP, token.verb());
    assertNull(token.target());
  }

  // Inventory verbs (INVENTORY, PICKUP, DROP, EQUIP, UNEQUIP, USE)
  @Test
  void testParseInventory() {
    CommandToken token = parser.parse("inventory");
    assertEquals(Verb.INVENTORY, token.verb());
    assertNull(token.target());
  }

  @Test
  void testParsePickupSingleWord() {
    CommandToken token = parser.parse("pickup sword");
    assertEquals(Verb.PICKUP, token.verb());
    assertEquals("sword", token.target());
  }

  @Test
  void testParsePickupMultiWord() {
    CommandToken token = parser.parse("pickup steel sword");
    assertEquals(Verb.PICKUP, token.verb());
    assertEquals("steel sword", token.target());
    assertEquals(2, token.args().size());
  }

  @Test
  void testParseDropMultiWord() {
    CommandToken token = parser.parse("drop rusty shield");
    assertEquals(Verb.DROP, token.verb());
    assertEquals("rusty shield", token.target());
  }

  @Test
  void testParseEquip() {
    CommandToken token = parser.parse("equip leather armor");
    assertEquals(Verb.EQUIP, token.verb());
    assertEquals("leather armor", token.target());
  }

  @Test
  void testParseUnequip() {
    CommandToken token = parser.parse("unequip helmet");
    assertEquals(Verb.UNEQUIP, token.verb());
    assertEquals("helmet", token.target());
  }

  @Test
  void testParseUse() {
    CommandToken token = parser.parse("use health potion");
    assertEquals(Verb.USE, token.verb());
    assertEquals("health potion", token.target());
  }

  // Interaction verbs (INSPECT, ACTIVATE, SOLVE, HINT)
  @Test
  void testParseInspect() {
    CommandToken token = parser.parse("inspect ancient door");
    assertEquals(Verb.INSPECT, token.verb());
    assertEquals("ancient door", token.target());
  }

  @Test
  void testParseActivate() {
    CommandToken token = parser.parse("activate lever");
    assertEquals(Verb.ACTIVATE, token.verb());
    assertEquals("lever", token.target());
  }

  @Test
  void testParseSolve() {
    CommandToken token = parser.parse("solve puzzle");
    assertEquals(Verb.SOLVE, token.verb());
    assertEquals("puzzle", token.target());
  }

  @Test
  void testParseHint() {
    CommandToken token = parser.parse("hint");
    assertEquals(Verb.HINT, token.verb());
    assertNull(token.target());
  }

  // Combat verbs (ATTACK, DEFEND, IGNORE)
  @Test
  void testParseAttack() {
    CommandToken token = parser.parse("attack ice troll");
    assertEquals(Verb.ATTACK, token.verb());
    assertEquals("ice troll", token.target());
  }

  @Test
  void testParseDefend() {
    CommandToken token = parser.parse("defend");
    assertEquals(Verb.DEFEND, token.verb());
    assertNull(token.target());
  }

  @Test
  void testParseIgnore() {
    CommandToken token = parser.parse("ignore wild dog");
    assertEquals(Verb.IGNORE, token.verb());
    assertEquals("wild dog", token.target());
  }

  // System verbs (HELP, SAVE, LOAD, QUIT)
  @Test
  void testParseHelp() {
    CommandToken token = parser.parse("help");
    assertEquals(Verb.HELP, token.verb());
    assertNull(token.target());
  }

  @Test
  void testParseHelpShortcut() {
    CommandToken token = parser.parse("?");
    assertEquals(Verb.HELP, token.verb());
    assertNull(token.target());
  }

  @Test
  void testParseSave() {
    CommandToken token = parser.parse("save slot1");
    assertEquals(Verb.SAVE, token.verb());
    assertEquals("slot1", token.target());
  }

  @Test
  void testParseLoad() {
    CommandToken token = parser.parse("load slot2");
    assertEquals(Verb.LOAD, token.verb());
    assertEquals("slot2", token.target());
  }

  @Test
  void testParseQuit() {
    CommandToken token = parser.parse("quit");
    assertEquals(Verb.QUIT, token.verb());
    assertNull(token.target());
  }

  // Edge cases and normalization
  @Test
  void testParseWithExtraWhitespace() {
    CommandToken token = parser.parse("  pickup   steel   sword  ");
    assertEquals(Verb.PICKUP, token.verb());
    assertEquals("steel sword", token.target());
  }

  @Test
  void testParseWithMixedCase() {
    CommandToken token = parser.parse("ATTACK Ice TROLL");
    assertEquals(Verb.ATTACK, token.verb());
    assertEquals("ice troll", token.target());
  }

  @Test
  void testParseUnknownVerb() {
    CommandToken token = parser.parse("dance");
    assertEquals(Verb.UNKNOWN, token.verb());
  }

  @Test
  void testParsePreservesRawInput() {
    String input = "PICKUP  Steel Sword";
    CommandToken token = parser.parse(input);
    assertEquals(input, token.raw());
  }

  // All direction shortcuts
  @Test
  void testParseAllDirections() {
    assertEquals("north", parser.parse("n").target());
    assertEquals("south", parser.parse("s").target());
    assertEquals("east", parser.parse("e").target());
    assertEquals("west", parser.parse("w").target());
    assertEquals("north", parser.parse("north").target());
    assertEquals("south", parser.parse("south").target());
    assertEquals("east", parser.parse("east").target());
    assertEquals("west", parser.parse("west").target());
  }
}
