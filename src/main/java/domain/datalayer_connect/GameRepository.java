package domain.datalayer_connect;

import domain.Level;
import domain.entities.creatures.Hero;

import java.util.ArrayList;

public interface GameRepository {
    void save(Hero hero, String name, int levelNum, Level level, String notification);
    LoadedGame load();
    void rmSave();

    void saveScoreList(ArrayList<LoadedScoreData> loadedScoreList);
    ArrayList<LoadedScoreData> loadScoreList();
}
