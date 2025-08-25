package domain.datalayer_connect;

import domain.Level;
import domain.entities.creatures.Hero;

public record LoadedGame(int levelNum, String name, Hero hero, Level level, String notification) {
}
