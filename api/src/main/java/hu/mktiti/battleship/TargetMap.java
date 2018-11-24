package hu.mktiti.battleship;

import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class TargetMap implements Serializable {

    private final ShootResult[][] field = new ShootResult[Position.HEIGHT][Position.WIDTH];

    private final static String ROW_SEPARATOR = IntStream.range(0, Position.WIDTH)
            .mapToObj(i -> "-")
            .collect(Collectors.joining("+", "+", "+"));

    public ShootResult get(final int x, final int y) {
        return field[y][x];
    }

    public ShootResult get(final Position position) {
        return get(position.x, position.y);
    }

    public void safeSet(final int x, final int y, final ShootResult result) {
        if (x >= 0 && x < Position.WIDTH && y >= 0 && y < Position.HEIGHT) {
            field[y][x] = result;
        }
    }

    public void set(final int x, final int y, final ShootResult result) {
        field[y][x] = result;
    }

    public void set(final Position position, final ShootResult result) {
        set(position.x, position.y, result);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append(ROW_SEPARATOR);
        builder.append('\n');

        for (ShootResult[] row : field) {
            builder.append('|');
            for (ShootResult cell : row) {
                if (cell == null) {
                    builder.append(' ');
                } else {
                    switch (cell) {
                        case MISS:
                            builder.append('~');
                            break;
                        case HIT:
                            builder.append('*');
                            break;
                        default:
                            builder.append('X');
                            break;
                    }
                }
                builder.append('|');
            }

            builder.append('\n');
            builder.append(ROW_SEPARATOR);
            builder.append('\n');
        }

        return builder.toString();
    }

    public Stream<Position> freePositions() {
        return IntStream.range(0, Position.WIDTH).boxed().flatMap(x ->
            IntStream.range(0, Position.HEIGHT).mapToObj(y -> new Position(x, y))
        ).filter(p -> get(p) == null);
    }

}