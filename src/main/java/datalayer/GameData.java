package datalayer;

import domain.Level;
import domain.entities.creatures.Hero;

public record GameData(int levelNum, String name, Hero hero, Level level, String notification) {

}
