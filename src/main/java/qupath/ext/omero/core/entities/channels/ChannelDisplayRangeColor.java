package qupath.ext.omero.core.entities.channels;

import qupath.lib.common.ColorTools;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents the display range and the color of a channel.
 */
public class ChannelDisplayRangeColor {

    private final long minDisplayRange;
    private final long maxDisplayRange;
    private final int rgbColor;

    /**
     * Create a new channel display range color
     *
     * @param minDisplayRange  the minimum table lookup value for this channel
     * @param maxDisplayRange  the maximum table lookup value for this channel
     * @param rgbColor  the color of this channel
     */
    public ChannelDisplayRangeColor(long minDisplayRange, long maxDisplayRange, int rgbColor) {
        this.minDisplayRange = minDisplayRange;
        this.maxDisplayRange = maxDisplayRange;
        this.rgbColor = rgbColor;
    }

    /**
     * Return a representation of a list of ChannelDisplayRangeColor accepted by OMERO when
     * making requests towards the WebGateway API.
     *
     * @param channelDisplayRangeColors  the list of ChannelDisplayRangeColor to represent
     * @return the WebGateway OMERO representation of the list
     */
    public static String getOmeroRepresentation(List<ChannelDisplayRangeColor> channelDisplayRangeColors) {
        return IntStream.range(0, channelDisplayRangeColors.size())
                .mapToObj(i -> String.format(
                        "%d|%d:%d$%02X%02X%02X",
                        i + 1,
                        channelDisplayRangeColors.get(i).minDisplayRange,
                        channelDisplayRangeColors.get(i).maxDisplayRange,
                        ColorTools.red(channelDisplayRangeColors.get(i).rgbColor),
                        ColorTools.green(channelDisplayRangeColors.get(i).rgbColor),
                        ColorTools.blue(channelDisplayRangeColors.get(i).rgbColor)
                ))
                .collect(Collectors.joining (","));
    }
}
