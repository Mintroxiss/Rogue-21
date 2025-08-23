package domain.entities.creatures;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import domain.cells.Tile;
import domain.cells.TileType;
import domain.entities.EntityGenerator;
import domain.positions.MovablePosition;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)

@JsonSubTypes({
        @JsonSubTypes.Type(value = Hero.class, name = "hero"),
        @JsonSubTypes.Type(value = Enemy.class, name = "enemy"),
})

@JsonIdentityInfo(
        generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "@id"
)

public abstract class Creature {
    protected Integer health;
    protected Integer agility;
    protected Integer strength;
    protected MovablePosition pos = new MovablePosition();
    protected Tile tile;
    protected int numOfHitsReceived = 0;
    protected boolean stunState = false;

    public Creature() {
    }

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

    public boolean takeDied() {
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

    public void increaseNumOfHitsReceived() {
        numOfHitsReceived++;
    }

    public int getNumOfHitsReceived() {
        return numOfHitsReceived;
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
            sum += EntityGenerator.getRandomInt(1, sides);
        }
        return sum;
    }

    public boolean getStunState() {
        return stunState;
    }

    public void changeStunState() {
        stunState = !stunState;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public void setNumOfHitsReceived(int numOfHitsReceived) {
        this.numOfHitsReceived = numOfHitsReceived;
    }

    public boolean isStunState() {
        return stunState;
    }

    public void setStunState(boolean stunState) {
        this.stunState = stunState;
    }
}
