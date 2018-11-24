package hu.mktiti.battleship.engine;

import hu.mktiti.battleship.Position;
import hu.mktiti.battleship.Ship;
import hu.mktiti.battleship.ShootResult;
import hu.mktiti.battleship.TargetMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

final class ShipState {

    private static class PartState {

        final Position position;
        boolean isHit;

        private PartState(final Position position) {
            this(position, false);
        }

        private PartState(final Position position, final boolean isHit) {
            this.position = position;
            this.isHit = isHit;
        }
    }

    private final Ship.Direction direction;
    private final List<PartState> parts;

    ShipState(final Ship ship) {
        direction = ship.getDirection();

        parts = new ArrayList<>(ship.getLength());

        Position pos = ship.getPosition();
        for (int p = 0; p < ship.getLength(); p++) {
            if (!pos.isValid()) {
                throw new IllegalArgumentException("Ship at invalid position");
            }

            parts.add(new PartState(pos));
            pos = new Position(pos.x + direction.deltaX, pos.y + direction.deltaY);
        }
    }

    Stream<Position> positions() {
        return parts.stream().map(p -> p.position);
    }

    Stream<Position> neighbours() {
        final List<Position> positions = new ArrayList<>(parts.size() * 2 + 2);

        final Position start = parts.get(0).position;
        positions.add(new Position(start.x - direction.deltaX, start.y - direction.deltaY));

        final Position end = parts.get(parts.size() - 1).position;
        positions.add(new Position(end.x + direction.deltaX, end.y + direction.deltaY));

        for (PartState part : parts) {
            final Position pos = part.position;
            positions.add(new Position(pos.x - direction.deltaY, pos.y - direction.deltaX));
            positions.add(new Position(pos.x + direction.deltaY, pos.y + direction.deltaX));
        }

        return positions.stream().filter(Position::isValid);
    }

    boolean isSunk() {
        return parts.stream().allMatch(p -> p.isHit);
    }

    ShootResult shoot(final TargetMap targetMap, final Position position) {
        for (PartState part : parts) {
            if (part.position.equals(position)) {
                part.isHit = true;

                if (isSunk()) {
                    for (PartState p : parts) {
                        targetMap.set(p.position, ShootResult.SINK);

                        // Neighbors
                        targetMap.safeSet(p.position.x + direction.deltaY, p.position.y + direction.deltaX, ShootResult.MISS);
                        targetMap.safeSet(p.position.x - direction.deltaY, p.position.y - direction.deltaX, ShootResult.MISS);
                    }

                    final Position head = parts.get(0).position;
                    targetMap.safeSet(head.x - direction.deltaX, head.y - direction.deltaY, ShootResult.MISS);

                    final Position end = parts.get(parts.size() - 1).position;
                    targetMap.safeSet(end.x + direction.deltaX, end.y + direction.deltaY, ShootResult.MISS);

                    return ShootResult.SINK;
                } else {
                    targetMap.set(position, ShootResult.HIT);
                    return ShootResult.HIT;
                }
            }
        }

        return ShootResult.MISS;
    }

}