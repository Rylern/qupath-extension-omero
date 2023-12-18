package qupath.ext.omero.core.entities.repositoryentities.serverentities.image;

import com.google.gson.annotations.SerializedName;

import java.util.Optional;

/**
 * This class contains various information about a channel.
 */
class Channel {
    @SerializedName(value = "Name") private String name;

    /**
     * @return the name of the channel, or an empty Optional if not found
     */
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    /**
     * Set the name of this channel.
     *
     * @param name  the new name of this channel
     */
    public void setName(String name) {
        this.name = name;
    }
}
