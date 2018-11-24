package hu.mktiti.battleship;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ShipUtil {

    private static final Random random = new Random();

    private static boolean canFit(final boolean[][] field, final Position start, final int size, final Ship.Direction direction) {

        final int dx = direction.deltaX;
        final int dy = direction.deltaY;

        for (int i = 0; i < size; i++) {
            final Position pos = new Position(start.x + i * dx, start.y + i * dy);

            if (!pos.isValid() || field[pos.y][pos.x]) {
                return false;
            }
        }

        return true;
    }

    private static void safeSet(final boolean[][] field, final int x, final int y) {
        if (x >= 0 && x < field[0].length && y >= 0 && y < field.length) {
            field[y][x] = true;
        }
    }

    public static List<Ship> randomSetup(final List<Integer> shipSizes) {
        final List<Ship> ships = new ArrayList<>(shipSizes.size());
        final boolean[][] field = new boolean[Position.HEIGHT][Position.WIDTH];

        for (int size : shipSizes) {
            final Ship.Direction dir = random.nextBoolean() ? Ship.Direction.VERTICAL : Ship.Direction.HORIZONTAL;

            final int maxX = Position.WIDTH - dir.deltaX * size;
            final int maxY = Position.HEIGHT - dir.deltaY * size;

            final List<Position> possibs = IntStream.range(0, maxX).boxed().flatMap(x ->
                    IntStream.range(0, maxY).mapToObj(y -> new Position(x, y))
            ).filter(start -> canFit(field, start, size, dir))
            .collect(Collectors.toList());

            final Position start = possibs.get(random.nextInt(possibs.size()));
            final Ship ship = new Ship(size, start, dir);

            final int dx = dir.deltaX;
            final int dy = dir.deltaY;
            for (int i = 0; i < size; i++) {
                field[start.y + dy * i][start.x + dx * i] = true;

                // Neighbors
                safeSet(field, start.x + dx * i - dy, start.y + dy * i - dx);
                safeSet(field, start.x + dx * i + dy, start.y + dy * i + dx);
            }

            safeSet(field, start.x - dx, start.y - dy);
            safeSet(field, start.x + size * dx, start.y + size * dy);

            ships.add(ship);
        }

        return ships;
    }

}