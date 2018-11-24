package hu.mktiti.battleship;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Ship implements Serializable {

    private static final List<Integer> shipSizes = Arrays.asList(4, 3, 3, 2, 2, 2, 1, 1, 1, 1);

    public enum Direction implements Serializable {
        VERTICAL(0, 1), HORIZONTAL(1, 0);

        public final int deltaX;
        public final int deltaY;

        Direction(final int deltaX, final int deltaY) {
            this.deltaX = deltaX;
            this.deltaY = deltaY;
        }
    }

    final int length;
    final Position position;
    final Direction direction;

    public Ship(final int length, final Position position, final Direction direction) {
        if (length < 1) {
            throw new IllegalArgumentException("Ship size must be positive");
        }

        if (position == null || !position.isValid()) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }

        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }

        this.length = length;
        this.position = position;
        this.direction = direction;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Ship) {
            final Ship other = (Ship)o;
            return other.length == length && Objects.equals(other.position, position) && other.direction == direction;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, position, direction);
    }

    @Override
    public String toString() {
        return "Ship {length: " + length + ", position: " + position + ", direction: " + direction + "}";
    }

    public static List<Integer> getShipSizes() {
        return shipSizes;
    }

    public int getLength() {
        return length;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }
}