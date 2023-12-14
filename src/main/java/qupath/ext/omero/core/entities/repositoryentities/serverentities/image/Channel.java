package qupath.ext.omero.core.entities.repositoryentities.serverentities.image;

import com.google.gson.annotations.SerializedName;

/**
 * This class contains various information about a channel.
 */
class Channel {
    @SerializedName(value = "Name") private String name;

    /**
     * @return the name of the channel, or an empty String if not found
     */
    public String getName() {
        return name == null ? "" : name;
    }
}
