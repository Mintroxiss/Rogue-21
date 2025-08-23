package domain.positions;

public class MovablePosition extends Position {
    public MovablePosition() {}

    public MovablePosition(int x, int y) {
        super(x, y);
    }

    public void moveUp() {
        this.setY(this.getY() - 1);
    }

    public void moveDown() {
        this.setY(this.getY() + 1);
    }

    public void moveLeft() {
        this.setX(this.getX() - 1);
    }

    public void moveRight() {
        this.setX(this.getX() + 1);
    }

    /**
     * Сдвигает координаты влево вверх
     */
    public void moveLeftUp() {
        this.setX(this.getX() - 1);
        this.setY(this.getY() - 1);
    }

    /**
     * Сдвигает координаты влево вниз
     */
    public void moveLeftDown() {
        this.setX(this.getX() - 1);
        this.setY(this.getY() + 1);
    }

    /**
     * Сдвигает координаты вправо вверх
     */
    public void moveRightUp() {
        this.setX(this.getX() + 1);
        this.setY(this.getY() - 1);
    }

    /**
     * Сдвигает координаты вправо вниз
     */
    public void moveRightDown() {
        this.setX(this.getX() + 1);
        this.setY(this.getY() + 1);
    }
}
