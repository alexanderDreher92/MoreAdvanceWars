package base;

import java.awt.GridLayout;

import javax.swing.*;

/***
 * This class represents a label, in which several information about the terrain
 * are shown.
 * 
 * @author Alexander Dreher
 *
 */
public class TerrainInfoLabel extends JLabel {

	private static final long serialVersionUID = 1L;
	private JLabel name = new JLabel();
	private JLabel defenseStrength = new JLabel();
	private JLabel owner = new JLabel();
	private JLabel capture = new JLabel();

	/**
	 * The constructor of this class.
	 */
	public TerrainInfoLabel() {
		this.setLayout(new GridLayout(3, 1));
		this.add(name);
		this.add(defenseStrength);
		this.add(owner);
		this.add(capture);
	}

	/**
	 * This method updates the label information based on the current pointers
	 * location.
	 */
	public void update() {
		Cell pointer = Logic.currentPointer;
		if (pointer != null) {
			int x = pointer.getX_Coordinate();
			int y = pointer.getY_Coordinate();
			String nameValue = "Name: ";
			String defenseValue = "Defense: ";
			String ownerValue = "Owner: ";
			String captureValue = "Captured: ";
			if (Logic.buildingMap[x][y] != null) {
				nameValue = nameValue + Logic.buildingMap[x][y].getName();
				defenseValue = defenseValue + Logic.buildingMap[x][y].getDefenseStrength();
				ownerValue = ownerValue
						+ ((Logic.buildingMap[x][y].getPlayer() != null) ? Logic.buildingMap[x][y].getPlayer() : "/");
				captureValue = captureValue + (20 - Logic.buildingMap[x][y].getHealth()) + "/20";
			} else {
				switch (Logic.terrainMap[x][y]) {
				case 0:
					nameValue = nameValue + "Plain";
					defenseValue = defenseValue + "0";
					break;
				case 1:
					nameValue = nameValue + "Water";
					defenseValue = defenseValue + "0";
					break;
				default:
					break;
				}
				ownerValue = ownerValue + "/";
				captureValue = captureValue + "/";
			}
			this.name.setText(nameValue);
			this.defenseStrength.setText(defenseValue);
			this.owner.setText(ownerValue);
			this.capture.setText(captureValue);
		}
	}
}