package base;

/**
 * This class represents a single building, subdividing into the different
 * types. The HQ is the main command structure. If it is captured by the enemy,
 * you lose. A base can be used to build units. A city is just for income and
 * healing.
 * 
 * @author Alexander Dreher
 *
 */
public class Building {

	public static final int TYPE_HQ = 0;
	public static final int TYPE_BASE = 1;
	public static final int TYPE_CITY = 2;

	private String name;
	private int type;
	private Player player;
	private int defenseStrength;
	private int health;
	private int x_Coordinate;
	private int y_Coordinate;

	public Building(int type, int player, int x_Coordinate, int y_Coordinate) {
		switch (type) {
		case TYPE_HQ:
			this.name = "HQ";
			this.type = TYPE_HQ;
			this.defenseStrength = 4;
			break;
		case TYPE_BASE:
			this.name = "Base";
			this.type = TYPE_BASE;
			this.defenseStrength = 3;
			break;
		case TYPE_CITY:
			this.name = "City";
			this.type = TYPE_CITY;
			this.defenseStrength = 3;
			break;
		default:
			break;
		}
		this.health = 20;
		if (player == -1) {
			this.player = null;
		} else {
			this.player = Logic.playerList.get(player);
		}
		this.x_Coordinate = x_Coordinate;
		this.y_Coordinate = y_Coordinate;
	}

	public String getName() {
		return this.name;
	}

	public int getHealth() {
		return this.health;
	}

	public int getDefenseStrength() {
		return this.defenseStrength;
	}

	public void setHealth(int value) {
		this.health = value;
	}

	public int getType() {
		return this.type;
	}

	public String toString() {
		return "Building " + this.name + " at " + this.x_Coordinate + "/" + this.y_Coordinate + " from "
				+ (this.player);
	}

	public Player getPlayer() {
		return this.player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}