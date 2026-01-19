package booking.client.ui;

import shareable.BookingDTO;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class BookingCard extends JPanel {
    private BookingDTO booking;
    private Runnable onCancel;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public BookingCard(BookingDTO booking, Runnable onCancel) {
        this.booking = booking;
        this.onCancel = onCancel;

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 255, 255));
        setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        setPreferredSize(new Dimension(0, 120));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Left panel - info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(255, 255, 255));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(booking.slotDescription);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(33, 33, 33));

        String timeText = booking.slotTime.format(DATE_FORMATTER);
        JLabel timeLabel = new JLabel(timeText);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(new Color(117, 117, 117));

        String bookedAtText = "Booked: " + booking.bookedAt.format(DATE_FORMATTER);
        JLabel bookedLabel = new JLabel(bookedAtText);
        bookedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bookedLabel.setForeground(new Color(158, 158, 158));

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(timeLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(bookedLabel);
        infoPanel.add(Box.createVerticalGlue());

        // Right panel - button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(new Color(255, 255, 255));

        ModernButton cancelBtn = new ModernButton("Cancel", new Color(244, 67, 54), new Color(229, 57, 53));
        cancelBtn.setPreferredSize(new Dimension(130, 40));
        cancelBtn.addActionListener(e -> onCancel.run());
        buttonPanel.add(cancelBtn);

        add(infoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
    }
}
