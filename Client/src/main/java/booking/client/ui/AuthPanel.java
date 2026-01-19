package booking.client.ui;

import shareable.LoginCommand;
import shareable.RegisterCommand;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class AuthPanel extends JPanel {
    private static final int FIELD_WIDTH = 520;
    private static final int FIELD_HEIGHT = 40;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JPanel headerPanel;
    private JPanel formPanel;
    private boolean isLoginMode = true;

    private final Consumer<LoginCommand> onLogin;
    private final Consumer<RegisterCommand> onRegister;

    private final ThemeManager themeManager = ThemeManager.getInstance();

    public AuthPanel(Consumer<LoginCommand> onLogin, Consumer<RegisterCommand> onRegister) {
        this.onLogin = onLogin;
        this.onRegister = onRegister;

        setLayout(new BorderLayout());
        setBackground(themeManager.getBackgroundColor());

        buildHeader();
        buildFormContainer();

        themeManager.addThemeChangeListener(isDark -> applyTheme());
        applyTheme();
    }

    private void buildHeader() {
        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(40, 20, 40, 20));

        titleLabel = new JLabel("Welcome to Booking System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        subtitleLabel = new JLabel("Sign in to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(subtitleLabel);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void buildFormContainer() {
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(40, 30, 40, 30));

        createLoginForm();

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(themeManager.getBackgroundColor());
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createLoginForm() {
        formPanel.removeAll();
        isLoginMode = true;
        subtitleLabel.setText("Sign in to your account");

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        styleField(usernameField);
        styleField(passwordField);

        JLabel usernameLabel = buildLabel("Username:");
        JLabel passwordLabel = buildLabel("Password:");

        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(30));

        JPanel buttonPanel = buildButtonPanel();
        ModernButton loginBtn = new ModernButton("Login", themeManager.getAccentGreen(), new Color(100, 195, 100));
        loginBtn.setMaximumSize(new Dimension(FIELD_WIDTH, 45));
        loginBtn.addActionListener(e -> performLogin());
        buttonPanel.add(loginBtn);
        formPanel.add(buttonPanel);
        formPanel.add(Box.createVerticalStrut(20));

        JPanel togglePanel = buildTogglePanel("Don't have an account? ", "Create one", this::switchToRegister);
        formPanel.add(togglePanel);

        formPanel.revalidate();
        formPanel.repaint();
    }

    private void createRegisterForm() {
        formPanel.removeAll();
        isLoginMode = false;
        subtitleLabel.setText("Create your account");

        usernameField = new JTextField();
        emailField = new JTextField();
        fullNameField = new JTextField();
        passwordField = new JPasswordField();
        styleField(usernameField);
        styleField(emailField);
        styleField(fullNameField);
        styleField(passwordField);

        JLabel usernameLabel = buildLabel("Username:");
        JLabel emailLabel = buildLabel("Email:");
        JLabel fullNameLabel = buildLabel("Full Name:");
        JLabel passwordLabel = buildLabel("Password:");

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

        JPanel buttonPanel = buildButtonPanel();
        ModernButton registerBtn = new ModernButton("Create Account", themeManager.getAccentGreen(),
                new Color(100, 195, 100));
        registerBtn.setMaximumSize(new Dimension(FIELD_WIDTH, 45));
        registerBtn.addActionListener(e -> performRegister());
        buttonPanel.add(registerBtn);
        formPanel.add(buttonPanel);
        formPanel.add(Box.createVerticalStrut(20));

        JPanel togglePanel = buildTogglePanel("Already have an account? ", "Sign in", this::switchToLogin);
        formPanel.add(togglePanel);

        formPanel.revalidate();
        formPanel.repaint();
    }

    private JLabel buildLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(themeManager.getTextColor());
        return label;
    }

    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setMaximumSize(new Dimension(FIELD_WIDTH, 45));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setOpaque(false);
        return buttonPanel;
    }

    private JPanel buildTogglePanel(String question, String actionText, Runnable action) {
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        togglePanel.setMaximumSize(new Dimension(FIELD_WIDTH, 40));
        togglePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        togglePanel.setOpaque(false);

        JLabel questionLabel = new JLabel(question);
        questionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        questionLabel.setForeground(themeManager.getTextSecondaryColor());

        JButton link = new JButton(actionText);
        link.setForeground(themeManager.getAccentGreen());
        link.setFont(new Font("Segoe UI", Font.BOLD, 12));
        link.setOpaque(false);
        link.setContentAreaFilled(false);
        link.setBorderPainted(false);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addActionListener(e -> action.run());

        togglePanel.add(questionLabel);
        togglePanel.add(link);
        return togglePanel;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        field.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setBackground(themeManager.getPanelBackground());
        field.setForeground(themeManager.getTextColor());
        field.setCaretColor(themeManager.getTextColor());
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
        clearFields();
    }

    public void resetToLogin() {
        createLoginForm();
        clearFields();
    }

    private void clearFields() {
        if (usernameField != null) {
            usernameField.setText("");
        }
        if (passwordField != null) {
            passwordField.setText("");
        }
        if (emailField != null) {
            emailField.setText("");
        }
        if (fullNameField != null) {
            fullNameField.setText("");
        }
    }

    private void applyTheme() {
        setBackground(themeManager.getBackgroundColor());
        headerPanel.setBackground(themeManager.getHeaderBackground());
        formPanel.setBackground(themeManager.getBackgroundColor());
        titleLabel.setForeground(themeManager.getTextColor());
        subtitleLabel.setForeground(themeManager.getTextSecondaryColor());

        for (Component component : formPanel.getComponents()) {
            if (component instanceof JLabel) {
                component.setForeground(themeManager.getTextColor());
            }
            if (component instanceof JPanel) {
                component.setBackground(themeManager.getBackgroundColor());
            }
        }
        if (usernameField != null) {
            styleField(usernameField);
        }
        if (emailField != null) {
            styleField(emailField);
        }
        if (fullNameField != null) {
            styleField(fullNameField);
        }
        if (passwordField != null) {
            styleField(passwordField);
        }

        revalidate();
        repaint();
    }
}
