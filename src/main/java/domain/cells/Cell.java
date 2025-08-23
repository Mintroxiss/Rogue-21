package domain.cells;

import domain.entities.creatures.Creature;
import domain.entities.items.Item;

public class Cell {
    private Tile base;
    private Item item = null;
    private Creature creature = null;
    private boolean cellIsVisible = false;

    public Cell() {}

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
    public TileType takeTopTileType() {
        TileType res;
        if (cellIsVisible) {
            if (creature != null) {
                res = creature.getTile().getTileType();
            } else if (item != null) {
                res = TileType.ITEM;
            } else {
                res = base.getTileType();
            }
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

    public void setBase(Tile base) {
        this.base = base;
    }
}
