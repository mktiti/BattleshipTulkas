package hu.mktiti.battleship.engine;

import hu.mktiti.battleship.*;
import hu.mktiti.tulkas.api.match.DuelGameEngine;
import hu.mktiti.tulkas.api.match.MatchResult;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class BattleshipEngine extends DuelGameEngine<BattleshipBot> {

    private class BotData {
        final ShipMap shipMap;
        Position previousTarget;
        ShootResult previousResult;

        private BotData(final ShipMap shipMap) {
            this.shipMap = shipMap;
        }
    }

    private BotData botAData;
    private BotData botBData;

    public BattleshipEngine(final BattleshipBot botA, final BattleshipBot botB) {
        super(botA, botB);
    }

    private BotData getCurrentData() {
        return currentBotActor() == MatchResult.BotActor.BOT_A ? botAData : botBData;
    }

    private BotData getOpponentData() {
        return currentBotActor() == MatchResult.BotActor.BOT_A ? botBData : botAData;
    }

    @Override
    protected TurnResult initBot(final BattleshipBot bot, final boolean isBotA) {
        final List<Ship> setup = bot.setupField(Ship.getShipSizes());
        final BotData data = new BotData(new ShipMap(setup));

        if (isBotA) {
            botAData = data;
        } else {
            botBData = data;
        }

        return TurnResult.CONTINUE;
    }

    @Override
    protected TurnResult playTurn(final BattleshipBot bot, final boolean isBotA) {
        final BotData currentData = getCurrentData();
        final BotData opponentData = getOpponentData();

        do {
            final Position target = bot.nextShoot(opponentData.shipMap.targetMap, currentData.previousTarget, currentData.previousResult);

            if (target == null || !target.isValid()) {
                logForCurrent("You have returned an invalid shoot target (" + target + ")");
                return TurnResult.ERROR;
            }

            if (opponentData.shipMap.targetMap.get(target) != null) {
                logForCurrent("You have already shot at this target (" + target + ")");
                return TurnResult.ERROR;
            }

            final ShootResult result = opponentData.shipMap.shoot(target);

            currentData.previousTarget = target;
            currentData.previousResult = result;

            logForCurrent("Shot at '" + target + "' resulted in '" + result + "'");
            logForCurrent("Target:\n" + opponentData.shipMap.targetMap);

            if (result == ShootResult.SINK && opponentData.shipMap.allSunk()) {
                logForCurrent("Congratulations, you have won!");
                return TurnResult.WIN;
            }

        } while (currentData.previousResult != ShootResult.MISS);

        return TurnResult.CONTINUE;
    }

    public static void main(String[] args) {
        final BattleshipBot bot = new BattleshipBot() {
            @Override
            public List<Ship> setupField(List<Integer> shipSizes) {
                return ShipUtil.randomSetup(shipSizes);
            }

            @Override
            public Position nextShoot(TargetMap targetMap, Position previousTarget, ShootResult previousResult) {
                final List<Position> targets = targetMap.freePositions().collect(Collectors.toList());
                return targets.get(new Random().nextInt(targets.size()));
            }
        };

        new BattleshipEngine(bot, bot).playGame();
    }

}
