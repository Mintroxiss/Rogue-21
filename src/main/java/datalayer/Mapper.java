package datalayer;

import domain.Level;
import domain.entities.creatures.Hero;

import java.util.Arrays;
import java.util.List;

public class Mapper {
    public static GameData toDatalayer(Hero hero, String name, int levelNum, Level level, String notification) {
        GameData gameData = new GameData();
        gameData.hero = hero;
        gameData.name = name;
        gameData.levelNum = levelNum;
        gameData.level = level;
        gameData.notification = notification;

        return gameData;
    }

    public static List<Object> toDomain(GameData gameData) {
        return Arrays.asList(
                gameData.levelNum,
                gameData.name,
                gameData.hero,
                gameData.level,
                gameData.notification
        );
    }
}
