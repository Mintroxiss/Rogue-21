package domain.entities.creatures;

public enum EnemyType {
    ZOMBIE("Zombie"),
    VAMPIRE("Vampire"),
    GHOST("Ghost"),
    OGRE("Ogre"),
    SNAKE_MAGE("Snake Mage");

    private final String name;

    EnemyType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
