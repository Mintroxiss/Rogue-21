package datalayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.GameRepository;
import domain.Level;
import domain.entities.creatures.Hero;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonGameRepository implements GameRepository {
    private static final String SAVE_FILE = "save.json";
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
    public List<Object> load() {
        try {
            GameData dto = mapper.readValue(new File(SAVE_FILE), GameData.class);
            return Mapper.toDomain(dto);
        } catch (IOException e) {
//            throw new RuntimeException(e); // TODO
            return null;
        }
    }
}
