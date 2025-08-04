package domain;

import domain.creatures.Creature;
import domain.items.Item;

public class Cell {
    private final Tile base;
    private Item item = null;
    private Creature creature = null;

    public Cell(Tile base) {
        this.base = base;
    }


    public Cell(Tile base, Creature creature) {
        this(base);
        this.creature = creature;
    }

    public Cell(Tile base, Item item) {
        this(base);
        this.item = item;
    }

    public Cell(Tile base, Item item, Creature creature) {
        this(base, creature);
        this.item = item;
    }

    public Creature getCreature() {
        return creature;
    }

    public void setCreature(Creature creature) {
        this.creature = creature;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Tile getBase() {
        return base;
    }


    /**
     * Возвращает плитку, находящуюся на вершине клетки
     * @return Верхняя плитка в клетке
     */
    public TileType getTopTileType() {
        if (creature != null) {
            return creature.getTile().getTileType();
        }
        if (item != null) {
            return TileType.ITEM;
        }
        return base.getTileType();
    }
}
