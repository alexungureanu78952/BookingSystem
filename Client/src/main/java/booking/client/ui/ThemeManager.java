package booking.client.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    // Eye-friendly green accent color (2010s retro vibe)
    private static final Color ACCENT_GREEN_LIGHT = new Color(76, 175, 80);
    private static final Color ACCENT_GREEN_DARK = new Color(88, 195, 92);

    // Light theme colors
    private static final Color LIGHT_BACKGROUND = new Color(245, 245, 245);
    private static final Color LIGHT_PANEL_BG = new Color(255, 255, 255);
    private static final Color LIGHT_HEADER_BG = new Color(240, 240, 240);
    private static final Color LIGHT_TEXT = new Color(33, 33, 33);
    private static final Color LIGHT_TEXT_SECONDARY = new Color(117, 117, 117);
    private static final Color LIGHT_BORDER = new Color(200, 200, 200);

    // Dark theme colors
    private static final Color DARK_BACKGROUND = new Color(33, 33, 33);
    private static final Color DARK_PANEL_BG = new Color(48, 48, 48);
    private static final Color DARK_HEADER_BG = new Color(40, 40, 40);
    private static final Color DARK_TEXT = new Color(240, 240, 240);
    private static final Color DARK_TEXT_SECONDARY = new Color(189, 189, 189);
    private static final Color DARK_BORDER = new Color(80, 80, 80);

    private static ThemeManager instance;
    private boolean isDarkMode = false;
    private List<ThemeChangeListener> listeners = new ArrayList<>();

    private ThemeManager() {
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public void setDarkMode(boolean dark) {
        if (this.isDarkMode != dark) {
            this.isDarkMode = dark;
            notifyListeners();
        }
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void toggleTheme() {
        setDarkMode(!isDarkMode);
    }

    public Color getAccentGreen() {
        return isDarkMode ? ACCENT_GREEN_DARK : ACCENT_GREEN_LIGHT;
    }

    public Color getBackgroundColor() {
        return isDarkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND;
    }

    public Color getPanelBackground() {
        return isDarkMode ? DARK_PANEL_BG : LIGHT_PANEL_BG;
    }

    public Color getHeaderBackground() {
        return isDarkMode ? DARK_HEADER_BG : LIGHT_HEADER_BG;
    }

    public Color getTextColor() {
        return isDarkMode ? DARK_TEXT : LIGHT_TEXT;
    }

    public Color getTextSecondaryColor() {
        return isDarkMode ? DARK_TEXT_SECONDARY : LIGHT_TEXT_SECONDARY;
    }

    public Color getBorderColor() {
        return isDarkMode ? DARK_BORDER : LIGHT_BORDER;
    }

    public void addThemeChangeListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }

    public void removeThemeChangeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (ThemeChangeListener listener : listeners) {
            listener.onThemeChanged(isDarkMode);
        }
    }

    @FunctionalInterface
    public interface ThemeChangeListener {
        void onThemeChanged(boolean isDarkMode);
    }
}
