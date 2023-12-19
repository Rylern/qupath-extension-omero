package qupath.ext.omero.core.entities.image;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestChannelSettings {

    @Test
    void Check_Name() {
        String expectedName = "";
        ChannelSettings channelSettings = new ChannelSettings(expectedName, 0, 0, "");

        String name = channelSettings.getName();

        Assertions.assertEquals(expectedName, name);
    }

    @Test
    void Check_Min_Display_Range() {
        double expectedMinDisplayRange = 65.43;
        ChannelSettings channelSettings = new ChannelSettings("", expectedMinDisplayRange, 0, "");

        double minDisplayRang = channelSettings.getMinDisplayRange();

        Assertions.assertEquals(expectedMinDisplayRange, minDisplayRang);
    }

    @Test
    void Check_Max_Display_Range() {
        double expectedMaxDisplayRange = 98.34234;
        ChannelSettings channelSettings = new ChannelSettings("", 0, expectedMaxDisplayRange, "");

        double maxDisplayRang = channelSettings.getMaxDisplayRange();

        Assertions.assertEquals(expectedMaxDisplayRange, maxDisplayRang);
    }

    @Test
    void Check_RGB_Color() {
        String expectedColor = "0FBF0F";
        ChannelSettings channelSettings = new ChannelSettings("", 0, 0, expectedColor);

        String color = channelSettings.getRgbColorHex();

        Assertions.assertEquals(expectedColor, color);
    }
}
