package mobile.substance.sdk.colors;

/**
 * Created by Julian Os on 04.05.2016.
 */
public class ColorPackage {
    private int primaryColor, primaryDarkColor, textColor, secondaryTextColor,
        accentColor, accentTextColor, accentSecondaryTextColor;

    public ColorPackage(int primaryColor, int primaryDarkColor, int textColor, int secondaryTextColor,
                        int accentColor, int accentTextColor, int accentSecondaryTextColor) {

        //Main Colors
        this.primaryColor = primaryColor;
        this.primaryDarkColor = primaryDarkColor;
        this.textColor = textColor;
        this.secondaryTextColor = secondaryTextColor;

        //Accent Colors
        this.accentColor = accentColor;
        this.accentTextColor = accentTextColor;
        this.accentSecondaryTextColor = accentSecondaryTextColor;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Primary Colors
    ///////////////////////////////////////////////////////////////////////////

    public int getPrimaryColor() {
        return primaryColor;
    }

    public int getPrimaryDarkColor() {
        return primaryDarkColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getSecondaryTextColor() {
        return secondaryTextColor;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Accent Colors
    ///////////////////////////////////////////////////////////////////////////

    public int getAccentColor() {
        return accentColor;
    }

    public int getAccentSecondaryTextColor() {
        return accentSecondaryTextColor;
    }

    public int getAccentTextColor() {
        return accentTextColor;
    }
}
