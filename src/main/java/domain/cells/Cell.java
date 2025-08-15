package domain.cells;

import domain.creatures.Creature;
import domain.items.Item;

public class Cell {
    private final Tile base;
    private Item item = null;
    private Creature creature = null;
    private boolean cellIsVisible = true;

    public Cell(Tile base) {
        this.base = base;
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
     *
     * @return Верхняя плитка в клетке
     */
    public TileType getTopTileType() {
        TileType res;
        if (creature != null) {
            res = creature.getTile().getTileType();
        } else if (item != null) {
            res = TileType.ITEM;
        } else if (cellIsVisible) {
            res = base.getTileType();
        } else {
            res = TileType.DARK;
        }
        return res;
    }

    public boolean isCellIsVisible() {
        return cellIsVisible;
    }

    public void setCellIsVisible(boolean cellIsVisible) {
        this.cellIsVisible = cellIsVisible;
    }
}
