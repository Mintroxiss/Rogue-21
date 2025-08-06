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
                .setInitialTerminalSize(new TerminalSize(gameSession.COLUMN_NUM, gameSession.ROW_NUM + 4))
                .createScreen();

        screen.startScreen();
        screen.setCursorPosition(null);
        TextGraphics graphics = screen.newTextGraphics();
        graphics.setBackgroundColor(TextColor.ANSI.BLACK);

        while (gameSession.isNotGameOver()) {
            if (gameSession.isFieldUpdating()) {
                switch (gameSession.getGameSessionMode()) {
                    case ENTER_THE_NAME -> {}
                    case GAME_FIELD -> addGameFieldInScreen(graphics, gameSession);
                    case INVENTORY -> addInventoryFieldInScreen(graphics, gameSession);
                    case SCORES -> {}
                }
                screen.refresh();
            }
            gameSession.gameTick(getKey(screen));
        }

        screen.stopScreen();
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
        for (int i = 0; i < gameSession.ROW_NUM; i++) {
            for (int j = 0; j < gameSession.COLUMN_NUM; j++) {
                graphics.setCharacter(j, i + 2, inventoryField[i][j]);
            }
        }
    }

    private static void addGameFieldInScreen(TextGraphics graphics, GameSession gameSession) {
        TileType[][] gameField = gameSession.getGameField();
        for (int i = 0; i < gameSession.ROW_NUM; i++) {
            for (int j = 0; j < gameSession.COLUMN_NUM; j++) {
                TextColor.ANSI color = switch (gameField[i][j]) {
                    case HERO -> TextColor.ANSI.YELLOW;
                    case OGRE, GHOST, ZOMBIE, VAMPIRE, SNAKE_MAGE -> TextColor.ANSI.RED;
                    case DOOR -> TextColor.ANSI.GREEN;
                    default -> TextColor.ANSI.WHITE;
                };
                graphics.setForegroundColor(color);
                char symbol = switch(gameField[i][j]) {
                    case CORRIDOR -> '░';
                    case ITEM -> '(';
                    case DOOR -> '▋';
                    case WALL -> '█';
                    case FLOOR, DOORWAY -> '▪';
                    case HERO -> '@';
                    case OGRE -> 'O';
                    case GHOST -> 'G';
                    case ZOMBIE -> 'Z';
                    case VAMPIRE -> 'V';
                    case SNAKE_MAGE -> 'S';
                    default -> ' ';
                };
                graphics.setCharacter(j, i + 2, symbol);
            }
        }
    }
}
