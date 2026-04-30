package gui;

import Controllers.AirportController;
import Managers.DepotManager;
import Managers.SupplyItem;
import aircrafts.Aircraft;

import java.awt.*;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class AirportDashboard extends JFrame {

    private AirportController controller;

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
        setSize(2000, 1300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setupLayout();
        setupTimer();

        refreshUI();
    }

    private void setupLayout() {
        setLayout(new BorderLayout(12, 12));

        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 12, 12));

        mainPanel.add(createQueuePanel());
        mainPanel.add(createResourcePanel());
        mainPanel.add(createSupplyPanel());
        mainPanel.add(createLogPanel());

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createQueuePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Holding Pattern - Flight Queue"));

        queueListModel = new DefaultListModel<>();
        queueList = new JList<>(queueListModel);

        clearFlightButton = new JButton("Clear Next Flight");

        clearFlightButton.addActionListener(e -> {
            controller.processNextFlight();
            refreshUI();
        });

        panel.add(new JScrollPane(queueList), BorderLayout.CENTER);
        panel.add(clearFlightButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createResourcePanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Terminal Depot - Resources"));

        budgetLabel = new JLabel();
        fuelLabel = new JLabel();
        mealsLabel = new JLabel();
        cartsLabel = new JLabel();

        panel.add(budgetLabel);
        panel.add(fuelLabel);
        panel.add(mealsLabel);
        panel.add(cartsLabel);

        return panel;
    }

    private JPanel createSupplyPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Supply Requisition"));

        supplyDropdown = new JComboBox<>(SupplyItem.values());
        purchaseButton = new JButton("Purchase Cargo");

        purchaseButton.addActionListener(e -> {
            SupplyItem selectedItem = (SupplyItem) supplyDropdown.getSelectedItem();

            if (selectedItem != null) {
                controller.purchaseSupply(selectedItem);
                refreshUI();
            }
        });

        panel.add(new JLabel("Select supply to purchase:"));
        panel.add(supplyDropdown);
        panel.add(purchaseButton);

        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Dispatch Radio - System Log"));

        logArea = new JTextArea();
        logArea.setEditable(false);

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