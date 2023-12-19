package qupath.ext.omero.core.entities.image;

import java.util.Objects;

/**
 * Represents several settings about a channel.
 */
public class ChannelSettings {

    private final String name;
    private final double minDisplayRange;
    private final double maxDisplayRange;
    private final String rgbColorHex;

    /**
     * Create a new channel settings
     *
     * @param name  the name of the channel
     * @param minDisplayRange  the minimum table lookup value for this channel
     * @param maxDisplayRange  the maximum table lookup value for this channel
     * @param rgbColorHex  the RGB color of this channel in the hexadecimal format (e.g. FF0000 for red)
     */
    public ChannelSettings(String name, double minDisplayRange, double maxDisplayRange, String rgbColorHex) {
        this.name = name;
        this.minDisplayRange = minDisplayRange;
        this.maxDisplayRange = maxDisplayRange;
        this.rgbColorHex = rgbColorHex;
    }

    /**
     * Create a new channel settings
     *
     * @param minDisplayRange  the minimum table lookup value for this channel
     * @param maxDisplayRange  the maximum table lookup value for this channel
     * @param rgbColorHex  the RGB color of this channel in the hexadecimal format (e.g. FF0000 for red)
     */
    public ChannelSettings(double minDisplayRange, double maxDisplayRange, String rgbColorHex) {
        this("", minDisplayRange, maxDisplayRange, rgbColorHex);
    }

    @Override
    public String toString() {
        return String.format("Channel %s of color %s, from %f to %f", name, rgbColorHex, minDisplayRange, maxDisplayRange);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof ChannelSettings channelSettings))
            return false;
        return channelSettings.name.equals(name) &&
                channelSettings.minDisplayRange == minDisplayRange &&
                channelSettings.maxDisplayRange == maxDisplayRange &&
                channelSettings.rgbColorHex.equals(rgbColorHex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, minDisplayRange, maxDisplayRange, rgbColorHex);
    }

    /**
     * @return the name of this channel
     */
    public String getName() {
        return name;
    }

    /**
     * @return the minimum table lookup value for this channel
     */
    public double getMinDisplayRange() {
        return minDisplayRange;
    }

    /**
     * @return the maximum table lookup value for this channel
     */
    public double getMaxDisplayRange() {
        return maxDisplayRange;
    }

    /**
     * @return the RGB color of this channel in the hexadecimal format (e.g. FF0000 for red)
     */
    public String getRgbColorHex() {
        return rgbColorHex;
    }
}
