package domain;

import domain.cells.TileType;
import domain.creatures.Hero;
import domain.items.ItemType;

public class GameSession {
    public final int ROWS = 22;
    public final int COLUMNS = 80;

    private Integer levelNum = 1;
    private boolean fieldUpdating = false;

    private GameSessionMode gameSessionMode = GameSessionMode.GAME_FIELD;

    private final Hero hero = new Hero();
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

    /**
     * Обрабатывает следующий тик игровой машины
     *
     * @param command команда
     */
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

    /**
     * Обрабатывает тик игрового поля
     *
     * @param command команда
     */
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
                gameSessionMode.setInventoryMode(InventoryMode.SHOW_ITEMS);
                yield true;
            }
            case "t" -> {
                if (level.cellWithHeroHasItem(hero.getPos())) {
                    notification = "Floor is occupied";
                } else {
                    hero.createInventoryField(null, ROWS, COLUMNS);
                    gameSessionMode = GameSessionMode.INVENTORY;
                    gameSessionMode.setInventoryMode(InventoryMode.THROW_AWAY_ITEM);
                }
                yield true;
            }
            case "j" -> {
                if (hero.getHealth() < hero.getTotalMaxHealth()) {
                    hero.createInventoryField(ItemType.FOOD, ROWS, COLUMNS);
                    gameSessionMode = GameSessionMode.INVENTORY;
                    gameSessionMode.setInventoryMode(InventoryMode.USE_FOOD);
                } else {
                    notification = "Max HP";
                }
                yield true;
            }
            case "e" -> {
                hero.createInventoryField(ItemType.SCROLL, ROWS, COLUMNS);
                gameSessionMode = GameSessionMode.INVENTORY;
                gameSessionMode.setInventoryMode(InventoryMode.USE_SCROLL);
                yield true;
            }
            case "h" -> {
                if (level.swapEquippedWeaponForLyingWeapon(hero)) {
                    notification = level.getNotification();
                } else {
                    hero.createInventoryField(ItemType.WEAPON, ROWS, COLUMNS);
                    gameSessionMode = GameSessionMode.INVENTORY;
                    gameSessionMode.setInventoryMode(InventoryMode.EQUIP_WEAPON);
                }
                yield true;
            }
            case "y" -> {
                if (level.swapEquippedArmorForLyingArmor(hero)) {
                    notification = level.getNotification();
                } else {
                    hero.createInventoryField(ItemType.ARMOR, ROWS, COLUMNS);
                    gameSessionMode = GameSessionMode.INVENTORY;
                    gameSessionMode.setInventoryMode(InventoryMode.EQUIP_ARMOR);
                }
                yield true;
            }
            case "k" -> {
                hero.createInventoryField(ItemType.POTION, ROWS, COLUMNS);
                gameSessionMode = GameSessionMode.INVENTORY;
                gameSessionMode.setInventoryMode(InventoryMode.USE_POTION);
                yield true;
            }
            case "enter", "\n" -> {
                boolean res = false;
                if (level.cellWithHeroHasDoor(hero)) {
                    nextLevel();
                    res = true;
                }
                yield res;
            }

            default -> false;
        };
        if (gameSessionMode == GameSessionMode.INVENTORY && gameSessionMode.getInventoryMode() != null) {
            notification = gameSessionMode.getInventoryMode().getAction();
        }
    }

    /**
     * Обрабатывает тик инвентаря
     *
     * @param command команда
     */
    private void executeInventoryCommand(String command) {
        if (gameSessionMode.getInventoryMode() != null) {
            fieldUpdating = true;
            switch (gameSessionMode.getInventoryMode()) {
                case USE_FOOD -> executeUseFoodCommand(command);
                case USE_POTION -> executeUsePotionCommand(command);
                case USE_SCROLL -> executeUseScrollCommand(command);
                case SHOW_ITEMS -> gameSessionMode = GameSessionMode.GAME_FIELD;
                case THROW_AWAY_ITEM -> executeThrowAwayItemCommand(command);
                case EQUIP_WEAPON -> executeEquipWeapon(command);
                case EQUIP_ARMOR -> executeEquipArmorCommand(command);
            }
            if (gameSessionMode == GameSessionMode.INVENTORY) {
                notification = gameSessionMode.getInventoryMode().getAction();
            }
        }
    }

    private void executeEquipArmorCommand(String command) {
        switch (command) {
            case "y", "-" -> gameSessionMode = GameSessionMode.GAME_FIELD;
            default -> {
                try {
                    int num = Integer.parseInt(command);
                    if (hero.equipArmorFromInventory(num)) {
                        gameSessionMode = GameSessionMode.GAME_FIELD;
                    }
                } catch (Exception _) {
                }
            }
        }
    }

    private void executeEquipWeapon(String command) {
        switch (command) {
            case "h", "=" -> gameSessionMode = GameSessionMode.GAME_FIELD;
            default -> {
                try {
                    int num = Integer.parseInt(command);
                    if (hero.equipWeaponFromInventory(num)) {
                        gameSessionMode = GameSessionMode.GAME_FIELD;
                    }
                } catch (Exception _) {
                }
            }
        }
    }

    private void executeThrowAwayItemCommand(String command) {
        switch (command) {
            case "t" -> gameSessionMode = GameSessionMode.GAME_FIELD;
            case "-" -> {
                if (level.heroThrowAwayEquippedArmor(hero)) {
                    gameSessionMode = GameSessionMode.GAME_FIELD;
                }
            }
            case "=" -> {

                if (level.heroThrowAwayEquippedWeapon(hero)) {
                    gameSessionMode = GameSessionMode.GAME_FIELD;
                }
            }
            default -> {
                try {
                    int num = Integer.parseInt(command);
                    if (level.heroThrowAwayItem(hero, num)) {
                        gameSessionMode = GameSessionMode.GAME_FIELD;
                    }
                } catch (Exception _) {
                }
            }
        }
    }

    private void executeUseScrollCommand(String command) {
        if (command.equals("e")) {
            gameSessionMode = GameSessionMode.GAME_FIELD;
        } else {
            try {
                int num = Integer.parseInt(command);
                if (hero.useScroll(num)) {
                    gameSessionMode = GameSessionMode.GAME_FIELD;
                }
            } catch (Exception _) {
            }
        }
    }

    private void executeUsePotionCommand(String command) {
        if (command.equals("k")) {
            gameSessionMode = GameSessionMode.GAME_FIELD;
        } else {
            try {
                int num = Integer.parseInt(command);
                if (!hero.usePotion(num)) {
                    notification = "This effect has not ended";
                }
                gameSessionMode = GameSessionMode.GAME_FIELD;
            } catch (Exception _) {
            }
        }
    }

    private void executeUseFoodCommand(String command) {
        if (command.equals("j")) {
            gameSessionMode = GameSessionMode.GAME_FIELD;
        } else {
            try {
                int num = Integer.parseInt(command);
                if (hero.useFood(num)) {
                    gameSessionMode = GameSessionMode.GAME_FIELD;
                }
            } catch (Exception _) {
            }
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
                "Gold:" + hero.getGold(),
        };
    }
}
