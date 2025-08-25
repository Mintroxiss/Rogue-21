package presentation;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import domain.GameSession;
import domain.cells.TileType;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        GameSession gameSession = new GameSession();

        Screen screen = new DefaultTerminalFactory()
                .setInitialTerminalSize(new TerminalSize(gameSession.COLUMNS, gameSession.ROWS + 6))
                .createScreen();

        screen.startScreen();
        screen.setCursorPosition(null);
        TextGraphics graphics = screen.newTextGraphics();
        graphics.setBackgroundColor(TextColor.ANSI.BLACK);

        while (gameSession.isTurnedOn()) {
            screen.clear();
            if (gameSession.isFieldUpdating()) {
                switch (gameSession.getGameSessionMode()) {
                    case ENTER_THE_NAME -> addEnterTheNameFieldInScreen(graphics, gameSession);
                    case GAME_FIELD -> {
                        addGameFieldInScreen(graphics, gameSession);
                        addGameInfoInScreen(graphics, gameSession);
                    }
                    case INVENTORY -> {
                        addInventoryFieldInScreen(graphics, gameSession);
                        addGameInfoInScreen(graphics, gameSession);
                    }
                    case SCORES -> addScoreFieldInScreen(graphics, gameSession);
                }
                addNotificationInScreen(graphics, gameSession);
                screen.refresh();
            }
            gameSession.gameTick(getKey(screen));
        }
        screen.stopScreen();
    }

    private static void addEnterTheNameFieldInScreen(TextGraphics graphics, GameSession gameSession) {
        char[][] inventoryField = gameSession.getEnterTheNameField();
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        for (int i = 0; i < gameSession.ROWS; i++) {
            for (int j = 0; j < gameSession.COLUMNS; j++) {
                graphics.setCharacter(j, i + 3, inventoryField[i][j]);
            }
        }
    }

    private static void addGameInfoInScreen(TextGraphics graphics, GameSession gameSession) {
        String[] gameInfo = gameSession.getGameInfo();
        for (int i = 0; i < gameSession.COLUMNS; i++) {
            graphics.setCharacter(i, gameSession.ROWS + 4, ' ');
        }
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        int index = 0;
        for (String s : gameInfo) {
            for (int j = 0; j < s.length(); j++) {
                graphics.setCharacter(index, gameSession.ROWS + 4, s.charAt(j));
                index++;
            }
            index += 3;
        }
    }

    private static void addNotificationInScreen(TextGraphics graphics, GameSession gameSession) {
        String notification = gameSession.getNotification();
        int offset = 1;
        int row = 1;
        clearNotificationInScreen(graphics, offset, gameSession, row);
        if (notification != null) {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            for (int i = offset; i < gameSession.COLUMNS && i - offset < notification.length(); i++) {
                graphics.setCharacter(i, row, notification.charAt(i - offset));
            }
        }
    }

    private static void clearNotificationInScreen(
            TextGraphics graphics,
            int offset,
            GameSession gameSession,
            int row
    ) {
        if (!graphics.getCharacter(offset, 1).is(' ')) {
            for (int i = 0; i < gameSession.COLUMNS; i++) {
                graphics.setCharacter(i, row, ' ');
            }
        }
    }

    private static String getKey(Screen screen) {
        KeyStroke key;
        try {
            key = screen.readInput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (key == null) {
            return null;
        }

        if (key.getCharacter() != null) {
            char c = key.getCharacter();

            if (c == 8) return "backspace";

            return String.valueOf(c);
        }

        KeyType type = key.getKeyType();
        return switch (type) {
            case ArrowUp -> "arrowUp";
            case ArrowDown -> "arrowDown";
            case ArrowLeft -> "arrowLeft";
            case ArrowRight -> "arrowRight";
            case Enter -> "enter";
            case Escape -> "esc";
            default -> null;
        };
    }


    private static void addInventoryFieldInScreen(TextGraphics graphics, GameSession gameSession) {
        char[][] inventoryField = gameSession.getInventoryField();
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        for (int i = 0; i < gameSession.ROWS; i++) {
            for (int j = 0; j < gameSession.COLUMNS; j++) {
                graphics.setCharacter(j, i + 3, inventoryField[i][j]);
            }
        }
    }

    private static void addScoreFieldInScreen(TextGraphics graphics, GameSession gameSession) {
        char[][] inventoryField = gameSession.getScoreField();
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        for (int i = 0; i < gameSession.ROWS; i++) {
            for (int j = 0; j < gameSession.COLUMNS; j++) {
                graphics.setCharacter(j, i + 3, inventoryField[i][j]);
            }
        }
    }

    private static void addGameFieldInScreen(TextGraphics graphics, GameSession gameSession) {
        TileType[][] gameField = gameSession.getGameField();
        for (int i = 0; i < gameSession.ROWS; i++) {
            for (int j = 0; j < gameSession.COLUMNS; j++) {
                TextColor.ANSI color = switch (gameField[i][j]) {
                    case HERO -> TextColor.ANSI.CYAN;
                    case OGRE -> TextColor.ANSI.YELLOW;
                    case ZOMBIE -> TextColor.ANSI.GREEN;
                    case VAMPIRE -> TextColor.ANSI.RED;
                    case DOOR -> TextColor.ANSI.GREEN_BRIGHT;
                    case WALL, FLOOR, CORRIDOR -> TextColor.ANSI.BLACK_BRIGHT;
                    default -> TextColor.ANSI.WHITE;
                };
                graphics.setForegroundColor(color);
                char symbol = switch (gameField[i][j]) {
                    case CORRIDOR -> '░';
                    case ITEM -> '(';
                    case DOOR -> '▋';
                    case WALL -> '█';
                    case FLOOR -> '.';
                    case HERO -> '@';
                    case OGRE -> 'O';
                    case GHOST -> 'G';
                    case ZOMBIE -> 'Z';
                    case VAMPIRE -> 'V';
                    case SNAKE_MAGE -> 'S';
                    default -> ' ';
                };
                graphics.setCharacter(j, i + 3, symbol);
            }
        }
    }
}
