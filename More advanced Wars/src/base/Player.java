package base;

import java.awt.Color;

/***
 * This class represents a player object, with information such as name, funds
 * number of units.
 * 
 * @author Alexander Dreher
 *
 */
public class Player {

	public static final Color player1Color = Color.RED;
	public static final Color player2Color = Color.BLACK;
	public static final Color player3Color = Color.YELLOW;
	public static final Color player4Color = Color.GRAY;

	private String name;
	private int remainingUnits;
	private int nrOfBuildings;
	private int funds;

	/**
	 * The constructor of this class.
	 * 
	 * @param name
	 *            The name of the player.
	 */
	public Player(String name) {
		this.name = name;
		this.funds = 0;
	}

	public String getName() {
		return this.name;
	}

	public int getRemainingUnits() {
		return this.remainingUnits;
	}

	public void setRemainingUnits(int value) {
		this.remainingUnits = value;
	}

	public int getNrOfBuildings() {
		return this.nrOfBuildings;
	}

	public void setNrOfBuildings(int value) {
		this.nrOfBuildings = value;
	}

	public int getFunds() {
		return this.funds;
	}

	public String toString() {
		return this.name;
	}

	public void loseUnit() {
		this.remainingUnits--;
	}

	public boolean hasLost() {
		if (this.remainingUnits == 0) {
			return true;
		} else {
			return false;
		}
	}

	public void addUnit() {
		this.remainingUnits++;
	}

	public void addFunds() {
		this.funds = this.funds + this.nrOfBuildings * 1000;
	}

	/**
	 * This method handles the paying process for a new unit. It will automatically
	 * pay the amount upon possibility.
	 * 
	 * @param cost
	 *            The cost of the unit.
	 * @return True, if the funds are sufficient, false otherwise.
	 */
	public boolean canPay(int cost) {
		// Main.log("[Player] canPay : START");
		// Main.log("[Player] canPay : cost " + cost);
		// Main.log("[Player] canPay : funds " + this.funds);
		if (this.funds >= cost) {
			this.funds = this.funds - cost;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method handles the logic behind capturing a building.
	 * 
	 * @param unit
	 *            The capturing unit.
	 * @param building
	 *            The building to be captured.
	 */
	public void capture(Unit unit, Building building) {
		if (unit.getType() == Unit.TYPE_INFANTRY) {
			int strength = (int) (unit.getHealth() / 10);
			int remainingBuildingHealth = building.getHealth();

			if (strength >= remainingBuildingHealth) {
				if (building.getPlayer() == null) {
					building.setPlayer(this);
					building.setHealth(20);
					this.nrOfBuildings++;
				} else {
					// ----- HQ check -----
					if (building.getType() == Building.TYPE_HQ) {
						Logic.playerList.remove(building.getPlayer());
						if (Logic.playerList.size() == 1) {
							Logic.win();
						}
					}
					// ----- Normal building -----
					building.getPlayer().nrOfBuildings--;
					building.setPlayer(this);
					building.setHealth(20);
					this.nrOfBuildings++;
				}
			} else {
				// ----- Not enough to capture, so reduce health -----
				building.setHealth(building.getHealth() - strength);
			}
		} else {
			// ----- Not an infantry unit -----
		}
	}

}