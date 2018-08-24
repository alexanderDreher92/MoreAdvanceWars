package base;

/**
 * This class represents the movable units on the field with their health and
 * attack options and such.
 * 
 * @author Alexander Dreher
 *
 */
public class Unit {

	public static final int TYPE_INFANTRY = 0;
	public static final int COST_INFANTRY = 1000;
	public static final int TYPE_SCOUT = 1;
	public static final int COST_SCOUT = 4000;
	public static final int TYPE_LIGHT_TANK = 2;
	public static final int COST_LIGHT_TANK = 7000;

	/**
	 * This table denotes the damage values from different unit types. The first
	 * coordinate is the attacker, the second one the defender. This can be modified
	 * by powers for each unit separately.
	 */
	private int[][] damageTable = { { 45, 25, 10 }, { 60, 25, 10 }, { 45, 70, 50 } };

	private String name;
	private int type;
	private int cost;
	private Player player;
	private int movementRange;
	private int attackRange;
	private double health;
	private boolean canMove = true;
	private boolean canAttack = true;
	private int x_Coordinate;
	private int y_Coordinate;

	public String getName() {
		return this.name;
	}

	public int getType() {
		return this.type;
	}

	public int getCost() {
		return this.cost;
	}

	public Player getPlayer() {
		return this.player;
	}

	public int getMovementRange() {
		return this.movementRange;
	}

	public int getAttackRange() {
		return this.attackRange;
	}

	public double getHealth() {
		return this.health;
	}

	public boolean canMove() {
		return this.canMove;
	}

	public boolean canAttack() {
		return this.canAttack;
	}

	public void setHealth(double value) {
		this.health = value;
	}

	public void fatigueMove() {
		this.canMove = false;
	}

	public void fatigueAttack() {
		this.canAttack = false;
		this.canMove = false;
	}

	public void unFatigueMove() {
		this.canMove = true;
	}

	public void unFatigueAttack() {
		this.canAttack = true;
	}

	public int getX_Coordinate() {
		return this.x_Coordinate;
	}

	public int getY_Coordinate() {
		return this.y_Coordinate;
	}
	
	public void setX_Coordinate(int value) {
		this.x_Coordinate = value;
	}

	public void setY_Coordinate(int value) {
		this.y_Coordinate = value;
	}

	/**
	 * The constructor of this class.
	 * 
	 * @param type
	 *            The unit type, e.g. tank or infantry.
	 * @param player
	 *            The owner of the unit.
	 * @param x_Coordinate
	 *            The units current x coordinate.
	 * @param y_Coordinate
	 *            The units current y coordinate.
	 */
	public Unit(int type, int player, int x_Coordinate, int y_Coordinate) {
		switch (type) {
		case TYPE_INFANTRY:
			this.name = "Infantry";
			this.type = TYPE_INFANTRY;
			this.cost = COST_INFANTRY;
			this.movementRange = 3;
			this.attackRange = 1;
			break;
		case TYPE_SCOUT:
			this.name = "Scout";
			this.type = TYPE_SCOUT;
			this.cost = COST_SCOUT;
			this.movementRange = 6;
			this.attackRange = 1;
			break;
		case TYPE_LIGHT_TANK:
			this.name = "Light Tank";
			this.type = TYPE_LIGHT_TANK;
			this.cost = COST_LIGHT_TANK;
			this.movementRange = 4;
			this.attackRange = 1;
			break;
		default:
			this.name = "Unknown";
			this.type = -1;
			this.cost = 0;
			this.movementRange = 100;
			this.attackRange = 1;
			break;
		}
		this.health = 100;
		this.player = Logic.playerList.get(player);
		this.x_Coordinate = x_Coordinate;
		this.y_Coordinate = y_Coordinate;
	}

	/**
	 * This method handles the damaging logic. Also it triggers a counterstrike.
	 * 
	 * @param target
	 *            The defending unit.
	 */
	public void damage(Unit target) {
		int damage = this.damageTable[this.type][target.type] * (1 + ((int) Math.random() - 1 / 2));
		target.setHealth(target.getHealth() - (damage * (this.health / 100)));
		if (target.getHealth() <= 0) {
			Logic.removeDefender();
		} else {
			target.counterStrike(this);
		}
	}

	/**
	 * After each attack, if the defender is still alive, it will counter-attack.
	 * This method deals with that logic.
	 * 
	 * @param attacker
	 *            The attacking unit.
	 */
	public void counterStrike(Unit attacker) {
		int damage = this.damageTable[this.type][attacker.type] * (1 + ((int) Math.random() - 1 / 2));
		attacker.setHealth(attacker.getHealth() - (damage * (this.health / 100)));
		if (attacker.getHealth() <= 0) {
			Logic.removeAttacker();
		}
	}

	public String toString() {
		return "Unit " + this.name + "(" + this.health + ")" + " at " + this.x_Coordinate + "/" + this.y_Coordinate
				+ " from " + (this.player);
	}

}