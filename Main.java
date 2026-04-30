import gui.AirportDashboard;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AirportDashboard dashboard = new AirportDashboard();
            dashboard.setVisible(true);
        });
    }
}