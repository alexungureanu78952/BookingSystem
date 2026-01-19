package booking.client.ui;

import shareable.UserDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class EditableUserProfilePanel extends JPanel {
    private UserDTO user;
    private Consumer<UserDTO> onSave;
    private Consumer<Void> onLogout;
    private boolean isEditing = false;

    private JLabel usernameLabel;
    private JLabel emailLabel;
    private JLabel fullNameLabel;

    private JTextField emailField;
    private JTextField fullNameField;

    private JButton editButton;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton logoutButton;

    private ThemeManager themeManager;

    public EditableUserProfilePanel(Consumer<UserDTO> onSave, Consumer<Void> onLogout) {
        this.onSave = onSave;
        this.onLogout = onLogout;
        this.themeManager = ThemeManager.getInstance();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        updateTheme();
        themeManager.addThemeChangeListener(isDark -> updateTheme());

        createComponents();
    }

    private void createComponents() {
        // Username section
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usernamePanel.setOpaque(false);
        JLabel usernameTitle = new JLabel("Username:");
        usernameTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usernameLabel = new JLabel();
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        usernamePanel.add(usernameTitle);
        usernamePanel.add(usernameLabel);

        // Email section
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emailPanel.setOpaque(false);
        JLabel emailTitle = new JLabel("Email:");
        emailTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel = new JLabel();
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emailField = new JTextField(20);
        emailField.setVisible(false);
        emailPanel.add(emailTitle);
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);

        // Full Name section
        JPanel fullNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fullNamePanel.setOpaque(false);
        JLabel fullNameTitle = new JLabel("Full Name:");
        fullNameTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fullNameLabel = new JLabel();
        fullNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fullNameField = new JTextField(20);
        fullNameField.setVisible(false);
        fullNamePanel.add(fullNameTitle);
        fullNamePanel.add(fullNameLabel);
        fullNamePanel.add(fullNameField);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setOpaque(false);

        editButton = new ModernButton("Edit", themeManager.getAccentGreen(), 
                new Color(104, 189, 107));
        editButton.setPreferredSize(new Dimension(100, 35));
        editButton.addActionListener(e -> startEditing());

        saveButton = new ModernButton("Save", themeManager.getAccentGreen(), 
                new Color(104, 189, 107));
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.setVisible(false);
        saveButton.addActionListener(e -> saveChanges());

        cancelButton = new ModernButton("Cancel", new Color(200, 200, 200), 
                new Color(220, 220, 220));
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setVisible(false);
        cancelButton.addActionListener(e -> cancelEditing());

        logoutButton = new ModernButton("Logout", new Color(244, 67, 54), 
                new Color(245, 87, 74));
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.addActionListener(e -> logout());

        buttonsPanel.add(editButton);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(logoutButton);

        add(usernamePanel);
        add(Box.createVerticalStrut(10));
        add(emailPanel);
        add(Box.createVerticalStrut(10));
        add(fullNamePanel);
        add(Box.createVerticalStrut(20));
        add(buttonsPanel);
        add(Box.createVerticalGlue());
    }

    public void setUser(UserDTO user) {
        this.user = user;
        updateDisplay();
    }

    private void updateDisplay() {
        if (user != null) {
            usernameLabel.setText(user.username);
            emailLabel.setText(user.email);
            emailField.setText(user.email);
            fullNameLabel.setText(user.fullName);
            fullNameField.setText(user.fullName);
        }
    }

    private void startEditing() {
        isEditing = true;
        emailLabel.setVisible(false);
        fullNameLabel.setVisible(false);
        emailField.setVisible(true);
        fullNameField.setVisible(true);
        editButton.setVisible(false);
        saveButton.setVisible(true);
        cancelButton.setVisible(true);
        revalidate();
        repaint();
    }

    private void saveChanges() {
        if (user != null && onSave != null) {
            UserDTO updatedUser = new UserDTO(
                    user.username,
                    emailField.getText(),
                    fullNameField.getText()
            );
            onSave.accept(updatedUser);
            setUser(updatedUser);
            cancelEditing();
        }
    }

    private void cancelEditing() {
        isEditing = false;
        emailLabel.setVisible(true);
        fullNameLabel.setVisible(true);
        emailField.setVisible(false);
        fullNameField.setVisible(false);
        editButton.setVisible(true);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        updateDisplay();
        revalidate();
        repaint();
    }

    private void logout() {
        if (onLogout != null) {
            onLogout.accept(null);
        }
    }

    private void updateTheme() {
        setBackground(themeManager.getPanelBackground());
        setForeground(themeManager.getTextColor());
    }
}
