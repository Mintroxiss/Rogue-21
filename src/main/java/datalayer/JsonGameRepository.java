package datalayer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Level;
import domain.datalayer_connect.GameRepository;
import domain.datalayer_connect.LoadedGame;
import domain.datalayer_connect.LoadedScoreData;
import domain.entities.creatures.Hero;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class JsonGameRepository implements GameRepository {
    private static final String SAVE_FILE = "save.json";
    private static final String SCORES_FILE = "score.json";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void save(Hero hero, String name, int levelNum, Level level, String notification) {
        try {
            GameData dto = Mapper.toDatalayer(hero, name, levelNum, level, notification);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(SAVE_FILE), dto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LoadedGame load() {
        try {
            GameData dto = mapper.readValue(new File(SAVE_FILE), GameData.class);
            return Mapper.toDomain(dto);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void rmSave() {
        File save = new File(SAVE_FILE);
        if (save.exists()) {
            save.delete();
        }
    }

    @Override
    public void saveScoreList(ArrayList<LoadedScoreData> loadedScoreList) {
        ArrayList<ScoreData> scoreList = new ArrayList<>(loadedScoreList.stream()
                .map(e -> new ScoreData(
                        e.name(),
                        e.gold(),
                        e.level(),
                        e.steps(),
                        e.kills(),
                        e.foodEaten(),
                        e.potionsDrunk(),
                        e.scrollsRead(),
                        e.hits(),
                        e.misses())
                )
                .toList()
        );
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(SCORES_FILE), scoreList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<LoadedScoreData> loadScoreList() {
        File file = new File(SCORES_FILE);
        try {
            ArrayList<ScoreData> scoreList = mapper.readValue(file, new TypeReference<ArrayList<ScoreData>>() {
            });
            return new ArrayList<>(scoreList.stream()
                    .map(e -> new LoadedScoreData(
                            e.getName(),
                            e.getGold(),
                            e.getLevel(),
                            e.getSteps(),
                            e.getKills(),
                            e.getFoodEaten(),
                            e.getPotionsDrunk(),
                            e.getScrollsRead(),
                            e.getHits(),
                            e.getMisses()))
                    .toList()
            );
        } catch (IOException _) {
            return new ArrayList<>();
        }
    }
}
