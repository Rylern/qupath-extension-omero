package qupath.ext.omero.core.entities.repositoryentities.serverentities.image;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestChannel {

    @Test
    void Check_Empty() {
        Channel channel = new Gson().fromJson("{}", Channel.class);

        String channelName = channel.getName().orElse("");

        Assertions.assertTrue(channelName.isEmpty());
    }

    @Test
    void Check_Dimensions() {
        Channel channel = createChannel();
        String expectedChannelName = "Channel name";

        String channelName = channel.getName().orElse("");

        Assertions.assertEquals(expectedChannelName, channelName);
    }

    @Test
    void Check_Name_Changed() {
        Channel channel = createChannel();
        String expectedChannelName = "New channel name";

        channel.setName(expectedChannelName);

        Assertions.assertEquals(expectedChannelName, channel.getName().orElse(""));
    }

    private Channel createChannel() {
        String json = """
                {
                    "Name": "Channel name"
                }
                """;
        return new Gson().fromJson(json, Channel.class);
    }
}
