package base;

import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class represents the label, in which a player can choose, which unit he
 * wants to build.
 * 
 * @author Alexander Dreher
 *
 */
public class BuildingChoiceLabel extends JLabel {

	private static final long serialVersionUID = 1L;
	private JButton infantry = new JButton("Infantry (" + Unit.COST_INFANTRY + ")");
	private JButton scout = new JButton("Scout (" + Unit.COST_SCOUT + ")");
	private JButton lightTank = new JButton("Light Tank (" + Unit.COST_LIGHT_TANK + ")");

	/**
	 * The constructor of this class.
	 */
	public BuildingChoiceLabel() {
		setActionListeners();
		// design
		this.setLayout(new GridLayout(3, 1));
		this.add(infantry);
		this.add(scout);
		this.add(lightTank);
	}

	/**
	 * This auxiliary method sets the ActionListeners to each of the buttons.
	 */
	private void setActionListeners() {
		infantry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Logic.buyUnit(0, false);
			}
		});
		scout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Logic.buyUnit(1, false);
			}
		});
		lightTank.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Logic.buyUnit(2, false);
			}
		});

	}

}
