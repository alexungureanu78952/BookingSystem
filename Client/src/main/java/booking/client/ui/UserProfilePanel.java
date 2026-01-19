package booking.client.ui;

import shareable.UserDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class UserProfilePanel extends JPanel {
    private UserDTO user;
    private Consumer<Void> onLogout;
    private JLabel usernameLabel;
    private JLabel emailLabel;
    private JLabel fullNameLabel;

    public UserProfilePanel(UserDTO user, Consumer<Void> onLogout) {
        this.user = user;
        this.onLogout = onLogout;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(255, 255, 255));
        setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(63, 81, 181));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel);
        add(headerPanel);

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(255, 255, 255));
        infoPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Username
        JLabel usernameTitle = new JLabel("Username:");
        usernameTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usernameTitle.setForeground(new Color(33, 33, 33));

        usernameLabel = new JLabel(user.username);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        usernameLabel.setForeground(new Color(117, 117, 117));

        // Email
        JLabel emailTitle = new JLabel("Email:");
        emailTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailTitle.setForeground(new Color(33, 33, 33));

        emailLabel = new JLabel(user.email);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emailLabel.setForeground(new Color(117, 117, 117));

        // Full Name
        JLabel fullNameTitle = new JLabel("Full Name:");
        fullNameTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fullNameTitle.setForeground(new Color(33, 33, 33));

        fullNameLabel = new JLabel(user.fullName);
        fullNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fullNameLabel.setForeground(new Color(117, 117, 117));

        infoPanel.add(usernameTitle);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(usernameLabel);
        infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(emailTitle);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(emailLabel);
        infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(fullNameTitle);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(fullNameLabel);
        infoPanel.add(Box.createVerticalStrut(30));

        add(infoPanel);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(255, 255, 255));
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        ModernButton logoutBtn = new ModernButton("Logout", new Color(244, 67, 54), new Color(229, 57, 53));
        logoutBtn.setPreferredSize(new Dimension(120, 40));
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                onLogout.accept(null);
            }
        });

        buttonPanel.add(logoutBtn);
        add(buttonPanel);
        add(Box.createVerticalGlue());

        setPreferredSize(new Dimension(300, 400));
    }
}
