package domain;

public class EnterTheName {
    public final int MAX_LENGTH = 12;
    private String name = "";

    public void addLetter(char letter) {
        if (name.length() < MAX_LENGTH) {
            name = (name + letter).trim();
        }
    }

    public void removeLastLetter() {
        if (!name.isEmpty()) {
            name = name.substring(0, name.length() - 1);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.length() <= MAX_LENGTH) {
            this.name = name;
        }
    }

    public char[][] getStartField(int ROWS, int COLUMNS) {
        char[][] field = new char[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                field[i][j] = ' ';
            }
        }
        String[] strings = {"Rogue 21", "Please, enter the name:", name};
        for (int i = 0; i < strings.length; i++) {
            String str = strings[i];
            for (int j = 0; j < str.length(); j++) {
                field[ROWS / 2 + i * 2][COLUMNS / 2 - str.length() / 2 + j] = str.charAt(j);
            }
        }

        return field;
    }
}
