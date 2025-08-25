package datalayer;

import domain.Level;
import domain.datalayer_connect.LoadedGame;
import domain.entities.creatures.Hero;

public class Mapper {
    public static GameData toDatalayer(Hero hero, String name, int levelNum, Level level, String notification) {
        return new GameData(levelNum, name, hero, level, notification);
    }

    public static LoadedGame toDomain(GameData gameData) {
        return new LoadedGame(
                gameData.levelNum(),
                gameData.name(),
                gameData.hero(),
                gameData.level(),
                gameData.notification()
        );
    }

    public static ScoreData getNewScore(
            String name,
            int gold,
            int level,
            int steps,
            int kills,
            int foodEaten,
            int potionsDrunk,
            int scrollsRead,
            int hits,
            int misses
    ) {
        return new ScoreData(name, gold, level, steps, kills, foodEaten, potionsDrunk, scrollsRead, hits, misses);
    }
}
