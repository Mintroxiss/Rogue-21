package domain.cells;

public class Tile {
    private boolean walkable;
    private TileType tileType;

    public Tile() {}

    public Tile(boolean walkable, TileType tileType) {
        this.walkable = walkable;
        this.tileType = tileType;
    }

    public Boolean isWalkable() {
        return walkable;
    }

    public TileType getTileType() {
        return tileType;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
    }
}

