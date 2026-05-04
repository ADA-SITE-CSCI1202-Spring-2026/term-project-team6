package gui;

import controllers.AirportController;
import managers.DepotManager;
import managers.SupplyItem;
import aircrafts.Aircraft;

import java.awt.*;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class AirportDashboard extends JFrame {

    private AirportController controller;

    // Save file name
    private static final String SAVE_FILE = "saveAndLoad/airport_state.txt";

    // Fonts
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 22);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 20);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font LIST_FONT = new Font("Arial", Font.PLAIN, 18);
    private static final Font LOG_FONT = new Font("Monospaced", Font.PLAIN, 17);

    // Smaller font for drop-down menu
    private static final Font DROPDOWN_FONT = new Font("Arial", Font.PLAIN, 17);

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
    private JButton saveButton;
    private JButton loadButton;

    // Log area
    private JTextArea logArea;

    // Timer
    private Timer flightArrivalTimer;
    private Random random;

    public AirportDashboard() {
        controller = new AirportController();
        random = new Random();

        setTitle("Skyways");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setupLayout();
        setupTimer();

        refreshUI();

        // Opens maximized without covering the taskbar
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(12, 12));

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 12, 12));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Top row: Queue stays left and bigger, Resources stays right and smaller
        JPanel topRow = createFixedRatioRow(
                createQueuePanel(),
                createResourcePanel(),
                0.62
        );

        // Bottom row: Purchase stays left and smaller, Logger stays right and bigger
        JPanel bottomRow = createFixedRatioRow(
                createSupplyPanel(),
                createLogPanel(),
                0.38
        );

        centerPanel.add(topRow);
        centerPanel.add(bottomRow);

        add(createTopControlPanel(), BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createFixedRatioRow(JPanel leftPanel, JPanel rightPanel, double leftRatio) {
        JPanel row = new JPanel(null) {
            @Override
            public void doLayout() {
                int gap = 12;

                Insets insets = getInsets();

                int availableWidth = getWidth() - insets.left - insets.right;
                int availableHeight = getHeight() - insets.top - insets.bottom;

                int leftWidth = (int) Math.round((availableWidth - gap) * leftRatio);
                int rightWidth = (availableWidth - gap) - leftWidth;

                // Left panel
                leftPanel.setBounds(
                        insets.left,
                        insets.top,
                        leftWidth,
                        availableHeight
                );

                // Right panel
                rightPanel.setBounds(
                        insets.left + leftWidth + gap,
                        insets.top,
                        rightWidth,
                        availableHeight
                );
            }

            @Override
            public Dimension getMinimumSize() {
                return new Dimension(0, 0);
            }
        };

        row.add(leftPanel);
        row.add(rightPanel);

        return row;
    }

    private Border createPanelBorder(String title) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(title);
        titledBorder.setTitleFont(TITLE_FONT);

        return BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );
    }

    private JPanel createTopControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));

        saveButton = new JButton("Save State");
        loadButton = new JButton("Load State");

        saveButton.setFont(BUTTON_FONT);
        loadButton.setFont(BUTTON_FONT);

        saveButton.addActionListener(e -> {
            boolean saved = controller.saveState(SAVE_FILE);
            refreshUI();

            if (saved) {
                JOptionPane.showMessageDialog(this, "State saved to " + SAVE_FILE);
            } else {
                JOptionPane.showMessageDialog(this, "Save failed. Check system log.");
            }
        });

        loadButton.addActionListener(e -> {
            // Stop timer while loading so no new flight appears during file restoration
            if (flightArrivalTimer != null) {
                flightArrivalTimer.stop();
            }

            boolean loaded = controller.loadState(SAVE_FILE);
            refreshUI();

            // Start timer again after loading
            if (flightArrivalTimer != null) {
                flightArrivalTimer.start();
            }

            if (loaded) {
                JOptionPane.showMessageDialog(this, "State loaded from " + SAVE_FILE);
            } else {
                JOptionPane.showMessageDialog(this, "Load failed. Check system log.");
            }
        });

        panel.add(saveButton);
        panel.add(loadButton);

        return panel;
    }

    private JPanel createQueuePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(createPanelBorder("Holding Pattern - Flight Queue"));

        queueListModel = new DefaultListModel<>();
        queueList = new JList<>(queueListModel);
        queueList.setFont(LIST_FONT);
        queueList.setFixedCellHeight(32);

        clearFlightButton = new JButton("Clear Next Flight");
        clearFlightButton.setFont(BUTTON_FONT);

        clearFlightButton.addActionListener(e -> {
            controller.processNextFlight();
            refreshUI();
        });

        panel.add(new JScrollPane(queueList), BorderLayout.CENTER);
        panel.add(clearFlightButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createResourcePanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 8, 8));
        panel.setBorder(createPanelBorder("Terminal Depot - Resources"));

        budgetLabel = new JLabel();
        fuelLabel = new JLabel();
        mealsLabel = new JLabel();
        cartsLabel = new JLabel();

        budgetLabel.setFont(LABEL_FONT);
        fuelLabel.setFont(LABEL_FONT);
        mealsLabel.setFont(LABEL_FONT);
        cartsLabel.setFont(LABEL_FONT);

        budgetLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        fuelLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        mealsLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        cartsLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        panel.add(budgetLabel);
        panel.add(fuelLabel);
        panel.add(mealsLabel);
        panel.add(cartsLabel);

        return panel;
    }

    private JPanel createSupplyPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 8, 8));
        panel.setBorder(createPanelBorder("Supply Requisition"));

        JLabel selectSupplyLabel = new JLabel("Select supply to purchase:");
        selectSupplyLabel.setFont(LABEL_FONT);

        supplyDropdown = new JComboBox<>(SupplyItem.values());

        // Smaller font for the selected item in the drop-down
        supplyDropdown.setFont(DROPDOWN_FONT);

        // Prevents combo box preferred size from changing after selecting items
        supplyDropdown.setPrototypeDisplayValue(SupplyItem.JET_FUEL);

        // Smaller font for the opened drop-down list too
        supplyDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                Component component = super.getListCellRendererComponent(
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus
                );

                component.setFont(DROPDOWN_FONT);
                return component;
            }
        });

        purchaseButton = new JButton("Purchase Cargo");
        purchaseButton.setFont(BUTTON_FONT);

        purchaseButton.addActionListener(e -> {
            SupplyItem selectedItem = (SupplyItem) supplyDropdown.getSelectedItem();

            if (selectedItem != null) {
                controller.purchaseSupply(selectedItem);
                refreshUI();
            }
        });

        panel.add(selectSupplyLabel);
        panel.add(supplyDropdown);
        panel.add(purchaseButton);

        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(createPanelBorder("Dispatch Radio - System Log"));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(LOG_FONT);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setMargin(new Insets(8, 8, 8, 8));

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