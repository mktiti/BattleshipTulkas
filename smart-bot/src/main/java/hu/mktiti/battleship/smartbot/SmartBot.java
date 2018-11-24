package hu.mktiti.battleship.smartbot;

import hu.mktiti.battleship.*;
import hu.mktiti.tulkas.api.BotLoggerFactory;
import hu.mktiti.tulkas.api.GameBotLogger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class SmartBot implements BattleshipBot {

    private final Random random = new Random();

    private final GameBotLogger logger = BotLoggerFactory.getLogger();

    private final List<Position> targetShip = new ArrayList<>(4);

    @Override
    public List<Ship> setupField(List<Integer> shipSizes) {
        return ShipUtil.randomSetup(shipSizes);
    }

    @Override
    public Position nextShoot(TargetMap targetMap, Position previousTarget, ShootResult previousResult) {
        final Position target;

        if (previousTarget == null) {
            logger.log("First shot");
        } else if (previousResult == ShootResult.SINK) {
            logger.log("Ship sunk");
            targetShip.clear();
        } else if (previousResult == ShootResult.HIT) {
            logger.log("Ship hit");
            targetShip.add(previousTarget);
        }

        if (targetShip.size() == 0) {
            logger.log("No ship know, selecting target randomly");
            target = randomTarget(targetMap);
        } else if (targetShip.size() == 1) {
            logger.log("Ship located, direction unknown - shooting at random valid side");

            final Position shipPos = targetShip.get(0);
            final List<Position> sides = new ArrayList<>(4);
            for (int dy = -1; dy <= 1; dy += 1) {
                for (int dx = -1; dx <= 1; dx += 1) {
                    if ((dx == 0 || dy == 0) && dx != dy) {
                        final Position pos = new Position(shipPos.x + dx, shipPos.y + dy);
                        if (pos.isValid() && targetMap.get(pos) == null) {
                            sides.add(pos);
                        }
                    }
                }
            }

            target = sides.get(random.nextInt(sides.size()));

        } else {
            logger.log("Ship located, direction known");

            final Position shipHead = targetShip.get(0);
            final Position shipSnd = targetShip.get(1);

            final Ship.Direction dir = (shipHead.x == shipSnd.x) ? Ship.Direction.VERTICAL : Ship.Direction.HORIZONTAL;
            final Comparator<Position> dirComp = (a, b) -> (dir == Ship.Direction.HORIZONTAL) ?
                                                            Integer.compare(a.x, b.x) : Integer.compare(a.y, b.y);


            final Position start = targetShip.stream().min(dirComp).get();
            final Position beforeStart = new Position(start.x - dir.deltaX, start.y - dir.deltaY);

            if (beforeStart.isValid() && targetMap.get(beforeStart) == null) {
                target = beforeStart;
            } else {

                final Position end = targetShip.stream().max(dirComp).get();
                target = new Position(end.x + dir.deltaX, end.y + dir.deltaY);

            }
        }

        logger.log("Targeting: " + target);
        return target;
    }

    private Position randomTarget(final TargetMap targetMap) {
        final List<Position> targets = targetMap.freePositions().collect(Collectors.toList());
        return targets.get(random.nextInt(targets.size()));
    }

}