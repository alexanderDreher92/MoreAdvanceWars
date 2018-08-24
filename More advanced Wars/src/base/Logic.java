package base;

import java.awt.Color;
import java.util.*;
import javax.swing.*;

import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

/***
 * This class represents a collection of all the involved logic for the contents
 * of the maps, pathfinding and such.
 * 
 * @author Alexander Dreher
 *
 */
public class Logic {

	public static final Color LIGHT_BLUE = new Color(0, 191, 255);
	private static final int MAX_HEALING = 20;

	public static Unit[][] unitMap;
	public static Cell[][] map;
	public static Building[][] buildingMap;
	public static int[][] terrainMap;

	public static ArrayList<Player> playerList = new ArrayList<Player>();

	public static Cell currentPosition;
	public static Cell currentPointer;

	private static boolean unitIsSelected = false;
	private static boolean buildingIsSelected = false;
	private static int currentPlayer = 0;
	private static int nrOfPlayers;
	private static boolean playing = true;
	private static boolean AIEnabled = true;
	private static int dayCounter = 1;

	public static boolean unitIsSelected() {
		return unitIsSelected;
	}

	public static int getCurrentPlayer() {
		return currentPlayer;
	}

	public static boolean isPlaying() {
		return playing;
	}

	/**
	 * This method prepares the map, which will be chosen by the player, and sets
	 * the corresponding contents to the cell- ,terrain- and unitmap.
	 */
	public static void initializeMap() {
		Maps.map02();

		currentPointer = map[0][0];

		blockTerrain();
	}

