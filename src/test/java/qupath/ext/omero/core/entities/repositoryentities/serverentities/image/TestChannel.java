package qupath.ext.omero.core.entities.repositoryentities.serverentities.image;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestChannel {

    @Test
    void Check_Empty() {
        Channel channel = new Gson().fromJson("{}", Channel.class);

        String channelName = channel.getName();

        Assertions.assertTrue(channelName.isEmpty());
    }

    @Test
    void Check_Dimensions() {
        Channel channel = createChannel();
        String expectedChannelName = "Channel name";

        String channelName = channel.getName();

        Assertions.assertEquals(expectedChannelName, channelName);
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
