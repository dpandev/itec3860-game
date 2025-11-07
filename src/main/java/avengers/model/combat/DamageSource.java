package avengers.model.combat;

import avengers.model.Element;

/**
 * Represents the source of damage in combat, including its elemental type and whether it is
 * critical.
 *
 * @param element The elemental type of the damage.
 * @param isCritical Whether the damage is critical.
 */
public record DamageSource(Element element, boolean isCritical) {}
