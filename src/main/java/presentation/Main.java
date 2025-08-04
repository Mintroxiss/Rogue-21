package presentation;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import domain.GameSession;
import domain.TileType;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        Screen screen = new DefaultTerminalFactory()
                .setInitialTerminalSize(new TerminalSize(80, 24))
                .createScreen();

        screen.startScreen();
        screen.setCursorPosition(null);
        TextGraphics graphics = screen.newTextGraphics();
        graphics.setBackgroundColor(TextColor.ANSI.BLACK);

        GameSession gameSession = new GameSession();
        while (gameSession.isNotGameOver()) {
            gameSession.gameTick(getCharKey(screen));
            if (gameSession.isGameFieldUpdating()) {
                addGameFieldInScreen(graphics, gameSession.getGameField());
                screen.refresh();
            }
        }

        screen.stopScreen();
    }

    private static Character getCharKey(Screen screen) {
        Character charKey = null;
        KeyStroke key;
        try {
            key = screen.readInput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (key.getCharacter() != null) {
            charKey = key.getCharacter();
        } /*else { TODO
            KeyType type = key.getKeyType();
            charKey = switch (type) {
                case ArrowUp -> null;
                case ArrowDown -> null;
                case ArrowLeft -> null;
                case ArrowRight -> null;
                case Enter -> null;
                case Escape -> null;
                default -> null;
            };
        }*/
        return charKey;
    }

    private static void addGameFieldInScreen(TextGraphics graphics, TileType[][] gameField) {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 80; j++) {
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
