package mobile.substance.sdk.colors;

/**
 * Created by Julian Os on 04.05.2016.
 */
public class ColorPackage {
    private int primaryColor, primaryDarkColor, accentColor, primaryTextColor, secondaryTextColor, activeIconColor, inactiveIconColor, accentIconColor;

    public ColorPackage(int primaryColor, int primaryDarkColor, int accentColor, int primaryTextColor, int secondaryTextColor, int activeIconColor, int inactiveIconColor, int accentIconColor) {
        this.primaryColor = primaryColor;
        this.primaryDarkColor = primaryDarkColor;
        this.accentColor = accentColor;
        this.primaryTextColor = primaryTextColor;
        this.secondaryTextColor = secondaryTextColor;
        this.activeIconColor = activeIconColor;
        this.inactiveIconColor = inactiveIconColor;
        this.accentIconColor = accentIconColor;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public int getPrimaryDarkColor() {
        return primaryDarkColor;
    }

    public int getAccentColor() {
        return accentColor;
    }

    public int getPrimaryTextColor() {
        return primaryTextColor;
    }

    public int getSecondaryTextColor() {
        return secondaryTextColor;
    }

    public int getInactiveIconColor() {
        return inactiveIconColor;
    }

    public int getActiveIconColor() {
        return activeIconColor;
    }

    public int getAccentIconColor() {
        return accentIconColor;
    }
}
