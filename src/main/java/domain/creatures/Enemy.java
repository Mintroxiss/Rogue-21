package domain.creatures;

import domain.cells.TileType;
import domain.items.Item;
import domain.positions.MovablePosition;

public class Enemy extends Creature {
    private final EnemyType enemyType;
    private final Integer hostility;
    private Item rewardTreasure;

    public Enemy(
            EnemyType enemyType,
            TileType tileType,
            int hostility,
            int health,
            int agility,
            int strength,
            MovablePosition pos,
            Item rewardTreasure
    ) {
        super(health, agility, strength, pos, tileType);
        this.enemyType = enemyType;
        this.hostility = hostility;
        this.rewardTreasure = rewardTreasure;
    }

    public Enemy(EnemyType enemyType, TileType tileType, int hostility) {
        this(enemyType, tileType, hostility, 5, 5, 5, null, null);
    }

    public Enemy(Enemy enemy) {
        this(
                enemy.getEnemyType(),
                enemy.getTile().getTileType(),
                enemy.getHostility(),
                enemy.getHealth(),
                enemy.getAgility(),
                enemy.getStrength(),
                enemy.getPos(),
                enemy.getRewardTreasure()
        );
    }

    public EnemyType getEnemyType() {
        return enemyType;
    }

    public Integer getHostility() {
        return hostility;
    }

    public Item getRewardTreasure() {
        return rewardTreasure;
    }

    public void setRewardTreasure(Item rewardTreasure) {
        this.rewardTreasure = rewardTreasure;
    }
}
