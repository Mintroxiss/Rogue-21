package domain;

import datalayer.JsonGameRepository;
import domain.cells.TileType;
import domain.entities.creatures.Hero;
import domain.entities.items.ItemType;

import java.util.List;

public class GameSession {
    public final int ROWS = 32; // 22
    public final int COLUMNS = 100; // 80

    GameRepository repo = new JsonGameRepository();
    private final EnterTheName enterTheName;
    private Integer levelNum;
    private GameSessionMode gameSessionMode;
    private boolean fieldUpdating = true;
    private final Hero hero;
    private Level level;
    private String notification;

    public GameSession() {
        List<Object> loaded = repo.load();
        enterTheName = new EnterTheName();
        if (loaded != null) {
            levelNum = (int) loaded.get(0);
            enterTheName.setName((String) loaded.get(1));
            hero = (Hero) loaded.get(2);
            level = (Level) loaded.get(3);
            notification = (String) loaded.get(4);

            gameSessionMode = GameSessionMode.GAME_FIELD;
        } else {
            levelNum = 1;
            hero = new Hero();
            level = new Level(ROWS, COLUMNS, levelNum, hero);
            notification = null;
            gameSessionMode = GameSessionMode.ENTER_THE_NAME;
        }
    }

    public TileType[][] getGameField() {
        fieldUpdating = false;
        return level.takeTileTypeGameField();
    }

    public char[][] getInventoryField() {
        fieldUpdating = false;
        return hero.takeInventoryField();
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
            case ENTER_THE_NAME -> executeEnterTheNameCommand(command);
            case GAME_FIELD -> executeGameFieldCommand(command);
            case INVENTORY -> executeInventoryCommand(command);
//            case SCORES -> ;
        }

        String levelNotification = level.getNotification();
        if (levelNotification != null) {
            notification = levelNotification;
        }
    }

    public char[][] getEnterTheNameField() {
        return enterTheName.getStartField(ROWS, COLUMNS);
    }

    private void executeEnterTheNameCommand(String command) {
        switch (command) {
            case "\n" -> gameSessionMode = GameSessionMode.GAME_FIELD;
            case "backspace" -> enterTheName.removeLastLetter();
            default -> {
                if (command.length() == 1) {
                    enterTheName.addLetter(command.charAt(0));
                }
            }
        }
        fieldUpdating = true;
    }

    /**
     * Обрабатывает тик игрового поля
     *
     * @param command команда
     */
    private void executeGameFieldCommand(String command) {
        fieldUpdating = switch (command) {
            case "w" -> level.heroMoveUp();
            case "d" -> level.heroMoveRight();
            case "s" -> level.heroMoveDown();
            case "a" -> level.heroMoveLeft();
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
                if (hero.getHealth() < hero.takeTotalMaxHealth()) {
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
                if (level.swapEquippedWeaponForLyingWeapon()) {
                    notification = level.getNotification();
                } else {
                    hero.createInventoryField(ItemType.WEAPON, ROWS, COLUMNS);
                    gameSessionMode = GameSessionMode.INVENTORY;
                    gameSessionMode.setInventoryMode(InventoryMode.EQUIP_WEAPON);
                }
                yield true;
            }
            case "y" -> {
                if (level.swapEquippedArmorForLyingArmor()) {
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
            case "\n" -> {
                boolean res = false;
                if (level.cellWithHeroHasDoor()) {
                    nextLevel();
                    res = true;
                }
                yield res;
            }
            case "esc" -> {
                repo.save(hero, enterTheName.getName(), levelNum, level, notification);
                notification = "Saving was successful!";
                yield true;
            }
            default -> false;
        };
        if (gameSessionMode == GameSessionMode.INVENTORY && gameSessionMode.getInventoryMode() != null) {
            notification = gameSessionMode.getInventoryMode().getAction();
        }
        if (level.isGameOver()) {
            gameSessionMode = GameSessionMode.SCORES;
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
                if (level.heroThrowAwayEquippedArmor()) {
                    gameSessionMode = GameSessionMode.GAME_FIELD;
                }
            }
            case "=" -> {

                if (level.heroThrowAwayEquippedWeapon()) {
                    gameSessionMode = GameSessionMode.GAME_FIELD;
                }
            }
            default -> {
                try {
                    int num = Integer.parseInt(command);
                    if (level.heroThrowAwayItem(num)) {
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
        if (levelNum > 21) {
            gameSessionMode = GameSessionMode.SCORES;
        } else {
            level = new Level(ROWS, COLUMNS, levelNum, hero);
        }
    }

    public String getNotification() {
        String res = notification;
        notification = null;
        return res;
    }

    public String[] getGameInfo() {
        return new String[]{
                "Level:" + levelNum,
                "HP:" + hero.getHealth() + "(" + hero.takeTotalMaxHealth() + ")",
                "Str:" + hero.takeTotalStrength(),
                "Ag:" + hero.takeTotalAgility(),
                "Gold:" + hero.takeGold(),
        };
    }
}
