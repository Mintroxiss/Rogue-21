package domain.creatures;

import domain.GameGenerator;
import domain.cells.Tile;
import domain.cells.TileType;
import domain.positions.MovablePosition;

public abstract class Creature {
    protected Integer health;
    protected Integer agility;
    protected Integer strength;
    protected MovablePosition pos;
    protected final Tile tile;

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

    public void setPos(MovablePosition pos) {
        this.pos = pos;
    }

    public boolean isDied() {
        return health <= 0;
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

    /**
     * Рассчитывает урон по логике броска кубика
     *
     * @param count количество бросков
     * @param sides количество сторон
     * @return урон
     */
    protected int rollDice(int count, int sides) {
        int sum = 0;
        for (int i = 0; i < count; i++) {
            sum += GameGenerator.getRandomInt(1, sides);
        }
        return sum;
    }
}
