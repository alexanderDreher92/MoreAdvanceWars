package base;

/***
 * This class represents a collection of the possible map layouts, and some map
 * functionality. Here you can add custom maps, just follow the layout of the
 * function map01(). The cells of the map should not be changed, but terrain and
 * starting units can be customized. Of course the size can be easily changed
 * too, but be aware, that it has to be squared in the current release, or it
 * will throw errors!
 * 
 * @author Alexander Dreher
 *
 */
public class Maps {

	public static final int TERRAIN_GRASS = 0;
	public static final int TERRAIN_WATER = 1;

	/**
	 * This debugging method prints the given map onto the console.
	 * 
	 * @param map
	 *            The map to be drawn.
	 */
	public static void printMap(Object[][] map) {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				System.out.print(map[i][j] + " ");
			}
			System.out.println("");
		}
		System.out.println("");
	}

	/**
	 * This method represents the layout of the first standard map, "Skirmish".
	 */
	public static void map01() {
		// ----- The Cells of the map -----
		Cell[][] map = new Cell[10][5];

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = new Cell(i, j);
			}
		}

		Logic.map = map;

		// ----- The terrain of the map -----
		int[][] terrain = new int[10][5];

		terrain[4][1] = TERRAIN_WATER;
		terrain[4][2] = TERRAIN_WATER;
		terrain[4][3] = TERRAIN_WATER;
		terrain[3][2] = TERRAIN_WATER;
		terrain[5][2] = TERRAIN_WATER;

		Logic.terrainMap = terrain;

		// ----- The players -----
		Logic.playerList.add(new Player("Andy"));
		Logic.playerList.add(new Player("Flak"));

		// ----- The units initially on the map -----
		Unit[][] units = new Unit[10][5];

		units[0][1] = new Unit(Unit.TYPE_INFANTRY, 0, 0, 1);
		units[1][2] = new Unit(Unit.TYPE_LIGHT_TANK, 0, 1, 2);
		units[0][3] = new Unit(Unit.TYPE_INFANTRY, 0, 0, 3);
		Logic.playerList.get(0).setRemainingUnits(3);

		units[9][1] = new Unit(Unit.TYPE_INFANTRY, 1, 9, 1);
		units[8][2] = new Unit(Unit.TYPE_SCOUT, 1, 8, 2);
		units[9][3] = new Unit(Unit.TYPE_INFANTRY, 1, 9, 3);
		Logic.playerList.get(1).setRemainingUnits(3);

		Logic.unitMap = units;

		// ----- The buildings on the map -----
		Building[][] buildings = new Building[10][5];

		Logic.buildingMap = buildings;
	}

	/**
	 * This method represents the layout of the second standard map, "Never give
	 * up".
	 */
	public static void map02() {
		// ----- The Cells of the map -----
		Cell[][] map = new Cell[15][15];

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = new Cell(i, j);
			}
		}

		Logic.map = map;
		// ----- The terrain of the map -----
		int[][] terrain = new int[15][15];

		terrain[2][2] = TERRAIN_WATER;
		terrain[2][12] = TERRAIN_WATER;
		terrain[12][2] = TERRAIN_WATER;
		terrain[12][12] = TERRAIN_WATER;
		terrain[7][7] = TERRAIN_WATER;

		for (int i = 0; i < 15; i++) {
			terrain[0][i] = TERRAIN_WATER;
			terrain[1][i] = TERRAIN_WATER;
			terrain[13][i] = TERRAIN_WATER;
			terrain[14][i] = TERRAIN_WATER;

			terrain[i][0] = TERRAIN_WATER;
			terrain[i][1] = TERRAIN_WATER;
			terrain[i][13] = TERRAIN_WATER;
			terrain[i][14] = TERRAIN_WATER;
		}

		for (int i = 6; i < 9; i++) {
			terrain[i][2] = TERRAIN_WATER;
			terrain[i][3] = TERRAIN_WATER;
			terrain[i][4] = TERRAIN_WATER;
			terrain[i][10] = TERRAIN_WATER;
			terrain[i][11] = TERRAIN_WATER;
			terrain[i][12] = TERRAIN_WATER;
		}

		Logic.terrainMap = terrain;
		// ----- The players -----
		Logic.playerList.add(new Player("Andy"));
		Logic.playerList.add(new Player("Flak"));

		// ----- The units initially on the map -----
		Unit[][] units = new Unit[15][15];

		units[4][5] = new Unit(Unit.TYPE_INFANTRY, 0, 4, 5);
		units[4][9] = new Unit(Unit.TYPE_INFANTRY, 0, 4, 9);
		Logic.playerList.get(0).setRemainingUnits(2);

		for (int i = 4; i < 11; i++) {
			units[12][i] = new Unit(Unit.TYPE_INFANTRY, 1, 12, i);
		}
		units[11][2] = new Unit(Unit.TYPE_INFANTRY, 1, 11, 2);
		units[11][3] = new Unit(Unit.TYPE_INFANTRY, 1, 11, 3);
		units[11][4] = new Unit(Unit.TYPE_INFANTRY, 1, 11, 4);
		units[11][10] = new Unit(Unit.TYPE_INFANTRY, 1, 11, 10);
		units[11][11] = new Unit(Unit.TYPE_INFANTRY, 1, 11, 11);
		units[11][12] = new Unit(Unit.TYPE_INFANTRY, 1, 11, 12);
		Logic.playerList.get(1).setRemainingUnits(13);

		Logic.unitMap = units;

		// ----- The buildings on the map -----
		Building[][] buildings = new Building[15][15];

		buildings[4][7] = new Building(Building.TYPE_HQ, 0, 4, 7);
		buildings[10][7] = new Building(Building.TYPE_HQ, 1, 10, 7);

		for (int i = 5; i < 10; i++) {
			buildings[3][i] = new Building(Building.TYPE_CITY, 0, 3, i);
			buildings[11][i] = new Building(Building.TYPE_CITY, -1, 11, i);
		}

		buildings[4][2] = new Building(Building.TYPE_BASE, -1, 4, 2);
		buildings[4][12] = new Building(Building.TYPE_BASE, -1, 4, 12);
		buildings[5][5] = new Building(Building.TYPE_BASE, 0, 5, 5);
		buildings[5][9] = new Building(Building.TYPE_BASE, 0, 5, 9);

		buildings[9][5] = new Building(Building.TYPE_BASE, -1, 9, 5);
		buildings[9][9] = new Building(Building.TYPE_BASE, -1, 9, 9);
		buildings[10][2] = new Building(Building.TYPE_BASE, -1, 10, 2);
		buildings[10][12] = new Building(Building.TYPE_BASE, -1, 10, 12);

		Logic.playerList.get(0).setNrOfBuildings(8);
		Logic.playerList.get(1).setNrOfBuildings(3);
		Logic.playerList.get(0).addFunds();
		Logic.playerList.get(1).addFunds();

		Logic.buildingMap = buildings;
	}

	/**
	 * This method represents the layout of the third standard map, "BASE RACE".
	 */
	public static void map03() {
		// ----- The Cells of the map -----
		Cell[][] map = new Cell[9][3];

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = new Cell(i, j);
			}
		}

		Logic.map = map;
		// ----- The terrain of the map -----
		int[][] terrain = new int[9][3];

		Logic.terrainMap = terrain;
		// ----- The players -----
		Logic.playerList.add(new Player("Andy"));
		Logic.playerList.add(new Player("Flak"));

		// ----- The units initially on the map -----
		Unit[][] units = new Unit[9][3];

		Logic.unitMap = units;
		// ----- The buildings on the map -----
		Building[][] buildings = new Building[9][3];

		buildings[0][0] = new Building(Building.TYPE_BASE, 0, 0, 0);
		buildings[2][0] = new Building(Building.TYPE_BASE, -1, 2, 0);
		buildings[4][0] = new Building(Building.TYPE_BASE, -1, 4, 0);
		buildings[6][0] = new Building(Building.TYPE_BASE, -1, 6, 0);
		buildings[8][0] = new Building(Building.TYPE_BASE, 1, 8, 0);

		buildings[1][1] = new Building(Building.TYPE_BASE, 0, 1, 1);
		buildings[3][1] = new Building(Building.TYPE_BASE, -1, 3, 1);
		buildings[5][1] = new Building(Building.TYPE_BASE, -1, 5, 1);
		buildings[7][1] = new Building(Building.TYPE_BASE, 1, 7, 1);

		buildings[0][2] = new Building(Building.TYPE_BASE, 0, 0, 2);
		buildings[2][2] = new Building(Building.TYPE_BASE, -1, 2, 2);
		buildings[4][2] = new Building(Building.TYPE_BASE, -1, 4, 2);
		buildings[6][2] = new Building(Building.TYPE_BASE, -1, 6, 2);
		buildings[8][2] = new Building(Building.TYPE_BASE, 1, 8, 2);

		Logic.playerList.get(0).setNrOfBuildings(3);
		Logic.playerList.get(1).setNrOfBuildings(3);
		Logic.playerList.get(0).addFunds();
		Logic.playerList.get(1).addFunds();

		Logic.buildingMap = buildings;
	}

}