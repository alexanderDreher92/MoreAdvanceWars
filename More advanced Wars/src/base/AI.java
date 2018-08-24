package base;

import java.util.*;

/***
 * This class represents the AI scriptsand its used methods for pathfinding, map
 * evaluation, and acting.
 * 
 * @author Alexander Dreher
 *
 */
public class AI {

	public static ArrayList<Unit> avaiableUnits = new ArrayList<Unit>();
	public static ArrayList<Unit> enemyUnits = new ArrayList<Unit>();

	/**
	 * This simple AI will spam Infantry from all its bases, move to the closest
	 * enemy, and attack them. It does not try to capture properties or the enemy
	 * HQ. CRUSHING!
	 */
	public static void FlakAI() {
		if (Logic.isPlaying()) {
			Logic.currentPointer = null;
			Logic.currentPosition = null;
			// ----- If it is already near the enemy, attack ----
			prepareUnitLists();
			directAttack();
			// ----- If it is not near the enemy, try to move next to it and attack -----
			prepareUnitLists();
			indirectAttack();
			// ----- Otherwise move closer to the enemy -----
			prepareUnitLists();
			Iterator<Unit> it = avaiableUnits.iterator();
			while (it.hasNext()) {
				Unit currUnit = it.next();
				if (enemyUnits.size() > 0) {
					Unit nearestUnit = findNearestUnit(currUnit);
					moveToClosestPosition(currUnit, nearestUnit);
				}
			}
			// ----- Build some more units on every avaiable base -----
			buildUnits();
			// ----- EndTurn -----
			Logic.currentPointer = Logic.map[0][0];
			Logic.currentPosition = null;
			Logic.endTurnButton();
		}
	}

	/**
	 * This method handles the logic, if the acting unit, represented by the
	 * currentPosition, is next to an opponent.
	 */
	private static void directAttack() {
		Iterator<Unit> it = avaiableUnits.iterator();
		while (it.hasNext()) {
			Unit currUnit = it.next();
			Logic.currentPosition = Logic.map[currUnit.getX_Coordinate()][currUnit.getY_Coordinate()];
			Iterator<Unit> it2 = enemyUnits.iterator();
			while (it2.hasNext()) {
				Unit targetUnit = it2.next();
				Logic.currentPointer = Logic.map[targetUnit.getX_Coordinate()][targetUnit.getY_Coordinate()];
				if (Logic.isWithinAttackRange(currUnit, targetUnit)) {
					Logic.currentPosition = Logic.map[currUnit.getX_Coordinate()][currUnit.getY_Coordinate()];
					Logic.currentPointer = Logic.map[targetUnit.getX_Coordinate()][targetUnit.getY_Coordinate()];
					Logic.actionButton(true);
					break;
				}
			}
		}
	}

	/**
	 * This method handles the logic, if the unit is not directly near an enemy, but
	 * the opponent, represented by the currentPointer, is in attack range of the
	 * acting unit, represented by the currentPosition.
	 */
	private static void indirectAttack() {
		Iterator<Unit> it = avaiableUnits.iterator();
		while (it.hasNext()) {
			Unit currUnit = it.next();
			Logic.currentPosition = Logic.map[currUnit.getX_Coordinate()][currUnit.getY_Coordinate()];
			Iterator<Unit> it2 = enemyUnits.iterator();
			while (it2.hasNext()) {
				Unit targetUnit = it2.next();
				int targetX = targetUnit.getX_Coordinate();
				int targetY = targetUnit.getY_Coordinate();
				// ----- Try to move next to it and attack -----
				// left
				if (Logic.isValidMapField(targetX, targetY - 1, Logic.map)) {
					Logic.currentPointer = Logic.map[targetX][targetY - 1];
					if (Logic.isReachableWithinRange(currUnit.getMovementRange())) {
						Logic.moveUnit();
						Logic.currentPointer = Logic.map[targetX][targetY];
						Logic.actionButton(true);
						break;
					}
				}
				// top
				if (Logic.isValidMapField(targetX - 1, targetY, Logic.map)) {
					Logic.currentPointer = Logic.map[targetX - 1][targetY];
					if (Logic.isReachableWithinRange(currUnit.getMovementRange())) {
						Logic.moveUnit();
						Logic.currentPointer = Logic.map[targetX][targetY];
						Logic.actionButton(true);
						break;
					}
				}
				// right
				if (Logic.isValidMapField(targetX, targetY + 1, Logic.map)) {
					Logic.currentPointer = Logic.map[targetX][targetY + 1];
					if (Logic.isReachableWithinRange(currUnit.getMovementRange())) {
						Logic.moveUnit();
						Logic.currentPointer = Logic.map[targetX][targetY];
						Logic.actionButton(true);
						break;
					}
				}
				// bottom
				if (Logic.isValidMapField(targetX + 1, targetY, Logic.map)) {
					Logic.currentPointer = Logic.map[targetX + 1][targetY];
					if (Logic.isReachableWithinRange(currUnit.getMovementRange())) {
						Logic.moveUnit();
						Logic.currentPointer = Logic.map[targetX][targetY];
						Logic.actionButton(true);
						break;
					}
				}
			}
		}
	}

