package hu.mktiti.battleship.engine;

import hu.mktiti.battleship.Position;
import hu.mktiti.battleship.Ship;
import hu.mktiti.battleship.ShootResult;
import hu.mktiti.battleship.TargetMap;

import java.util.*;
import java.util.stream.Collectors;

final class ShipMap {

    private final List<ShipState> ships;
    final TargetMap targetMap;

    ShipMap(final List<Ship> placedShips) {
        if (placedShips.stream().anyMatch(s -> !isShipValid(s))) {
            throw new IllegalArgumentException("Invalid ships");
        }

        final List<Integer> sizes = placedShips.stream()
                                        .map(Ship::getLength)
                                        .sorted(Integer::compareTo)
                                        .collect(Collectors.toList());

        final List<Integer> validSizes = Ship.getShipSizes().stream()
                                            .sorted(Integer::compareTo)
                                            .collect(Collectors.toList());

        if (!Objects.equals(sizes, validSizes)) {
            throw new IllegalArgumentException("Invalid ship sizes");
        }

        final List<ShipState> allShips = placedShips.stream().map(ShipState::new).collect(Collectors.toList());

        ships = new ArrayList<>(allShips.size());
        final Set<Position> taken = new HashSet<>();
        for (ShipState ship : allShips) {
            final int takenSize = taken.size();

            final List<Position> shipPoses = ship.positions().collect(Collectors.toList());
            taken.addAll(shipPoses);

            if (taken.size() != takenSize + shipPoses.size()) {
                throw new IllegalArgumentException("Ships overlap");
            }

            taken.addAll(ship.neighbours().collect(Collectors.toList()));
            ships.add(ship);
        }

        targetMap = new TargetMap();
    }

    private static boolean isShipValid(final Ship ship) {
        if (ship == null || ship.getDirection() == null || ship.getPosition() == null) {
            return false;
        }

        return ship.getPosition().isValid();
    }

    ShootResult shoot(final Position position) {
        if (!position.isValid()) {
            throw new IllegalArgumentException("Invalid position");
        }

        return ships.stream()
                .map(s -> s.shoot(targetMap, position))
                .filter(r -> r != ShootResult.MISS)
                .findAny()
                .orElseGet(() -> {
                    targetMap.set(position, ShootResult.MISS);
                    return ShootResult.MISS;
                });
    }

    boolean allSunk() {
        return ships.stream().allMatch(ShipState::isSunk);
    }
}