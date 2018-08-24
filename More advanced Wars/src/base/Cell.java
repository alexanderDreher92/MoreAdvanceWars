package base;

import javax.swing.*;

import java.awt.Color;
import java.awt.event.*;

/***
 * This class represents a single field on the game map. It extends JButton for
 * clicking functionality. Note: It has two visited booleans for search
 * iteration within search iteration.
 * 
 * @author Alexander Dreher
 *
 */
public class Cell extends JButton {

	private static final long serialVersionUID = 1L;
	private int x_Coordinate;
	private int y_Coordinate;
	private boolean isBlocked = false;
	private boolean isVisited = false;
	private boolean isVisited2 = false;

	/**
	 * The constructor of this class.
	 * 
	 * @param x
	 *            The x coordinate of the new Cell.
	 * @param y
	 *            The y coordinate of the new Cell.
	 */
	public Cell(int x, int y) {
		super();
		this.setForeground(Color.WHITE);
		this.x_Coordinate = x;
		this.y_Coordinate = y;

		this.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Cell source = (Cell) event.getSource();
				Logic.buttonClicked(source);
			}
		});
	}

	public int getX_Coordinate() {
		return this.x_Coordinate;
	}

	public int getY_Coordinate() {
		return this.y_Coordinate;
	}

	public String toString() {
		return "Cell " + this.x_Coordinate + "/" + this.y_Coordinate + " ; Visited:" + this.isVisited + " ; Blocked:"
				+ this.isBlocked;
	}

	public boolean equals(Cell otherCell) {
		if (this.x_Coordinate == otherCell.x_Coordinate && this.y_Coordinate == otherCell.y_Coordinate) {
			return true;
		} else {
			return false;
		}
	}

	public void block() {
		this.isBlocked = true;
	}

	public void unblock() {
		this.isBlocked = false;
	}

	public boolean isBlocked() {
		return this.isBlocked;
	}

	public void visit() {
		this.isVisited = true;
	}

	public void unvisit() {
		this.isVisited = false;
	}

	public boolean isVisited() {
		return this.isVisited;
	}

	public void visit2() {
		this.isVisited2 = true;
	}

	public void unvisit2() {
		this.isVisited2 = false;
	}

	public boolean isVisited2() {
		return this.isVisited2;
	}

}