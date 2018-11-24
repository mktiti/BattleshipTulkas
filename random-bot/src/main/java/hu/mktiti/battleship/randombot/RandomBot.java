package hu.mktiti.battleship.randombot;

import hu.mktiti.battleship.*;
import hu.mktiti.tulkas.api.BotLoggerFactory;
import hu.mktiti.tulkas.api.GameBotLogger;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class RandomBot implements BattleshipBot {

    private final Random random = new Random();

    private final GameBotLogger logger = BotLoggerFactory.getLogger();

    @Override
    public List<Ship> setupField(List<Integer> shipSizes) {
        return ShipUtil.randomSetup(shipSizes);
    }

    @Override
    public Position nextShoot(TargetMap targetMap, Position previousTarget, ShootResult previousResult) {
        final List<Position> targets = targetMap.freePositions().collect(Collectors.toList());
        final Position target = targets.get(random.nextInt(targets.size()));

        logger.log("Targeting " + target);

        return target;
    }

}