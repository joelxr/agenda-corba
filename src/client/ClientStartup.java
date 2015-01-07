package client;

import javax.swing.UIManager;

public class ClientStartup {

	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new MainWindow(args).start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
