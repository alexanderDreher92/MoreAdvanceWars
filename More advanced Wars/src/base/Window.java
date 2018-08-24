package base;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/***
 * This class represents the main playing frame and its contents.
 * 
 * @author Alexander Dreher
 *
 */
public class Window extends JFrame {

	private static final long serialVersionUID = 1L;

	private JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private JPanel infoSide = new JPanel();
	private JPanel combatSide = new JPanel();
	private JTextField input = new JTextField();
	private JButton attack = new JButton("Action");
	private JButton endTurn = new JButton("End turn");
	private JLabel currentPlayerLabel = new JLabel("Current player: " + (Logic.getCurrentPlayer() + 1));
	private JMenuBar menubar = new JMenuBar();
	private JMenu infoMenu = new JMenu("Info");
	private JMenuItem help = new JMenuItem("Help");
	private JMenuItem version = new JMenuItem("Version");
	private PlayerInfoLabel player1Info;
	private PlayerInfoLabel player2Info;
	private TerrainInfoLabel terrainInfo = new TerrainInfoLabel();
	private BuildingChoiceLabel buildingChoice = new BuildingChoiceLabel();

	public JLabel getCurrentPlayerLabel() {
		return this.currentPlayerLabel;
	}

	/**
	 * The constructor of this class.
	 */
	public Window() {
		super();
		Logic.initializeMap();
		initializeGraphics();
		updateMapGraphics();
	}

	/**
	 * This method enables other classes to make the textfield grab input.
	 */
	public void grabFocusOnStart() {
		input.grabFocus();
	}

