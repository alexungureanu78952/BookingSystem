package booking.client.ui;

import booking.client.BookingClient;
import shareable.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BookingGUI extends JFrame {
    private BookingClient client;
    private JTabbedPane tabbedPane;
    private JPanel slotsPanel;
    private JPanel bookingsPanel;
    private JLabel statusLabel;
    private JButton menuButton;
    private boolean connected = false;
    private boolean authenticated = false;
    private UserDTO currentUser;

    private List<TimeSlotDTO> currentSlots = new ArrayList<>();
    private List<BookingDTO> currentBookings = new ArrayList<>();

    private CardLayout mainCardLayout;
    private JPanel mainCardPanel;
    private ThemeManager themeManager;

    private enum RequestedDataType {
        SLOTS, BOOKINGS, NONE
    }

    private RequestedDataType lastRequest = RequestedDataType.NONE;

    public BookingGUI() {
        themeManager = ThemeManager.getInstance();

        setTitle("Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        client = new BookingClient();

        // Setup card layout for auth/main views
        mainCardLayout = new CardLayout();
        mainCardPanel = new JPanel(mainCardLayout);
        add(mainCardPanel);

        // Create auth panel
        AuthPanel authPanel = new AuthPanel(
                loginCmd -> handleLogin(loginCmd),
                registerCmd -> handleRegister(registerCmd));
        mainCardPanel.add(authPanel, "AUTH");

        // Create main panel
        JPanel mainPanel = createMainPanel();
        mainCardPanel.add(mainPanel, "MAIN");

        // Show auth first
        mainCardLayout.show(mainCardPanel, "AUTH");

        // Listen to theme changes
        themeManager.addThemeChangeListener(isDark -> updateTheme());

        // Connect to server
        connectToServer();

        setVisible(true);
    }

    private void updateTheme() {
        SwingUtilities.invokeLater(() -> {
            mainCardPanel.setBackground(themeManager.getBackgroundColor());
            mainCardPanel.repaint();

            // Find main panel and update it
            for (Component comp : mainCardPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    updateComponentTheme((JPanel) comp);
                }
            }
        });
    }

    private void updateComponentTheme(JPanel panel) {
        panel.setBackground(themeManager.getBackgroundColor());
        panel.setForeground(themeManager.getTextColor());

        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                updateComponentTheme((JPanel) comp);
            } else if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(themeManager.getTextColor());
            }
        }

        panel.revalidate();
        panel.repaint();
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(themeManager.getBackgroundColor());
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBackground(themeManager.getPanelBackground());
        tabbedPane.setForeground(themeManager.getTextColor());

        // Available Slots tab
        slotsPanel = createSlotsPanel();
        tabbedPane.addTab("Available Slots", slotsPanel);

        // My Bookings tab
        bookingsPanel = createBookingsPanel();
        tabbedPane.addTab("My Bookings", bookingsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(themeManager.getHeaderBackground());
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Booking System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(themeManager.getTextColor());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(themeManager.getHeaderBackground());

        menuButton = new ModernButton("Menu", themeManager.getAccentGreen(),
                new Color(104, 189, 107));
        menuButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menuButton.setPreferredSize(new Dimension(100, 40));
        menuButton.addActionListener(e -> showMenu());

        rightPanel.add(menuButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createSlotsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(themeManager.getBackgroundColor());
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JButton refreshBtn = new ModernButton("Refresh", themeManager.getAccentGreen(),
                new Color(104, 189, 107));
        refreshBtn.setPreferredSize(new Dimension(130, 40));
        refreshBtn.addActionListener(e -> loadAvailableSlots());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(themeManager.getBackgroundColor());
        buttonPanel.add(refreshBtn);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(themeManager.getBackgroundColor());

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        scrollPane.setBackground(themeManager.getBackgroundColor());
        scrollPane.getViewport().setBackground(themeManager.getBackgroundColor());

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.putClientProperty("listPanel", listPanel);

        return mainPanel;
    }

    private JPanel createBookingsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(themeManager.getBackgroundColor());
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JButton refreshBtn = new ModernButton("Refresh", themeManager.getAccentGreen(),
                new Color(104, 189, 107));
        refreshBtn.setPreferredSize(new Dimension(130, 40));
        refreshBtn.addActionListener(e -> loadMyBookings());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(themeManager.getBackgroundColor());
        buttonPanel.add(refreshBtn);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(themeManager.getBackgroundColor());

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        scrollPane.setBackground(themeManager.getBackgroundColor());
        scrollPane.getViewport().setBackground(themeManager.getBackgroundColor());

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.putClientProperty("listPanel", listPanel);

        return mainPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BorderLayout());
        footerPanel.setBackground(themeManager.getBackgroundColor());
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, themeManager.getBorderColor()));

        statusLabel = new JLabel("Disconnected");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(244, 67, 54));

        JButton exitBtn = new ModernButton("Exit", new Color(244, 67, 54),
                new Color(245, 87, 74));
        exitBtn.setPreferredSize(new Dimension(100, 35));
        exitBtn.addActionListener(e -> exit());

        footerPanel.add(statusLabel, BorderLayout.WEST);
        footerPanel.add(exitBtn, BorderLayout.EAST);

        return footerPanel;
    }

    private void showMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(themeManager.getPanelBackground());

        JMenuItem myAccountItem = new JMenuItem("My Account");
        myAccountItem.setBackground(themeManager.getPanelBackground());
        myAccountItem.setForeground(themeManager.getTextColor());
        myAccountItem.addActionListener(e -> showUserProfile());

        JMenuItem themeItem = new JMenuItem("Theme: " + (themeManager.isDarkMode() ? "Dark" : "Light"));
        themeItem.setBackground(themeManager.getPanelBackground());
        themeItem.setForeground(themeManager.getTextColor());
        themeItem.addActionListener(e -> {
            themeManager.toggleTheme();
            showMenu();
        });

        menu.add(myAccountItem);
        menu.addSeparator();
        menu.add(themeItem);

        menu.show(menuButton, 0, menuButton.getHeight());
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                client.connect("localhost", 9090);
                connected = true;
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Connected");
                    statusLabel.setForeground(themeManager.getAccentGreen());
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Connection failed");
                    statusLabel.setForeground(new Color(244, 67, 54));
                    JOptionPane.showMessageDialog(BookingGUI.this,
                            "Failed to connect to server: " + e.getMessage(),
                            "Connection Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void handleLogin(LoginCommand loginCmd) {
        if (!connected) {
            JOptionPane.showMessageDialog(this, "Not connected to server", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new Thread(() -> {
            try {
                client.setOnDataCallback(data -> {
                    if (data instanceof UserDTO) {
                        currentUser = (UserDTO) data;
                        authenticated = true;
                        SwingUtilities.invokeLater(() -> {
                            menuButton.setText(currentUser.username);
                            mainCardLayout.show(mainCardPanel, "MAIN");
                            setupDataCallbackForBookings();
                            loadAvailableSlotsSequential();
                        });
                    }
                });

                client.setOnErrorCallback(msg -> {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, msg, "Login Error", JOptionPane.ERROR_MESSAGE);
                        setupDataCallbackForBookings();
                    });
                });

                client.sendCommand(loginCmd);
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    setupDataCallbackForBookings();
                });
            }
        }).start();
    }

    private void handleRegister(RegisterCommand registerCmd) {
        if (!connected) {
            JOptionPane.showMessageDialog(this, "Not connected to server", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new Thread(() -> {
            try {
                client.setOnSuccessCallback(msg -> {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
                        AuthPanel authPanel = (AuthPanel) mainCardPanel.getComponent(0);
                        authPanel.clearFields();
                        setupDataCallbackForBookings();
                    });
                });

                client.setOnErrorCallback(msg -> {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, msg, "Registration Error", JOptionPane.ERROR_MESSAGE);
                        setupDataCallbackForBookings();
                    });
                });

                client.setOnDataCallback(null);
                client.sendCommand(registerCmd);
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    setupDataCallbackForBookings();
                });
            }
        }).start();
    }

    private void setupDataCallbackForBookings() {
        client.setOnDataCallback(data -> {
            if (data instanceof List<?>) {
                List<?> list = (List<?>) data;
                if (!list.isEmpty()) {
                    Object first = list.get(0);
                    if (first instanceof TimeSlotDTO) {
                        currentSlots = (List<TimeSlotDTO>) list;
                        SwingUtilities.invokeLater(this::displaySlots);
                        lastRequest = RequestedDataType.NONE;
                    } else if (first instanceof BookingDTO) {
                        currentBookings = (List<BookingDTO>) list;
                        SwingUtilities.invokeLater(this::displayBookings);
                        lastRequest = RequestedDataType.NONE;
                    }
                } else {
                    RequestedDataType req = lastRequest;
                    lastRequest = RequestedDataType.NONE;
                    SwingUtilities.invokeLater(() -> {
                        if (req == RequestedDataType.SLOTS) {
                            currentSlots = new ArrayList<>();
                            displaySlots();
                        } else if (req == RequestedDataType.BOOKINGS) {
                            currentBookings = new ArrayList<>();
                            displayBookings();
                        }
                    });
                }
            }
        });
    }

    private void loadAvailableSlotsSequential() {
        new Thread(() -> {
            try {
                lastRequest = RequestedDataType.SLOTS;
                client.sendCommand(new ListSlotsCommand());
                Thread.sleep(500);
                lastRequest = RequestedDataType.BOOKINGS;
                client.sendCommand(new MyBookingsCommand());
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void loadAvailableSlots() {
        if (!connected) {
            JOptionPane.showMessageDialog(this, "Not connected to server", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new Thread(() -> {
            try {
                lastRequest = RequestedDataType.SLOTS;
                client.sendCommand(new ListSlotsCommand());
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error loading slots: " + e.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void displaySlots() {
        JPanel listPanel = (JPanel) slotsPanel.getClientProperty("listPanel");
        listPanel.removeAll();

        if (currentSlots.isEmpty()) {
            JLabel emptyLabel = new JLabel("No available slots");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(themeManager.getTextSecondaryColor());
            listPanel.add(emptyLabel);
        } else {
            for (TimeSlotDTO slot : currentSlots) {
                TimeSlotCard card = new TimeSlotCard(slot, () -> bookSlot(slot));
                listPanel.add(card);
                listPanel.add(Box.createVerticalStrut(10));
            }
        }

        listPanel.add(Box.createVerticalGlue());
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void bookSlot(TimeSlotDTO slot) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Book: " + slot.description + "?",
                "Confirm Booking",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    client.setOnDataCallback(null);

                    Consumer<String> originalSuccessCallback = msg -> {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
                            tabbedPane.setSelectedIndex(1);
                        });
                        client.setOnSuccessCallback(null);
                        client.setOnErrorCallback(null);
                        setupDataCallbackForBookings();
                        new Thread(() -> {
                            try {
                                Thread.sleep(200);
                                loadAvailableSlots();
                                Thread.sleep(300);
                                loadMyBookings();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }).start();
                    };

                    Consumer<String> originalErrorCallback = msg -> {
                        setupDataCallbackForBookings();
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
                        });
                    };

                    client.setOnSuccessCallback(originalSuccessCallback);
                    client.setOnErrorCallback(originalErrorCallback);
                    client.sendCommand(new ReserveCommand(slot.id));
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                    setupDataCallbackForBookings();
                }
            }).start();
        }
    }

    private void loadMyBookings() {
        if (!connected) {
            JOptionPane.showMessageDialog(this, "Not connected to server", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new Thread(() -> {
            try {
                lastRequest = RequestedDataType.BOOKINGS;
                client.sendCommand(new MyBookingsCommand());
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error loading bookings: " + e.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void displayBookings() {
        JPanel listPanel = (JPanel) bookingsPanel.getClientProperty("listPanel");
        listPanel.removeAll();

        if (currentBookings.isEmpty()) {
            JLabel emptyLabel = new JLabel("No bookings yet");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(themeManager.getTextSecondaryColor());
            listPanel.add(emptyLabel);
        } else {
            for (BookingDTO booking : currentBookings) {
                BookingCard card = new BookingCard(booking, () -> cancelBooking(booking));
                listPanel.add(card);
                listPanel.add(Box.createVerticalStrut(10));
            }
        }

        listPanel.add(Box.createVerticalGlue());
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void cancelBooking(BookingDTO booking) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel: " + booking.slotDescription + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    client.setOnDataCallback(null);

                    Consumer<String> originalSuccessCallback = msg -> {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
                        });
                        client.setOnSuccessCallback(null);
                        client.setOnErrorCallback(null);
                        setupDataCallbackForBookings();
                        new Thread(() -> {
                            try {
                                Thread.sleep(200);
                                loadAvailableSlots();
                                Thread.sleep(300);
                                loadMyBookings();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }).start();
                    };

                    Consumer<String> originalErrorCallback = msg -> {
                        setupDataCallbackForBookings();
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
                        });
                    };

                    client.setOnSuccessCallback(originalSuccessCallback);
                    client.setOnErrorCallback(originalErrorCallback);
                    client.sendCommand(new CancelCommand(booking.id));
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                    setupDataCallbackForBookings();
                }
            }).start();
        }
    }

    private void showUserProfile() {
        if (currentUser == null)
            return;

        JDialog profileDialog = new JDialog(this, "My Account", true);
        profileDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        profileDialog.setSize(400, 450);
        profileDialog.setLocationRelativeTo(this);

        EditableUserProfilePanel profilePanel = new EditableUserProfilePanel(
                updatedUser -> {
                    // Handle user info update if needed
                    currentUser = updatedUser;
                },
                v -> {
                    profileDialog.dispose();
                    logout();
                });
        profilePanel.setUser(currentUser);

        profileDialog.add(profilePanel);
        profileDialog.setVisible(true);
    }

    private void logout() {
        authenticated = false;
        currentUser = null;
        currentSlots.clear();
        currentBookings.clear();
        menuButton.setText("Menu");
        mainCardLayout.show(mainCardPanel, "AUTH");
        AuthPanel authPanel = (AuthPanel) mainCardPanel.getComponent(0);
        authPanel.clearFields();
    }

    private void exit() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (connected) {
                    client.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookingGUI());
    }
}
