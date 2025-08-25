package domain;

import datalayer.JsonGameRepository;
import domain.cells.TileType;
import domain.datalayer_connect.GameRepository;
import domain.datalayer_connect.LoadedGame;
import domain.datalayer_connect.LoadedScoreData;
import domain.entities.creatures.Hero;
import domain.entities.items.ItemType;

import java.util.ArrayList;

public class GameSession {
    public final int ROWS = 32; // 22
    public final int COLUMNS = 100; // 80

    private final GameRepository repo = new JsonGameRepository();
    private final LoadedGame loadedGame = repo.load();
    private final ArrayList<LoadedScoreData> loadedScoreList = repo.loadScoreList();

    private final EnterTheName enterTheName;
    private Integer levelNum;
    private GameSessionMode gameSessionMode;
    private boolean fieldUpdating = true;
    private Hero hero;
    private Level level;
    private String notification;
    private boolean gameFieldInit = false;
    private int scoreNum = 1;
    private boolean turnedOn = true;

    public GameSession() {
        enterTheName = new EnterTheName();
        if (loadedGame != null) {
            notification = "Do you want to continue the last game?";
            gameSessionMode = GameSessionMode.CONTINUE;
        } else {
            initVals();
            gameSessionMode = GameSessionMode.ENTER_THE_NAME;
        }
    }

    private void initVals() {
        levelNum = 1;
        hero = new Hero();
        level = new Level(ROWS, COLUMNS, levelNum, hero);
        notification = null;
        gameSessionMode = GameSessionMode.ENTER_THE_NAME;

        gameFieldInit = true;
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

    /**
     * Обрабатывает следующий тик игровой машины
     *
     * @param command команда
     */
    public void gameTick(String command) {
        switch (gameSessionMode) {
            case CONTINUE -> executeContinueCommand(command);
            case ENTER_THE_NAME -> executeEnterTheNameCommand(command);
            case GAME_FIELD -> executeGameFieldCommand(command);
            case INVENTORY -> executeInventoryCommand(command);
            case SCORES -> executeScoresCommand(command);
        }

        if (gameFieldInit) {
            String levelNotification = level.getNotification();
            if (levelNotification != null) {
                notification = levelNotification;
            }
        }
    }

    public char[][] getScoreField() {
        notification = "Game Over for " + enterTheName.getName();
        char[][] scoreField = new char[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                scoreField[i][j] = ' ';
            }
        }
        int row = 3 + addScoreInScoreField(loadedScoreList.get(scoreNum - 1), scoreField, 0, scoreNum);
        if (scoreNum < loadedScoreList.size()) {
            row = 3 + addScoreInScoreField(loadedScoreList.get(scoreNum), scoreField, row, scoreNum + 1);
        }
        if (scoreNum + 1 < loadedScoreList.size()) {
            addScoreInScoreField(loadedScoreList.get(scoreNum + 1), scoreField, row, scoreNum + 2);
        }

        return scoreField;
    }

    private int addScoreInScoreField(LoadedScoreData scoreData, char[][] field, int row, int num) {
        String str = num + ") " + scoreData.name();
        String firstSpacer = " ".repeat(enterTheName.MAX_LENGTH - scoreData.name().length() + 2);
        str += firstSpacer;
        String spacer = "\n" + " ".repeat(str.length());
        str += "Gold: " + scoreData.gold();
        str += spacer + "Level: " + scoreData.level() +
                spacer + "Kills: " + scoreData.kills() +
                spacer + "Hits: " + scoreData.hits() +
                spacer + "Misses: " + scoreData.misses() +
                spacer + "Steps: " + scoreData.steps() +
                spacer + "Food eaten: " + scoreData.foodEaten() +
                spacer + "Scrolls read: " + scoreData.scrollsRead() +
                spacer + "Potions drunk: " + scoreData.potionsDrunk();

        int column = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (column >= COLUMNS) {
                row++;
                column = 0;
            }
            if (c == '\n') {
                row++;
                column = 0;
            } else {
                field[row][column] = c;
                column++;
            }
        }

        return row;
    }


    private void executeScoresCommand(String command) {
        switch (command) {
            case "arrowUp" -> {
                if (scoreNum - 1 > 0) {
                    scoreNum--;
                }
            }
            case "arrowDown" -> {
                if (scoreNum + 1 < loadedScoreList.size()) {
                    scoreNum++;
                }
            }
            case "\n", "esc" -> turnedOn = false;
        }
    }

    private void executeContinueCommand(String command) {
        switch (command) {
            case "\n" -> {
                levelNum = loadedGame.levelNum();
                enterTheName.setName(loadedGame.name());
                hero = loadedGame.hero();
                level = loadedGame.level();
                notification = loadedGame.notification();

                gameFieldInit = true;
                gameSessionMode = GameSessionMode.GAME_FIELD;
            }
            case "backspace", "esc" -> {
                repo.rmSave();
                initVals();
                gameSessionMode = GameSessionMode.ENTER_THE_NAME;
            }
            default -> notification = "Do you want to continue the last game?";
        }
    }

    public char[][] getEnterTheNameField() {
        return enterTheName.getStartField(ROWS, COLUMNS);
    }

    private void executeEnterTheNameCommand(String command) {
        switch (command) {
            case "\n" -> {
                if (!enterTheName.getName().isEmpty()) {
                    gameSessionMode = GameSessionMode.GAME_FIELD;
                }
            }
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
            case "w", "arrowUp" -> level.heroMoveUp();
            case "d", "arrowRight" -> level.heroMoveRight();
            case "s", "arrowDown" -> level.heroMoveDown();
            case "a", "arrowLeft" -> level.heroMoveLeft();
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
            handleGameOverLogic();

        }
    }

    /**
     * Обрабатывает переход к этапу показа рекордов
     */
    private void handleGameOverLogic() {
        repo.rmSave();
        gameSessionMode = GameSessionMode.SCORES;
        loadedScoreList.add(new LoadedScoreData(
                enterTheName.getName(),
                hero.takeGold(),
                levelNum,
                hero.getSteps(),
                hero.getKills(),
                hero.getFoodEaten(),
                hero.getPotionsDrunk(),
                hero.getScrollsRead(),
                hero.getHits(),
                hero.getMisses())
        );
        repo.saveScoreList(loadedScoreList);
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
                    hero.setScrollsRead(hero.getScrollsRead() + 1);
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
                hero.setPotionsDrunk(hero.getPotionsDrunk() + 1);
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
                    hero.setFoodEaten(hero.getFoodEaten() + 1);
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
            handleGameOverLogic();
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

    public boolean isTurnedOn() {
        return turnedOn;
    }
}
