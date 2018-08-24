package base;

import javax.swing.*;
import java.awt.*;

/***
 * This class represents a label with all information about a player. It should
 * usually be shown on the right info side.
 * 
 * @author Alexander Dreher
 *
 */
public class PlayerInfoLabel extends JLabel {

	private static final long serialVersionUID = 1L;
	private JLabel name = new JLabel();
	private JLabel funds = new JLabel();
	private JLabel unitsBuildings = new JLabel();

	/**
	 * The constructor of this class.
	 * 
	 * @param player
	 *            The player, whose information shall be revealed.
	 */
	public PlayerInfoLabel(Player player) {
		this.setLayout(new GridLayout(3, 1));
		this.add(name);
		this.add(funds);
		this.add(unitsBuildings);
		update(player);
	}

	/**
	 * This method updates the label information.
	 * 
	 * @param player
	 *            The player, whose information shall be revealed.
	 */
	public void update(Player player) {
		this.name.setText(player.getName());
		this.funds.setText("Funds: " + player.getFunds());
		this.unitsBuildings
				.setText("Units: " + player.getRemainingUnits() + " / Buildings: " + player.getNrOfBuildings());
	}

}
