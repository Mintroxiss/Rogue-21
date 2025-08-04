package domain;

public class Tile {
    private final boolean walkable;
    private final TileType tileType;

    public Tile(boolean walkable, TileType tileType) {
        this.walkable = walkable;
        this.tileType = tileType;
    }

    public Boolean getWalkable() {
        return walkable;
    }

    public TileType getTileType() {
        return tileType;
    }
}

