package domain;

import domain.entities.creatures.Hero;

import java.util.List;

public interface GameRepository {
    void save(Hero hero, String name, int levelNum, Level level, String notification);
    List<Object> load();
}
