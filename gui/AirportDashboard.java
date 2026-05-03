package gui;

import Controllers.AirportController;
import Managers.DepotManager;
import Managers.SupplyItem;
import aircrafts.Aircraft;

import java.awt.*;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class AirportDashboard extends JFrame {

    private AirportController controller;

    // Fonts
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 18);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font LIST_FONT = new Font("Arial", Font.PLAIN, 17);
    private static final Font LOG_FONT = new Font("Monospaced", Font.PLAIN, 16);

    // Queue display
    private DefaultListModel<String> queueListModel;
    private JList<String> queueList;

    // Resource labels
    private JLabel budgetLabel;
    private JLabel fuelLabel;
    private JLabel mealsLabel;
    private JLabel cartsLabel;

    // Supply chain controls
    private JComboBox<SupplyItem> supplyDropdown;
    private JButton purchaseButton;

    // Action buttons
    private JButton clearFlightButton;

    // Log area
    private JTextArea logArea;

    // Timer
    private Timer flightArrivalTimer;
    private Random random;

    public AirportDashboard() {
        controller = new AirportController();
        random = new Random();

        setTitle("Skyways Airport Dispatch Dashboard");

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setupLayout();
        setupTimer();

        refreshUI();
    }

    private void setupLayout() {
        setLayout(new BorderLayout(15, 15));

        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(createQueuePanel());
        mainPanel.add(createResourcePanel());
        mainPanel.add(createSupplyPanel());
        mainPanel.add(createLogPanel());

        add(mainPanel, BorderLayout.CENTER);
    }

    private Border createPanelBorder(String title) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(title);
        titledBorder.setTitleFont(TITLE_FONT);

        return BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
    }

    private JPanel createQueuePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(createPanelBorder("Holding Pattern - Flight Queue"));

        queueListModel = new DefaultListModel<>();
        queueList = new JList<>(queueListModel);
        queueList.setFont(LIST_FONT);
        queueList.setFixedCellHeight(35);

        clearFlightButton = new JButton("Clear Next Flight");
        clearFlightButton.setFont(BUTTON_FONT);
        clearFlightButton.setPreferredSize(new Dimension(200, 55));

        clearFlightButton.addActionListener(e -> {
            controller.processNextFlight();
            refreshUI();
        });

        panel.add(new JScrollPane(queueList), BorderLayout.CENTER);
        panel.add(clearFlightButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createResourcePanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(createPanelBorder("Terminal Depot - Resources"));

        budgetLabel = new JLabel();
        fuelLabel = new JLabel();
        mealsLabel = new JLabel();
        cartsLabel = new JLabel();

        budgetLabel.setFont(LABEL_FONT);
        fuelLabel.setFont(LABEL_FONT);
        mealsLabel.setFont(LABEL_FONT);
        cartsLabel.setFont(LABEL_FONT);

        budgetLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        fuelLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mealsLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        cartsLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        panel.add(budgetLabel);
        panel.add(fuelLabel);
        panel.add(mealsLabel);
        panel.add(cartsLabel);

        return panel;
    }

    private JPanel createSupplyPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(createPanelBorder("Supply Requisition"));

        JLabel selectLabel = new JLabel("Select supply to purchase:");
        selectLabel.setFont(LABEL_FONT);

        supplyDropdown = new JComboBox<>(SupplyItem.values());
        supplyDropdown.setFont(LABEL_FONT);

        purchaseButton = new JButton("Purchase Cargo");
        purchaseButton.setFont(BUTTON_FONT);

        purchaseButton.addActionListener(e -> {
            SupplyItem selectedItem = (SupplyItem) supplyDropdown.getSelectedItem();

            if (selectedItem != null) {
                controller.purchaseSupply(selectedItem);
                refreshUI();
            }
        });

        panel.add(selectLabel);
        panel.add(supplyDropdown);
        panel.add(purchaseButton);

        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(createPanelBorder("Dispatch Radio - System Log"));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(LOG_FONT);
        logArea.setMargin(new Insets(10, 10, 10, 10));

        panel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        return panel;
    }

    private void setupTimer() {
        flightArrivalTimer = new Timer(randomDelay(), e -> {
            controller.generateRandomFlight();
            refreshUI();

            Timer timer = (Timer) e.getSource();
            timer.setDelay(randomDelay());
        });

        flightArrivalTimer.start();
    }

    private int randomDelay() {
        return 2000 + random.nextInt(3001);
        // 2000 ms to 5000 ms
    }

    private void refreshUI() {
        refreshQueue();
        refreshResources();
        refreshLogs();
    }

    private void refreshQueue() {
        queueListModel.clear();

        List<Aircraft> aircraftList = controller.getQueueManager().toList();

        for (Aircraft aircraft : aircraftList) {
            queueListModel.addElement(
                    aircraft.getAircraftType()
                            + " | Flight: " + aircraft.getFlightNumber()
                            + " | Fuel: " + aircraft.getRequiredFuel()
                            + " | Meals: " + aircraft.getRequiredMeals()
                            + " | Carts: " + aircraft.getRequiredCarts()
                            + " | Reward: $" + aircraft.getRewardAmount()
            );
        }
    }

    private void refreshResources() {
        DepotManager depot = controller.getDepotManager();

        budgetLabel.setText("Budget: $" + depot.getBudget());
        fuelLabel.setText("Jet Fuel: " + depot.getResourceAmount(SupplyItem.JET_FUEL));
        mealsLabel.setText("Meals: " + depot.getResourceAmount(SupplyItem.MEALS));
        cartsLabel.setText("Luggage Carts: " + depot.getResourceAmount(SupplyItem.CART));
    }

    private void refreshLogs() {
        List<String> logs = controller.getLogger().getAllLogs();

        StringBuilder sb = new StringBuilder();

        for (String log : logs) {
            sb.append(log).append("\n");
        }

        logArea.setText(sb.toString());
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}