	/**
	 * This method uses breadth-first search to find a unit of an opposing player
	 * with the minimal distance to the source unit.
	 * 
	 * @param source
	 *            The unit, which seeks its closest opponent.
	 * @return The Unit, which is closest.
	 */
	private static Unit findNearestUnit(Unit source) {
		Unit result = null;
		Unit currUnit = source;
		Logic.currentPosition = Logic.map[currUnit.getX_Coordinate()][currUnit.getY_Coordinate()];
		Iterator<Unit> it2 = enemyUnits.iterator();
		int distance = Integer.MAX_VALUE;
		while (it2.hasNext()) {
			Unit nextUnit = it2.next();
			int distanceToPosition = (int) (Math.abs(currUnit.getX_Coordinate() - nextUnit.getX_Coordinate()))
					+ (Math.abs(currUnit.getY_Coordinate() - nextUnit.getY_Coordinate()));
			if (distanceToPosition < distance) {
				distance = distanceToPosition;
				result = nextUnit;
			}
		}
		return result;
	}

	/**
	 * This method uses breadth-first search from the targets position, to find the
	 * closest unoccupied spot where the moving unit can move to.
	 * 
	 * @param mover
	 *            The unit which shall move.
	 * @param target
	 *            The target unit, to which should be moved.
	 */
	private static void moveToClosestPosition(Unit mover, Unit target) {
		// ----- Prepare variables -----
		Logic.unvisitAll2();
		Logic.currentPosition = Logic.map[mover.getX_Coordinate()][mover.getY_Coordinate()];
		int alignmentPlayer = Logic.playerList.indexOf(mover.getPlayer());
		ArrayList<Cell> currList = new ArrayList<Cell>();
		ArrayList<Cell> nextList = new ArrayList<Cell>();
		currList.add(Logic.map[target.getX_Coordinate()][target.getY_Coordinate()]);
		int failsafe = 0;
		// ----- Failsafe ensures, that it stops eventually -----
		while (failsafe < 25) {
			Iterator<Cell> it = currList.iterator();
			while (it.hasNext()) {
				Cell tmpCell = it.next();
				Logic.currentPointer = Logic.map[tmpCell.getX_Coordinate()][tmpCell.getY_Coordinate()];
				if (Logic.isReachableWithinRange(mover.getMovementRange())) {
					Logic.moveUnit();
					return;
				} else {
					tmpCell.visit2();
					int tmpX = tmpCell.getX_Coordinate();
					int tmpY = tmpCell.getY_Coordinate();
					// ----- Add neighbors -----
					// left
					if (Logic.pathCondition2(tmpX, tmpY - 1, alignmentPlayer)) {
						nextList.add(Logic.map[tmpX][tmpY - 1]);
						Logic.map[tmpX][tmpY - 1].visit2();
					}
					// top
					if (Logic.pathCondition2(tmpX - 1, tmpY, alignmentPlayer)) {
						nextList.add(Logic.map[tmpX - 1][tmpY]);
						Logic.map[tmpX - 1][tmpY].visit2();
					}
					// right
					if (Logic.pathCondition2(tmpX, tmpY + 1, alignmentPlayer)) {
						nextList.add(Logic.map[tmpX][tmpY + 1]);
						Logic.map[tmpX][tmpY + 1].visit2();
					}
					// bottom
					if (Logic.pathCondition2(tmpX + 1, tmpY, alignmentPlayer)) {
						nextList.add(Logic.map[tmpX + 1][tmpY]);
						Logic.map[tmpX + 1][tmpY].visit2();
					}
				}
			}
			currList.clear();
			currList.addAll(nextList);
			nextList.clear();
			failsafe++;
		}
	}

	/**
	 * This method creates the list of the AI's own units, as well as a list of
	 * target units.
	 */
	private static void prepareUnitLists() {
		avaiableUnits.clear();
		enemyUnits.clear();
		Unit[][] unitMap = Logic.unitMap;
		// ----- Gather all units in a list -----
		for (int i = 0; i < unitMap.length; i++) {
			for (int j = 0; j < unitMap[i].length; j++) {
				if (unitMap[i][j] != null && Logic.playerList.indexOf(unitMap[i][j].getPlayer()) == 1) {
					avaiableUnits.add(unitMap[i][j]);
				}
				if (unitMap[i][j] != null && Logic.playerList.indexOf(unitMap[i][j].getPlayer()) != 1) {
					enemyUnits.add(unitMap[i][j]);
				}
			}
		}
	}

	/**
	 * This method searches the whole map for bases of the AI-Player, and simply
	 * builds Infantry on them.
	 */
	private static void buildUnits() {
		// ----- Search the whole map -----
		for (int i = 0; i < Logic.map.length; i++) {
			for (int j = 0; j < Logic.map[i].length; j++) {
				// ----- If there is a building... -----
				if (Logic.buildingMap[i][j] != null && Logic.buildingMap[i][j].getType() == Building.TYPE_BASE
						&& Logic.playerList.indexOf(Logic.buildingMap[i][j].getPlayer()) == Logic.getCurrentPlayer()) {
					// ----- ... and no unit occupying it -----
					if (Logic.unitMap[i][j] == null) {
						Logic.currentPosition = Logic.map[i][j];
						Logic.buyUnit(Unit.TYPE_INFANTRY, true);
					}
				}
			}
		}
	}

}
