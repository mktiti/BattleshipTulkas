package hu.mktiti.battleship;

import hu.mktiti.tulkas.api.BotInterface;

import java.util.List;

public interface BattleshipBot extends BotInterface {

    List<Ship> setupField(final List<Integer> shipSizes);

    Position nextShoot(final TargetMap targetMap, final Position previousTarget, final ShootResult previousResult);

}