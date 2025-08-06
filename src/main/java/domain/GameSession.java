package domain;

import domain.cells.TileType;
import domain.creatures.Hero;
import domain.items.ItemType;
import domain.positions.MovablePosition;

public class GameSession {
    public final int ROW_NUM = 20;
    public final int COLUMN_NUM = 80;

    private Integer levelNum = 1;
    private boolean fieldUpdating = false;

    private GameSessionMode gameSessionMode = GameSessionMode.GAME_FIELD;
    private InventoryMode inventoryMode = InventoryMode.NOTHING;

    private final Hero hero = new Hero(new MovablePosition(COLUMN_NUM / 2, ROW_NUM / 2), null);
    private Level level = new Level(ROW_NUM, COLUMN_NUM, levelNum, hero);

    public TileType[][] getGameField() {
        fieldUpdating = false;
        return level.getTileTypeGameField();
    }

    public char[][] getInventoryField() {
        fieldUpdating = false;
        return hero.getInventoryField();
    }

    public GameSessionMode getGameSessionMode() {
        return gameSessionMode;
    }

    public Boolean isNotGameOver() {
        return true; // TODO
    }

    public void gameTick(String command) {
        switch (gameSessionMode) {
//            case ENTER_THE_NAME -> ;
            case GAME_FIELD -> executeGameFieldCommand(command);
            case INVENTORY -> executeInventoryCommand(command);
//            case SCORES -> ;
        }
    }

    private void executeInventoryCommand(String command) { // TODO
        fieldUpdating = switch (inventoryMode) {
            case USE_FOOD -> {
                if (command.equals("j")) {
                    gameSessionMode = GameSessionMode.GAME_FIELD;
                    inventoryMode = InventoryMode.NOTHING;
                } else {
                    try {
                        int num = Integer.parseInt(command);
                        if (hero.useFood(num)) {
                            // TODO вывод сообщения о неудачи
                            gameSessionMode = GameSessionMode.GAME_FIELD;
                            inventoryMode = InventoryMode.NOTHING;
                        }
                    } catch (Exception _) {
                    }
                }

                yield true;
            }
            case USE_POTION -> {
                yield true;
            }
            case USE_SCROLL -> {
                if (command.equals("e")) {
                    gameSessionMode = GameSessionMode.GAME_FIELD;
                    inventoryMode = InventoryMode.NOTHING;
                } else {
                    try {
                        int num = Integer.parseInt(command);
                        if (hero.useScroll(num)) {
                            // TODO вывод сообщения о неудачи
                            gameSessionMode = GameSessionMode.GAME_FIELD;
                            inventoryMode = InventoryMode.NOTHING;
                        }
                    } catch (Exception _) {
                    }
                }
                yield true;
            }
            case PUT_ITEM -> {
                yield true;
            }
            case SHOW_ITEMS -> {
                gameSessionMode = GameSessionMode.GAME_FIELD;
                inventoryMode = InventoryMode.NOTHING;
                yield true;
            }
            case THROW_AWAY_ITEM -> {
                if (command.equals("t")) {
                    gameSessionMode = GameSessionMode.GAME_FIELD;
                    inventoryMode = InventoryMode.NOTHING;
                } else {
                    try {
                        int num = Integer.parseInt(command);
                        if (level.heroThrowAwayItem(hero, num)) {
                            // TODO вывод сообщения о неудачи
                            gameSessionMode = GameSessionMode.GAME_FIELD;
                            inventoryMode = InventoryMode.NOTHING;
                        }
                    } catch (Exception _) {
                    }
                }
                yield true;
            }
            case EQUIP_WEAPON -> {
                yield true;
            }
            case NOTHING -> throw new IllegalArgumentException("Inventory mod not installed");
        };
    }

    private void executeGameFieldCommand(String command) {
        fieldUpdating = switch (command) {
            case "w" -> level.heroMoveUp(hero);
            case "d" -> level.heroMoveRight(hero);
            case "s" -> level.heroMoveDown(hero);
            case "a" -> level.heroMoveLeft(hero);
            case "i" -> {
                hero.createInventoryField(null, ROW_NUM, COLUMN_NUM);
                gameSessionMode = GameSessionMode.INVENTORY;
                inventoryMode = InventoryMode.SHOW_ITEMS;
                yield true;
            }
            case "t" -> {
                hero.createInventoryField(null, ROW_NUM, COLUMN_NUM);
                gameSessionMode = GameSessionMode.INVENTORY;
                inventoryMode = InventoryMode.THROW_AWAY_ITEM;
                yield true;
            }
            case "j" -> {
                boolean res = false;
                if (hero.getHealth() < hero.getMaxHealth()) {
                    hero.createInventoryField(ItemType.FOOD, ROW_NUM, COLUMN_NUM);
                    gameSessionMode = GameSessionMode.INVENTORY;
                    inventoryMode = InventoryMode.USE_FOOD;
                    res = true;
                }
                yield res;
            }
            case "e" -> {
                hero.createInventoryField(ItemType.SCROLL, ROW_NUM, COLUMN_NUM);
                gameSessionMode = GameSessionMode.INVENTORY;
                inventoryMode = InventoryMode.USE_SCROLL;
                yield true;
            }

            default -> false;
        };
    }

    public boolean isFieldUpdating() {
        return fieldUpdating;
    }

    private void nextLevel() {
        levelNum++;
        level = new Level(ROW_NUM, COLUMN_NUM, levelNum, hero);
    }
}
