package domain.creatures;

import domain.Tile;
import domain.TileType;
import domain.positions.MovablePosition;

public abstract class Creature {
    private Integer health;
    private Integer agility;
    private Integer strength;
    private final MovablePosition pos;
    private final Tile tile;

    public Creature(Integer health, Integer agility, Integer strength, MovablePosition pos, TileType tileType) {
        this.health = health;
        this.agility = agility;
        this.strength = strength;
        this.pos = pos;
        this.tile = new Tile(false, tileType);
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

    public Tile getTile() {
        return tile;
    }
}
