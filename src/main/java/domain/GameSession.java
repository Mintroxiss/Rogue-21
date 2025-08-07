package domain;

import domain.cells.TileType;
import domain.creatures.Hero;
import domain.items.ItemType;
import domain.positions.MovablePosition;

public class GameSession {
    public final int ROWS = 22;
    public final int COLUMNS = 80;

    private Integer levelNum = 1;
    private boolean fieldUpdating = false;

    private GameSessionMode gameSessionMode = GameSessionMode.GAME_FIELD;
    private InventoryMode inventoryMode = InventoryMode.NOTHING;

    private final Hero hero = new Hero(new MovablePosition(COLUMNS / 2, ROWS / 2), null, null);
    private Level level = new Level(ROWS, COLUMNS, levelNum, hero);
    private String notification = null;

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

        String levelNotification = level.getNotification();
        if (levelNotification != null) {
            notification = levelNotification;
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
                            gameSessionMode = GameSessionMode.GAME_FIELD;
                            inventoryMode = InventoryMode.NOTHING;
                        }
                    } catch (Exception _) {
                    }
                }

                yield true;
            }
            case USE_POTION -> {
                switch (command) {
                    case "k" -> {
                        gameSessionMode = GameSessionMode.GAME_FIELD;
                        inventoryMode = InventoryMode.NOTHING;
                    }
                    default -> {
                        try {
                            int num = Integer.parseInt(command);
                            if (!hero.usePotion(num)) {
                                notification = "This effect has not ended";
                            }
                            gameSessionMode = GameSessionMode.GAME_FIELD;
                            inventoryMode = InventoryMode.NOTHING;
                        } catch (Exception _) {
                        }
                    }
                }
                yield true;
            }
            case USE_SCROLL -> {
                switch (command) {
                    case "e" -> {
                        gameSessionMode = GameSessionMode.GAME_FIELD;
                        inventoryMode = InventoryMode.NOTHING;
                    }
                    default -> {
                        try {
                            int num = Integer.parseInt(command);
                            if (hero.useScroll(num)) {
                                gameSessionMode = GameSessionMode.GAME_FIELD;
                                inventoryMode = InventoryMode.NOTHING;
                            }
                        } catch (Exception _) {
                        }
                    }
                }
                yield true;
            }
            case SHOW_ITEMS -> {
                gameSessionMode = GameSessionMode.GAME_FIELD;
                inventoryMode = InventoryMode.NOTHING;
                yield true;
            }
            case THROW_AWAY_ITEM -> {
                switch (command) {
                    case "t" -> {
                        gameSessionMode = GameSessionMode.GAME_FIELD;
                        inventoryMode = InventoryMode.NOTHING;
                    }
                    case "-" -> {
                        if (level.heroThrowAwayEquippedArmor(hero)) {
                            gameSessionMode = GameSessionMode.GAME_FIELD;
                            inventoryMode = InventoryMode.NOTHING;
                        }
                    }
                    case "=" -> {

                        if (level.heroThrowAwayEquippedWeapon(hero)) {
                            gameSessionMode = GameSessionMode.GAME_FIELD;
                            inventoryMode = InventoryMode.NOTHING;
                        }
                    }
                    default -> {
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
                }
                yield true;
            }
            case EQUIP_WEAPON -> {
                switch (command) {
                    case "h", "=" -> {
                        gameSessionMode = GameSessionMode.GAME_FIELD;
                        inventoryMode = InventoryMode.NOTHING;
                    }
                    default -> {
                        try {
                            int num = Integer.parseInt(command);
                            if (hero.equipWeaponFromInventory(num)) {
                                gameSessionMode = GameSessionMode.GAME_FIELD;
                                inventoryMode = InventoryMode.NOTHING;
                            }
                        } catch (Exception _) {
                        }
                    }
                }
                yield true;
            }
            case EQUIP_ARMOR -> {
                switch (command) {
                    case "y", "-" -> {
                        gameSessionMode = GameSessionMode.GAME_FIELD;
                        inventoryMode = InventoryMode.NOTHING;
                    }
                    default -> {
                        try {
                            int num = Integer.parseInt(command);
                            if (hero.equipArmorFromInventory(num)) {
                                gameSessionMode = GameSessionMode.GAME_FIELD;
                                inventoryMode = InventoryMode.NOTHING;
                            }
                        } catch (Exception _) {
                        }
                    }
                }
                yield true;
            }
            case NOTHING -> throw new IllegalArgumentException("Inventory mod not installed");
        };
        if (gameSessionMode == GameSessionMode.INVENTORY) {
            notification = inventoryMode.getAction();
        }
    }

    private void executeGameFieldCommand(String command) {
        if (command == null) return;

        fieldUpdating = switch (command) {
            case "w" -> level.heroMoveUp(hero);
            case "d" -> level.heroMoveRight(hero);
            case "s" -> level.heroMoveDown(hero);
            case "a" -> level.heroMoveLeft(hero);
            case "i" -> {
                hero.createInventoryField(null, ROWS, COLUMNS);
                gameSessionMode = GameSessionMode.INVENTORY;
                inventoryMode = InventoryMode.SHOW_ITEMS;
                yield true;
            }
            case "t" -> {
                if (level.cellWithHeroHasItem(hero.getPos())) {
                    notification = "Floor is occupied";
                } else {
                    hero.createInventoryField(null, ROWS, COLUMNS);
                    gameSessionMode = GameSessionMode.INVENTORY;
                    inventoryMode = InventoryMode.THROW_AWAY_ITEM;
                }
                yield true;
            }
            case "j" -> {
                if (hero.getHealth() < hero.getTotalMaxHealth()) {
                    hero.createInventoryField(ItemType.FOOD, ROWS, COLUMNS);
                    gameSessionMode = GameSessionMode.INVENTORY;
                    inventoryMode = InventoryMode.USE_FOOD;
                } else {
                    notification = "Max HP";
                }
                yield true;
            }
            case "e" -> {
                hero.createInventoryField(ItemType.SCROLL, ROWS, COLUMNS);
                gameSessionMode = GameSessionMode.INVENTORY;
                inventoryMode = InventoryMode.USE_SCROLL;
                yield true;
            }
            case "h" -> {
                if (level.swapEquippedWeaponForLyingWeapon(hero)) {
                    notification = level.getNotification();
                } else {
                    hero.createInventoryField(ItemType.WEAPON, ROWS, COLUMNS);
                    gameSessionMode = GameSessionMode.INVENTORY;
                    inventoryMode = InventoryMode.EQUIP_WEAPON;
                }
                yield true;
            }
            case "y" -> {
                if (level.swapEquippedArmorForLyingArmor(hero)) {
                    notification = level.getNotification();
                } else {
                    hero.createInventoryField(ItemType.ARMOR, ROWS, COLUMNS);
                    gameSessionMode = GameSessionMode.INVENTORY;
                    inventoryMode = InventoryMode.EQUIP_ARMOR;
                }
                yield true;
            }
            case "k" -> {
                hero.createInventoryField(ItemType.POTION, ROWS, COLUMNS);
                gameSessionMode = GameSessionMode.INVENTORY;
                inventoryMode = InventoryMode.USE_POTION;
                yield true;
            }

            default -> false;
        };
        if (gameSessionMode == GameSessionMode.INVENTORY) {
            notification = inventoryMode.getAction();
        }
    }

    public boolean isFieldUpdating() {
        return fieldUpdating;
    }

    private void nextLevel() {
        levelNum++;
        level = new Level(ROWS, COLUMNS, levelNum, hero);
    }

    public String getNotification() {
        String res = notification;
        notification = null;
        return res;
    }

    public String[] getGameInfo() {
        return new String[]{
                "Level:" + levelNum,
                "HP:" + hero.getHealth() + "(" + hero.getTotalMaxHealth() + ")",
                "Str:" + hero.getTotalStrength(),
                "Ag:" + hero.getTotalAgility(),
                "Gold:",
        };
    }
}
