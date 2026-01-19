package booking.client.ui;

import shareable.LoginCommand;
import shareable.RegisterCommand;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class AuthPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JPanel headerPanel;
    private JPanel formPanel;
    private boolean isLoginMode = true;

    private Consumer<LoginCommand> onLogin;
    private Consumer<RegisterCommand> onRegister;

    private final ThemeManager themeManager = ThemeManager.getInstance();

    public AuthPanel(Consumer<LoginCommand> onLogin, Consumer<RegisterCommand> onRegister) {
        this.onLogin = onLogin;
        this.onRegister = onRegister;

        setLayout(new BorderLayout());
        setBackground(themeManager.getBackgroundColor());

        // Header
        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(themeManager.getHeaderBackground());
        headerPanel.setBorder(new EmptyBorder(40, 20, 40, 20));

        titleLabel = new JLabel("Welcome to Booking System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(themeManager.getTextColor());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        subtitleLabel = new JLabel("Sign in to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(themeManager.getTextSecondaryColor());
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(subtitleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(themeManager.getBackgroundColor());
        formPanel.setBorder(new EmptyBorder(40, 30, 40, 30));

        createLoginForm();

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(themeManager.getBackgroundColor());
        scrollPane.getViewport().setBackground(themeManager.getBackgroundColor());

        add(scrollPane, BorderLayout.CENTER);

        themeManager.addThemeChangeListener(isDark -> applyTheme());
        applyTheme();
    }

    private void createLoginForm() {
        formPanel.removeAll();
        isLoginMode = true;

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        usernameField.setMaximumSize(new Dimension(500, 40));
        usernameField.setBackground(themeManager.getPanelBackground());
        usernameField.setForeground(themeManager.getTextColor());

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        passwordField.setMaximumSize(new Dimension(500, 40));
        passwordField.setBackground(themeManager.getPanelBackground());
        passwordField.setForeground(themeManager.getTextColor());

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usernameLabel.setForeground(themeManager.getTextColor());

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(themeManager.getTextColor());

        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(30));

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBackground(themeManager.getBackgroundColor());
        buttonPanel.setMaximumSize(new Dimension(500, 45));

        ModernButton loginBtn = new ModernButton("Sign In", themeManager.getAccentGreen(), new Color(100, 195, 100));
        loginBtn.setMaximumSize(new Dimension(500, 45));
        loginBtn.addActionListener(e -> performLogin());

        buttonPanel.add(loginBtn);
        formPanel.add(buttonPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Toggle to register
        JPanel togglePanel = new JPanel();
        togglePanel.setBackground(themeManager.getBackgroundColor());
        togglePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel questionLabel = new JLabel("Don't have an account? ");
        questionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        questionLabel.setForeground(themeManager.getTextSecondaryColor());

        JButton registerLink = new JButton("Create one");
        registerLink.setForeground(themeManager.getAccentGreen());
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerLink.setOpaque(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setBorderPainted(false);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.addActionListener(e -> switchToRegister());

        togglePanel.add(questionLabel);
        togglePanel.add(registerLink);
        togglePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        formPanel.add(togglePanel);
        formPanel.add(Box.createVerticalGlue());

        formPanel.revalidate();
        formPanel.repaint();
    }

    private void createRegisterForm() {
        formPanel.removeAll();
        isLoginMode = false;

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        usernameField.setMaximumSize(new Dimension(500, 40));
        usernameField.setBackground(themeManager.getPanelBackground());
        usernameField.setForeground(themeManager.getTextColor());

        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        emailField.setMaximumSize(new Dimension(500, 40));
        emailField.setBackground(themeManager.getPanelBackground());
        emailField.setForeground(themeManager.getTextColor());

        fullNameField = new JTextField();
        fullNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fullNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        fullNameField.setMaximumSize(new Dimension(500, 40));
        fullNameField.setBackground(themeManager.getPanelBackground());
        fullNameField.setForeground(themeManager.getTextColor());

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        passwordField.setMaximumSize(new Dimension(500, 40));
        passwordField.setBackground(themeManager.getPanelBackground());
        passwordField.setForeground(themeManager.getTextColor());

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usernameLabel.setForeground(themeManager.getTextColor());

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(themeManager.getTextColor());

        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fullNameLabel.setForeground(themeManager.getTextColor());

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(themeManager.getTextColor());

        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(emailLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(fullNameLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(fullNameField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(30));

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBackground(themeManager.getBackgroundColor());
        buttonPanel.setMaximumSize(new Dimension(500, 45));

        ModernButton registerBtn = new ModernButton("Create Account", themeManager.getAccentGreen(),
                new Color(100, 195, 100));
        registerBtn.setMaximumSize(new Dimension(500, 45));
        registerBtn.addActionListener(e -> performRegister());

        buttonPanel.add(registerBtn);
        formPanel.add(buttonPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Toggle to login
        JPanel togglePanel = new JPanel();
        togglePanel.setBackground(themeManager.getBackgroundColor());
        togglePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel questionLabel = new JLabel("Already have an account? ");
        questionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        questionLabel.setForeground(themeManager.getTextSecondaryColor());

        JButton loginLink = new JButton("Sign in");
        loginLink.setForeground(themeManager.getAccentGreen());
        loginLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginLink.setOpaque(false);
        loginLink.setContentAreaFilled(false);
        loginLink.setBorderPainted(false);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.addActionListener(e -> switchToLogin());

        togglePanel.add(questionLabel);
        togglePanel.add(loginLink);
        togglePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        formPanel.add(togglePanel);
        formPanel.add(Box.createVerticalGlue());

        formPanel.revalidate();
        formPanel.repaint();
    }

    private void switchToLogin() {
        createLoginForm();
    }

    private void switchToRegister() {
        createRegisterForm();
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        onLogin.accept(new LoginCommand(username, password));
    }

    private void performRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        onRegister.accept(new RegisterCommand(username, password, email, fullName));
    }

    public void clearFields() {
        if (usernameField != null)
            usernameField.setText("");
        if (passwordField != null)
            passwordField.setText("");
        if (emailField != null)
            emailField.setText("");
        if (fullNameField != null)
            fullNameField.setText("");
    }

    private void applyTheme() {
        setBackground(themeManager.getBackgroundColor());
        if (headerPanel != null) {
            headerPanel.setBackground(themeManager.getHeaderBackground());
        }
        if (titleLabel != null) {
            titleLabel.setForeground(themeManager.getTextColor());
        }
        if (subtitleLabel != null) {
            subtitleLabel.setForeground(themeManager.getTextSecondaryColor());
        }
        if (formPanel != null) {
            formPanel.setBackground(themeManager.getBackgroundColor());
        }
        revalidate();
        repaint();
    }
}
