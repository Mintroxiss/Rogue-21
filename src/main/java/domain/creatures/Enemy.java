package domain.creatures;

import domain.cells.TileType;
import domain.positions.MovablePosition;

public class Enemy extends Creature {
    private final EnemyType enemyType;
    private Integer hostility;

    public Enemy(
            Integer health,
            Integer agility,
            Integer strength,
            MovablePosition pos,
            TileType tileType,
            EnemyType enemyType,
            Integer hostility
    ) {
        super(health, agility, strength, pos, tileType);
        this.enemyType = enemyType;
        this.hostility = hostility;
    }

    public EnemyType getEnemyType() {
        return enemyType;
    }

    public Integer getHostility() {
        return hostility;
    }

    public void setHostility(Integer hostility) {
        this.hostility = hostility;
    }
}
