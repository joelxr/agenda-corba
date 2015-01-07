package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.FocusManager;
import javax.swing.JTextField;

public class TextField extends JTextField {

	private static final long serialVersionUID = 1L;

	private String placeholder;

	public TextField(String placeholder, int columns) {
		super(columns);
		this.placeholder = placeholder;
	}

	@Override
	protected void paintComponent(java.awt.Graphics g) {
		super.paintComponent(g);

		if (getText().isEmpty() && !(FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this)) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setBackground(Color.gray);
			g2.setFont(getFont().deriveFont(Font.ITALIC));
			g2.drawString(this.placeholder, this.getBorder().getBorderInsets(this).left, this.getHeight() - this.getBorder().getBorderInsets(this).bottom);
			g2.dispose();
		}
	}
}