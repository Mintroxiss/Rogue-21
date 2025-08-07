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

        while (gameSession.isNotGameOver()) {
            if (gameSession.isFieldUpdating()) {
                switch (gameSession.getGameSessionMode()) {
                    case ENTER_THE_NAME -> {
                    }
                    case GAME_FIELD -> {
                        addGameFieldInScreen(graphics, gameSession);
                        addGameInfoInScreen(graphics, gameSession);
                    }
                    case INVENTORY -> {
                        addInventoryFieldInScreen(graphics, gameSession);
                        addGameInfoInScreen(graphics, gameSession);
                    }
                    case SCORES -> {
                    }
                }
                addNotificationInScreen(graphics, gameSession);
                screen.refresh();
            }
            gameSession.gameTick(getKey(screen));
        }

        screen.stopScreen();
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
        if (notification != null) {
            clearNotificationInScreen(graphics, offset, gameSession, row);
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            for (int i = offset; i < gameSession.COLUMNS && i - offset < notification.length(); i++) {
                graphics.setCharacter(i, row, notification.charAt(i - offset));
            }
        } else {
            clearNotificationInScreen(graphics, offset, gameSession, row);
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
        String keyStr;
        KeyStroke key;
        try {
            key = screen.readInput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (key.getCharacter() != null) {
            keyStr = key.getCharacter().toString();
        } else {
            KeyType type = key.getKeyType();
            keyStr = switch (type) {
                case ArrowUp -> null;
                case ArrowDown -> null;
                case ArrowLeft -> null;
                case ArrowRight -> null;
                case Enter -> null;
                case Escape -> "esc";
                default -> null;
            };
        }
        return keyStr;
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

    private static void addGameFieldInScreen(TextGraphics graphics, GameSession gameSession) {
        TileType[][] gameField = gameSession.getGameField();
        for (int i = 0; i < gameSession.ROWS; i++) {
            for (int j = 0; j < gameSession.COLUMNS; j++) {
                TextColor.ANSI color = switch (gameField[i][j]) {
                    case HERO -> TextColor.ANSI.YELLOW;
                    case OGRE, GHOST, ZOMBIE, VAMPIRE, SNAKE_MAGE -> TextColor.ANSI.RED;
                    case DOOR -> TextColor.ANSI.GREEN;
                    default -> TextColor.ANSI.WHITE;
                };
                graphics.setForegroundColor(color);
                char symbol = switch (gameField[i][j]) {
                    case CORRIDOR -> '░';
                    case ITEM -> '(';
                    case DOOR -> '▋';
                    case WALL -> '█';
                    case FLOOR, DOORWAY -> '.'; //▪
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
