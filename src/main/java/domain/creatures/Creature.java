package domain.creatures;

import domain.positions.MovablePosition;

public abstract class Creature {
    private Integer health;
    private Integer agility;
    private Integer strength;
    private final MovablePosition pos;

    public Creature(Integer health, Integer agility, Integer strength, MovablePosition pos) {
        this.health = health;
        this.agility = agility;
        this.strength = strength;
        this.pos = pos;
    }

    public MovablePosition getPos() {
        return pos;
    }

    public boolean isLiving() {
        return health > 0;
    }

    public Integer getAgility() {
        return agility;
    }

    public void setAgility(Integer agility) {
        this.agility = agility;
    }

    public Integer getStrength() {
        return strength;
    }

    public void setStrength(Integer strength) {
        this.strength = strength;
    }

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }
}