	/**
	 * This method handles all logic for initializing the frame and its contents.
	 */
	private void initializeGraphics() {
		this.setTitle("More Advanced Wars");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(900, 500);

		// ----- Logic for the game field -----
		combatSide.setLayout(new GridLayout(Logic.map.length, Logic.map[0].length));
		for (int i = 0; i < Logic.map.length; i++) {
			for (int j = 0; j < Logic.map[i].length; j++) {
				combatSide.add(Logic.map[i][j]);
			}
		}

		// ----- Logic for the info side -----
		prepareInput();
		infoSide.setLayout(new GridLayout(4, 2));
		infoSide.add(input);
		infoSide.add(currentPlayerLabel);
		attack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Logic.actionButton(false);
				updateMapGraphics();
			}
		});
		infoSide.add(attack);
		endTurn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Logic.endTurnButton();
				updateMapGraphics();
			}
		});
		infoSide.add(endTurn);

		player1Info = new PlayerInfoLabel(Logic.playerList.get(0));
		player2Info = new PlayerInfoLabel(Logic.playerList.get(1));
		infoSide.add(player1Info);
		infoSide.add(player2Info);
		infoSide.add(terrainInfo);
		infoSide.add(buildingChoice);

		// ----- Delimiter -----
		split.setLeftComponent(combatSide);
		split.setRightComponent(infoSide);

		split.setDividerLocation(500);
		split.setEnabled(false);

		this.add(split);

		// ----- Menubar stuff -----
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openHelp();
			}
		});
		version.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openVersion();
			}
		});
		infoMenu.add(help);
		infoMenu.add(version);
		menubar.add(infoMenu);
		this.setJMenuBar(menubar);
	}

	/**
	 * This method creates the continuous keyboard availability and maps the
	 * commands from the keys to the methods and logic.
	 */
	private void prepareInput() {
		input.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				JTextField source = (JTextField) e.getSource();
				if (Logic.isPlaying()) {
					source.grabFocus();
				}
			}
		});

		input.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode();
				// Main.log(code);
				switch (code) {
				case 37: // Left Arrow Key
					movePointerTo(0, -1);
					break;
				case 38: // Up Arrow Key
					movePointerTo(-1, 0);
					break;
				case 39: // Right Arrow Key
					movePointerTo(0, 1);
					break;
				case 40: // Down Arrow Key
					movePointerTo(1, 0);
					break;
				case 10: // Enter key
					Logic.enterKey();
					break;
				case 8: // Backspace key
					Logic.backspaceKey(false);
					break;
				case 65: // a key
					Logic.actionButton(false);
					break;
				case 69: // e key
					Logic.endTurnButton();
					break;
				default:
					break;
				}
				input.setText("");
			}

			public void keyReleased(KeyEvent e) {
				input.setText("");
			}

			public void keyTyped(KeyEvent e) {
				input.setText("");
			}
		});
	}

	/**
	 * This method moves the current pointed-to field for selection to the given
	 * coordinates. This can be called for direct neighbors of the current pointer.
	 * 
	 * @param xChange
	 *            The change in the horizontal direction.
	 * @param yChange
	 *            The change in the vertical direction.
	 */
	private void movePointerTo(int xChange, int yChange) {
		int newX = Logic.currentPointer.getX_Coordinate() + xChange;
		int newY = Logic.currentPointer.getY_Coordinate() + yChange;

		if (Logic.isValidMapField(newX, newY, Logic.map)) {

			Logic.setCurrentPointer(newX, newY);

			updateMapGraphics();
		}
	}

	/**
	 * This method redraws the visible map and should be called after each action
	 * that changes the map.
	 */
	public void updateMapGraphics() {
		if (Logic.isPlaying()) {
			Cell[][] map = Logic.map;
			int[][] terrainMap = Logic.terrainMap;
			Unit[][] unitMap = Logic.unitMap;

			// ----- Deleting everything -----
			for (int i = 0; i < map.length; i++) {
				for (int j = 0; j < map[i].length; j++) {
					map[i][j].setBackground(null);
					if (Main.showGrid() == false) {
						map[i][j].setBorder(null);
					}
				}
			}

			// ----- Redrawing terrain -----
			for (int i = 0; i < terrainMap.length; i++) {
				for (int j = 0; j < terrainMap[i].length; j++) {
					switch (terrainMap[i][j]) {
					case Maps.TERRAIN_GRASS:
						map[i][j].setBackground(Color.GREEN);
						break;
					case Maps.TERRAIN_WATER:
						map[i][j].setBackground(Color.BLUE);
						break;
					default:
						break;
					}
				}
			}

			// ----- Redrawing buildings -----
			for (int i = 0; i < Logic.buildingMap.length; i++) {
				for (int j = 0; j < Logic.buildingMap[i].length; j++) {
					if (Logic.buildingMap[i][j] != null) {
						map[i][j].setText("" + Logic.buildingMap[i][j].getName());
						switch (Logic.playerList.indexOf(Logic.buildingMap[i][j].getPlayer())) {
						case -1:
							map[i][j].setBackground(Color.GRAY);
							break;
						case 0:
							map[i][j].setBackground(Player.player1Color);
							break;
						case 1:
							map[i][j].setBackground(Player.player2Color);
							break;
						default:
							break;
						}
					}
				}
			}

			// ----- Redrawing units -----
			for (int i = 0; i < unitMap.length; i++) {
				for (int j = 0; j < unitMap[i].length; j++) {
					if (unitMap[i][j] != null) {
						int health = (int) unitMap[i][j].getHealth() / 10 + 1;
						map[i][j].setText(Logic.unitMap[i][j].getName() + " \n" + (health >= 10 ? "" : health));
						switch (Logic.playerList.indexOf(unitMap[i][j].getPlayer())) {
						case 0:
							map[i][j].setBackground(Player.player1Color);
							break;
						case 1:
							map[i][j].setBackground(Player.player2Color);
							break;
						case 2:
							map[i][j].setBackground(Player.player3Color);
							break;
						case 3:
							map[i][j].setBackground(Player.player4Color);
							break;
						default:
							break;
						}
					}
				}
			}

			// ----- Current selection -----
			if (Logic.unitIsSelected()
					&& Logic.unitMap[Logic.currentPosition.getX_Coordinate()][Logic.currentPosition.getY_Coordinate()]
							.canMove()) {
				Logic.highlightMovement();
			}

			// Current pointer
			if (Logic.currentPointer != null) {
				Logic.currentPointer.setBorder(new EtchedBorder(Color.GRAY, Color.WHITE));
			}

			// ----- Changes to playerInfo -----
			player1Info.update(Logic.playerList.get(0));
			player2Info.update(Logic.playerList.get(1));

			// ----- Changes to terrainInfo -----
			terrainInfo.update();
		}
	}

	/**
	 * This method opens a window with the help text.
	 */
	private void openHelp() {
		JFrame helpFrame = new JFrame();
		helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		helpFrame.setLocation(this.getX() + 900, this.getY());
		helpFrame.setSize(400, 300);
		helpFrame.setTitle("Help");
		JTextArea label = new JTextArea(Help.helptext);
		label.setEditable(false);
		helpFrame.add(label);
		helpFrame.setVisible(true);
	}

	/**
	 * This method opens a window with the version info.
	 */
	private void openVersion() {
		JFrame versionFrame = new JFrame();
		versionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		versionFrame.setLocation(this.getX() + 900, this.getY() + 300);
		versionFrame.setSize(200, 100);
		versionFrame.setTitle("Version");
		JLabel label = new JLabel("Version Alpha 1.0");
		versionFrame.add(label);
		versionFrame.setVisible(true);
	}

}