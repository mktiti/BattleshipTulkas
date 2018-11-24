package hu.mktiti.battleship;

import java.io.Serializable;
import java.util.Objects;

public final class Position implements Serializable {

    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;

    public final int x;
    public final int y;

    public Position() {
        this(0, 0);
    }

    public Position(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isValid() {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Position) {
            final Position other = (Position)o;
            return other.x == x && other.y == y;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "{x: " + x + ", y: " + y + '}';
    }
}