	/**
	 * This auxiliary method blocks all the Cells, which have unpassable terrain.
	 */
	private static void blockTerrain() {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (terrainMap[i][j] == Maps.TERRAIN_WATER) {
					map[i][j].block();
				}
			}
		}
	}

	/**
	 * This method is called, if a Cell on the map is clicked. It handles selection,
	 * as well as confirmation of actions.
	 * 
	 * @param source
	 *            The clicked Cell.
	 */
	public static void buttonClicked(Cell source) {
		int x = source.getX_Coordinate();
		int y = source.getY_Coordinate();
		Main.log("Clicked Cell " + x + "/" + y);
		if (unitMap[x][y] != null) {
			Main.log("Unit " + unitMap[x][y]);
		}
		setCurrentPointer(x, y);
		enterKey();
	}

	/**
	 * This method handles the logic behind the end turn button.
	 */
	public static void endTurnButton() {
		// end selection
		backspaceKey(false);
		// advance turn timer
		nrOfPlayers = playerList.size();
		currentPlayer = (currentPlayer + 1) % nrOfPlayers;
		if (currentPlayer == 0) {
			dayCounter++;
			Iterator<Player> it = playerList.iterator();
			while (it.hasNext()) {
				it.next().addFunds();
			}
		}
		Main.log("Day " + dayCounter + " Player " + currentPlayer);
		Main.getWindow().getCurrentPlayerLabel().setText("Current player: " + (currentPlayer + 1));
		// reset all idle status
		unFatigueAll();
		// heal all units of respective player on properties
		regenerateUnits();
		// let AI take over
		if (currentPlayer != 0 && AIEnabled) {
			AI.FlakAI();
		}
	}

	/**
	 * This method handles the logic behind the enter key, which should handle
	 * selection and confirmation.
	 */
	public static void enterKey() {
		int x = currentPointer.getX_Coordinate();
		int y = currentPointer.getY_Coordinate();
		if (unitIsSelected == true) {
			int posX = currentPosition.getX_Coordinate();
			int posY = currentPosition.getY_Coordinate();
			if (x == posX && y == posY) {
				// ----- If clicked on itself again, release control.
				backspaceKey(false);
			} else {
				// ----- Try to move to the location -----
				boolean allowedToMove;

				if (unitMap[posX][posY].canMove()) {
					allowedToMove = isReachableWithinRange(unitMap[posX][posY].getMovementRange());
				} else {
					allowedToMove = false;
				}

				if (allowedToMove) {
					moveUnit();
				}

			}

		}
		// ----- If nothing was selected before, select either building or unit (unit
		// takes priority -----
		if (unitIsSelected == false && buildingIsSelected == false) {
			if (buildingMap[x][y] != null && playerList.indexOf(buildingMap[x][y].getPlayer()) == currentPlayer) {
				currentPosition = map[x][y];
				unitIsSelected = false;
				buildingIsSelected = true;
				Main.log("Selected building " + buildingMap[x][y]);
			}
			if (unitMap[x][y] != null && playerList.indexOf(unitMap[x][y].getPlayer()) == currentPlayer) {
				currentPosition = map[x][y];
				unitIsSelected = true;
				buildingIsSelected = false;
				Main.log("Selected unit " + unitMap[x][y]);
			}
		}
		Main.getWindow().updateMapGraphics();
	}

	/**
	 * This method handles the logic of deselection via backspace key.
	 * 
	 * @param AITurn
	 *            This boolean tells, whether it is the AI's turn or not (necessary
	 *            because of pointers).
	 */
	public static void backspaceKey(boolean AITurn) {
		if (AITurn == false) {
			if (unitIsSelected) {
				if (unitMap[currentPosition.getX_Coordinate()][currentPosition.getY_Coordinate()].canMove() == false) {
					unitMap[currentPosition.getX_Coordinate()][currentPosition.getY_Coordinate()].fatigueAttack();
				}
			}
			currentPosition = null;
			unitIsSelected = false;
			buildingIsSelected = false;
			Main.getWindow().updateMapGraphics();
		} else {

		}
	}

	/**
	 * This method enables all units to move and attack again.
	 */
	private static void unFatigueAll() {
		for (int i = 0; i < unitMap.length; i++) {
			for (int j = 0; j < unitMap[i].length; j++) {
				if (unitMap[i][j] != null) {
					unitMap[i][j].unFatigueAttack();
					unitMap[i][j].unFatigueMove();
				}
			}
		}
	}

	/**
	 * This method handles the logic behind the big action button on the right. It
	 * should either attack a unit or capture a building.
	 * 
	 * @param AITurn
	 *            This boolean tells, whether it is the AI's turn or not (necessary
	 *            because of pointers).
	 */
	public static void actionButton(boolean AITurn) {
		if (Logic.unitIsSelected || AITurn) {
			int pointerX = currentPointer.getX_Coordinate();
			int pointerY = currentPointer.getY_Coordinate();
			int posX = currentPosition.getX_Coordinate();
			int posY = currentPosition.getY_Coordinate();
			// ----- The same position is selected, so capture a building -----
			if (pointerX == posX && pointerY == posY) {
				if (buildingMap[posX][posY] != null
						&& buildingMap[posX][posY].getPlayer() != playerList.get(currentPlayer)) {
					if (unitMap[posX][posY].canAttack()) {
						playerList.get(currentPlayer).capture(unitMap[posX][posY], buildingMap[posX][posY]);
						unitMap[posX][posY].fatigueAttack();
						Main.getWindow().updateMapGraphics();
					}
				}
			}
			// ----- A different field is selected, so attck an opponent -----
			Unit attacker = unitMap[currentPosition.getX_Coordinate()][currentPosition.getY_Coordinate()];
			Unit defender = unitMap[currentPointer.getX_Coordinate()][currentPointer.getY_Coordinate()];
			if (attacker.canAttack() && isAbleToAttack(attacker, defender)) {
				attacker.damage(defender);
				attacker.fatigueAttack();
				attacker.fatigueMove();
				backspaceKey(AITurn);
			}
		}
	}

	/**
	 * This method checks, whether a unit is able to reach and attack another unit
	 * from different player.
	 * 
	 * @param attacker
	 *            The attacking unit.
	 * @param defender
	 *            The target of the attack.
	 * @return True, if all conditions for an attack are fulfilled, false otherwise.
	 */
	private static boolean isAbleToAttack(Unit attacker, Unit defender) {
		if (attacker != null && defender != null && attacker.equals(defender) == false
				&& attacker.getPlayer() != defender.getPlayer() && isWithinAttackRange(attacker, defender)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method uses breadth-first search to determine whether an opponent is
	 * within the attack range of the attacker.
	 * 
	 * @param attacker
	 *            The attacking unit.
	 * @param defender
	 *            The target of the attack.
	 * @return True, if it can be reached, false otherwise.
	 */
	public static boolean isWithinAttackRange(Unit attacker, Unit defender) {
		// Main.log("[Logic] isWithinAttackRange : START");
		// Main.log("[Logic] isWithinAttackRange : attacker = " + attacker);
		// Main.log("[Logic] isWithinAttackRange : defender = " + defender);
		// ----- Prepare variables -----
		unvisitAll();
		int distance = 0;
		ArrayList<Cell> currentList = new ArrayList<Cell>();
		ArrayList<Cell> nextList = new ArrayList<Cell>();
		currentList.add(currentPosition);

		// ----- As long as we have range left -----
		while (distance <= attacker.getAttackRange()) {
			// Main.log("[Logic] isWithinAttackRange : distance = " + distance);

			// ----- We iterate over every Cell in the current Breadth -----
			Iterator<Cell> it = currentList.iterator();
			while (it.hasNext()) {
				Cell tmpCell = it.next();
				// Main.log("[Logic] isWithinAttackRange : tmpCell = " + tmpCell);

				// ----- Check, if we have reached the target ------
				if (tmpCell.equals(currentPointer)) {
					// Main.log("[Logic] isWithinAttackRange : END true");
					return true;
				}

				// ---add all unvisited neighbors---
				tmpCell.visit();
				int tmpX = tmpCell.getX_Coordinate();
				int tmpY = tmpCell.getY_Coordinate();
				// left
				if (attackCondition(tmpX, tmpY - 1)) {
					nextList.add(map[tmpX][tmpY - 1]);
					map[tmpX][tmpY - 1].visit();
					// Main.log("[Logic] isWithinAttackRange : left added");
				}
				// top
				if (attackCondition(tmpX - 1, tmpY)) {
					nextList.add(map[tmpX - 1][tmpY]);
					map[tmpX - 1][tmpY].visit();
					// Main.log("[Logic] isWithinAttackRange : top added");
				}
				// right
				if (attackCondition(tmpX, tmpY + 1)) {
					nextList.add(map[tmpX][tmpY + 1]);
					map[tmpX][tmpY + 1].visit();
					// Main.log("[Logic] isWithinAttackRange : right added");
				}
				// bottom
				if (attackCondition(tmpX + 1, tmpY)) {
					nextList.add(map[tmpX + 1][tmpY]);
					map[tmpX + 1][tmpY].visit();
					// Main.log("[Logic] isWithinAttackRange : bottom added");
				}
			}

			// ----- Update the lists and distance -----
			currentList.clear();
			currentList.addAll(nextList);
			nextList.clear();
			distance++;

		}

		// ----- The target was not reached within distance -----
		// Main.log("[Logic] isWithinAttackRange : END false");
		return false;
	}

	/**
	 * This auxiliary method for the method isWithinAttackRange checks the validity
	 * and passability of the cell at the coordinates.
	 * 
	 * @param x
	 *            The x coordinate of the Cell to be checked.
	 * @param y
	 *            The y coordinate of the Cell to be checked.
	 * @return True, if the cell is free, false, otherwise.
	 */
	private static boolean attackCondition(int x, int y) {
		if (isValidMapField(x, y, map) && map[x][y].isBlocked() == false && map[x][y].isVisited() == false) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method is called, if during an attack the defender is defeated and
	 * removes this unit from the game.
	 */
	public static void removeDefender() {
		int currX = currentPointer.getX_Coordinate();
		int currY = currentPointer.getY_Coordinate();
		Player alignment = unitMap[currX][currY].getPlayer();
		decreaseUnitCounterForPlayer(alignment);
		unitMap[currX][currY] = null;
		map[currX][currY].setText("");
		System.gc();
		Main.getWindow().updateMapGraphics();
	}

	/**
	 * This method is called, if during an attack the attacker is defeated and
	 * removes this unit from the game.
	 */
	public static void removeAttacker() {
		int currX = currentPosition.getX_Coordinate();
		int currY = currentPosition.getY_Coordinate();
		Player alignment = unitMap[currX][currY].getPlayer();
		decreaseUnitCounterForPlayer(alignment);
		unitMap[currX][currY] = null;
		map[currX][currY].setText("");
		backspaceKey(false);
		System.gc();
		Main.getWindow().updateMapGraphics();
	}

	/**
	 * This method decreases the amount of units for one player, and also checks the
	 * win condition.
	 * 
	 * @param player
	 *            The player losing an unit.
	 */
	private static void decreaseUnitCounterForPlayer(Player player) {
		Player tmpPlayer = playerList.get(playerList.indexOf(player));
		tmpPlayer.loseUnit();
		;
		if (tmpPlayer.hasLost()) {
			playerList.remove(player);
		}
		if (playerList.size() == 1) {
			win();
		}

	}

	/**
	 * This method handles all the logic concerning the end of the game.
	 */
	public static void win() {
		playing = false;
		Window window = Main.getWindow();
		JFrame winFrame = new JFrame();
		winFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		winFrame.setLocation(window.getX() + 200, window.getY() + 200);
		winFrame.setSize(200, 200);
		winFrame.setTitle("Schlacht vorbei!");
		JLabel label = new JLabel("Sieger: " + playerList.get(0));
		winFrame.add(label);
		winFrame.setVisible(true);
	}

	/**
	 * This method moves the currentPointer at the given coordinates.
	 * 
	 * @param x
	 *            The new x coordinate.
	 * @param y
	 *            The new y coordinate.
	 */
	public static void setCurrentPointer(int x, int y) {
		currentPointer.setBorder(new LineBorder(Color.GRAY));
		currentPointer = map[x][y];
		currentPointer.setBorder(new EtchedBorder(Color.GRAY, Color.WHITE));
	}

	/**
	 * This method checks, whether given coordinates are in the bounds of the given
	 * map.
	 * 
	 * @param x
	 *            The x coordinate of the field to be checked.
	 * @param y
	 *            The y coordinate of the field to be checked.
	 * @param map
	 *            The array to check.
	 * @return True, if it is a valid pair of coordinates, false otherwise.
	 */
	public static boolean isValidMapField(int x, int y, Object[][] map) {
		if (x < map.length && x >= 0 && y < map[0].length && y >= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This auxiliary method for pathfinding resets all Cell's visitation status.
	 */
	public static void unvisitAll() {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j].unvisit();
			}
		}
	}

	/**
	 * This auxiliary method for pathfinding resets all Cell's visitation2 status.
	 */
	public static void unvisitAll2() {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j].unvisit2();
			}
		}
	}

	/**
	 * This method checks, whether
	 * 
	 * @param range
	 * @return
	 */
	public static boolean isReachableWithinRange(int range) {
		// ----- Check, if target is already occupied -----
		if (unitMap[currentPointer.getX_Coordinate()][currentPointer.getY_Coordinate()] != null) {
			return false;
		}
		// ----- Prepare variables -----
		unvisitAll();
		int distance = 0;
		Player player = unitMap[currentPosition.getX_Coordinate()][currentPosition.getY_Coordinate()].getPlayer();
		int alignmentPlayer = playerList.indexOf(player);
		ArrayList<Cell> currentList = new ArrayList<Cell>();
		ArrayList<Cell> nextList = new ArrayList<Cell>();
		currentList.add(currentPosition);

		// ----- As long as we have range left -----
		while (distance <= range) {

			// ----- We iterate over every Cell in the current Breadth -----
			Iterator<Cell> it = currentList.iterator();
			while (it.hasNext()) {
				Cell tmpCell = it.next();

				// ----- Check, if we have reached the target ------
				if (tmpCell.equals(currentPointer)) {
					return true;
				}

				// ---add all unvisited neighbors---
				tmpCell.visit();
				int tmpX = tmpCell.getX_Coordinate();
				int tmpY = tmpCell.getY_Coordinate();
				// left
				if (pathCondition(tmpX, tmpY - 1, alignmentPlayer)) {
					nextList.add(map[tmpX][tmpY - 1]);
					map[tmpX][tmpY - 1].visit();
				}
				// top
				if (pathCondition(tmpX - 1, tmpY, alignmentPlayer)) {
					nextList.add(map[tmpX - 1][tmpY]);
					map[tmpX - 1][tmpY].visit();
				}
				// right
				if (pathCondition(tmpX, tmpY + 1, alignmentPlayer)) {
					nextList.add(map[tmpX][tmpY + 1]);
					map[tmpX][tmpY + 1].visit();
				}
				// bottom
				if (pathCondition(tmpX + 1, tmpY, alignmentPlayer)) {
					nextList.add(map[tmpX + 1][tmpY]);
					map[tmpX + 1][tmpY].visit();
				}
			}

			// ----- Update the lists and distance -----
			currentList.clear();
			currentList.addAll(nextList);
			nextList.clear();
			distance++;
		}
		// ----- The target was not reached within distance -----
		return false;
	}

	/**
	 * This auxiliary method replaces the check in the breadth-first search for
	 * impassable terrain, blocking enemy units and visitation status.
	 * 
	 * @param x
	 *            The x coordinate of the Cell to be checked.
	 * @param y
	 *            The y coordinate of the Cell to be checked.
	 * @param alignment
	 *            The currently moving player.
	 * @return True, if the Cell is unvisited and passable, false otherwise.
	 */
	public static boolean pathCondition(int x, int y, int alignment) {
		if (isValidMapField(x, y, map) && map[x][y].isBlocked() == false && map[x][y].isVisited() == false
				&& (unitMap[x][y] == null || playerList.indexOf(unitMap[x][y].getPlayer()) == alignment)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This auxiliary method replaces the check in the breadth-first search for
	 * impassable terrain, blocking enemy units and visitation2 status.
	 * 
	 * @param x
	 *            The x coordinate of the Cell to be checked.
	 * @param y
	 *            The y coordinate of the Cell to be checked.
	 * @param alignment
	 *            The currently moving player.
	 * @return True, if the Cell is unvisited and passable, false otherwise.
	 */
	public static boolean pathCondition2(int x, int y, int alignment) {
		if (isValidMapField(x, y, map) && map[x][y].isBlocked() == false && map[x][y].isVisited2() == false
				&& (unitMap[x][y] == null || playerList.indexOf(unitMap[x][y].getPlayer()) == alignment)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method handles the movement of a unit from the current position to the
	 * current pointer.
	 */
	public static void moveUnit() {
		int posX = currentPosition.getX_Coordinate();
		int posY = currentPosition.getY_Coordinate();
		int pointX = currentPointer.getX_Coordinate();
		int pointY = currentPointer.getY_Coordinate();
		Unit unitToMove = unitMap[posX][posY];
		if (unitToMove.canMove()) {
			unitToMove.fatigueMove();
			currentPosition.setText("");
			unitMap[pointX][pointY] = unitToMove;
			unitMap[posX][posY] = null;
			currentPosition = map[pointX][pointY];
			unitToMove.setX_Coordinate(pointX);
			unitToMove.setY_Coordinate(pointY);
			currentPosition.setText("Selected");
		}
	}

	/**
	 * This method draws the possible movement area of a unit upon selection.
	 */
	public static void highlightMovement() {
		unvisitAll();
		int distance = 0;
		int range = unitMap[currentPosition.getX_Coordinate()][currentPosition.getY_Coordinate()].getMovementRange();
		ArrayList<Cell> currList = new ArrayList<Cell>();
		ArrayList<Cell> nextList = new ArrayList<Cell>();
		Player player = unitMap[currentPosition.getX_Coordinate()][currentPosition.getY_Coordinate()].getPlayer();
		int alignmentPlayer = playerList.indexOf(player);
		currList.add(currentPosition);

		// ----- As long as we have range left -----
		while (distance <= range) {
			Iterator<Cell> it = currList.iterator();
			while (it.hasNext()) {
				Cell tmpCell = it.next();
				tmpCell.setBackground(LIGHT_BLUE);
				tmpCell.visit();
				int tmpX = tmpCell.getX_Coordinate();
				int tmpY = tmpCell.getY_Coordinate();
				// left
				if (pathCondition(tmpX, tmpY - 1, alignmentPlayer)) {
					nextList.add(map[tmpX][tmpY - 1]);
					map[tmpX][tmpY - 1].visit();
				}
				// top
				if (pathCondition(tmpX - 1, tmpY, alignmentPlayer)) {
					nextList.add(map[tmpX - 1][tmpY]);
					map[tmpX - 1][tmpY].visit();
				}
				// right
				if (pathCondition(tmpX, tmpY + 1, alignmentPlayer)) {
					nextList.add(map[tmpX][tmpY + 1]);
					map[tmpX][tmpY + 1].visit();
				}
				// bottom
				if (pathCondition(tmpX + 1, tmpY, alignmentPlayer)) {
					nextList.add(map[tmpX + 1][tmpY]);
					map[tmpX + 1][tmpY].visit();
				}
			}
			currList.clear();
			currList.addAll(nextList);
			nextList.clear();
			distance++;
		}
	}

	/**
	 * This method handles the logic behind buying additional units.
	 * 
	 * @param type
	 *            The type of the unit to be bought.
	 * @param AITurn
	 *            This boolean tells, whether it is the AI's turn or not (necessary
	 *            because of pointers).
	 */
	public static void buyUnit(int type, boolean AITurn) {
		// Main.log("[Logic] buyUnit : START");
		// Main.log("[Logic] buyUnit : type " + type);
		if (buildingIsSelected || AITurn) {
			switch (type) {
			case 0:
				// Main.log("[Logic] buyUnit : buy infantry");
				if (playerList.get(currentPlayer).canPay(Unit.COST_INFANTRY)) {
					Unit newUnit = new Unit(Unit.TYPE_INFANTRY, currentPlayer, currentPosition.getX_Coordinate(),
							currentPosition.getY_Coordinate());
					newUnit.fatigueAttack();
					newUnit.fatigueMove();
					unitMap[currentPosition.getX_Coordinate()][currentPosition.getY_Coordinate()] = newUnit;
					playerList.get(currentPlayer).addUnit();
				}
				break;
			case 1:
				if (playerList.get(currentPlayer).canPay(Unit.COST_SCOUT)) {
					Unit newUnit = new Unit(Unit.TYPE_SCOUT, currentPlayer, currentPosition.getX_Coordinate(),
							currentPosition.getY_Coordinate());
					newUnit.fatigueAttack();
					newUnit.fatigueMove();
					unitMap[currentPosition.getX_Coordinate()][currentPosition.getY_Coordinate()] = newUnit;
					playerList.get(currentPlayer).addUnit();
				}
				break;
			case 2:
				if (playerList.get(currentPlayer).canPay(Unit.COST_LIGHT_TANK)) {
					Unit newUnit = new Unit(Unit.TYPE_LIGHT_TANK, currentPlayer, currentPosition.getX_Coordinate(),
							currentPosition.getY_Coordinate());
					newUnit.fatigueAttack();
					newUnit.fatigueMove();
					unitMap[currentPosition.getX_Coordinate()][currentPosition.getY_Coordinate()] = newUnit;
					playerList.get(currentPlayer).addUnit();

				}
				break;
			default:
				break;
			}
		}
		backspaceKey(false);
		Main.getWindow().updateMapGraphics();
		// Main.log("[Logic] buyUnit : END");
	}

	/**
	 * This method iterates over the map and regenerates all units on bases of the
	 * currentPlayer.
	 */
	public static void regenerateUnits() {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (buildingMap[i][j] != null && playerList.indexOf(buildingMap[i][j].getPlayer()) == currentPlayer) {
					if (unitMap[i][j] != null && playerList.indexOf(unitMap[i][j].getPlayer()) == currentPlayer) {
						regenerate(i, j);
					}
				}
			}
		}
	}

	/**
	 * This method handles the healing logic behind the daily regeneration.
	 * 
	 * @param x
	 *            The x coordinate of the unit to be healed.
	 * @param y
	 *            The y coordinate of the unit to be healed.
	 */
	private static void regenerate(int x, int y) {
		Player currPlayer = playerList.get(currentPlayer);
		Unit unitToHeal = unitMap[x][y];
		int damage = 100 - (int) unitToHeal.getHealth();
		int cost;
		if (damage >= MAX_HEALING) {
			cost = (int) ((MAX_HEALING / 100) * unitToHeal.getCost());
		} else {
			cost = (int) ((damage / 100) * unitToHeal.getCost());
		}
		if (currPlayer.canPay(cost)) {
			if (damage > MAX_HEALING) {
				unitToHeal.setHealth(unitToHeal.getHealth() + MAX_HEALING);
			} else {
				unitToHeal.setHealth(100.0);
			}
		}
	}

}