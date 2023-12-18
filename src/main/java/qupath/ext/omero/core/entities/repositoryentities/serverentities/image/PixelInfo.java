package qupath.ext.omero.core.entities.repositoryentities.serverentities.image;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Optional;

/**
 * <p>This class contains various information related to the pixels of an image.</p>
 * <p>It uses the {@link PhysicalSize}, {@link ImageType}, and {@link Channel} classes.</p>
 */
class PixelInfo {

    @SerializedName(value = "SizeX") private int width;
    @SerializedName(value = "SizeY") private int height;
    @SerializedName(value = "SizeZ") private int z;
    @SerializedName(value = "SizeC") private int c;
    @SerializedName(value = "SizeT") private int t;
    @SerializedName(value = "PhysicalSizeX") private PhysicalSize physicalSizeX;
    @SerializedName(value = "PhysicalSizeY") private PhysicalSize physicalSizeY;
    @SerializedName(value = "PhysicalSizeZ") private PhysicalSize physicalSizeZ;
    @SerializedName(value = "Type") private ImageType imageType;
    @SerializedName(value = "Channels") private List<Channel> channels;

    @Override
    public String toString() {
        return String.format("""
                Image
                    type: %s
                    width: %d
                    height: %d
                    z: %d
                    c: %d
                    t: %d
                    physicalSizeX: %s
                    physicalSizeY: %s
                    physicalSizeZ: %s
                """, imageType, width, height, z, c, t, physicalSizeX, physicalSizeY, physicalSizeZ
        );
    }

    /**
     * @return the dimensions (width in pixels, height in pixels, number of z-slices, number of channels, number of time points)
     * of the image. If a dimension was not found, 0 is returned
     */
    public int[] getImageDimensions() {
        return new int[] { width, height, z, c, t };
    }

    /**
     * @return the pixel width, or an empty Optional if not found
     */
    public Optional<PhysicalSize> getPhysicalSizeX() {
        return Optional.ofNullable(physicalSizeX);
    }

    /**
     * @return the pixel height, or an empty Optional if not found
     */
    public Optional<PhysicalSize> getPhysicalSizeY() {
        return Optional.ofNullable(physicalSizeY);
    }

    /**
     * @return the spacing between z-slices, or an empty Optional if not found
     */
    public Optional<PhysicalSize> getPhysicalSizeZ() {
        return Optional.ofNullable(physicalSizeZ);
    }

    /**
     * @return the format of the pixel values (e.g. uint8)
     */
    public Optional<String> getPixelType() {
        return imageType == null ? Optional.empty() : imageType.getValue();
    }

    /**
     * Return the channels of this image. If one of the channel name is not found,
     * the channel names take the values 0, 1, 2... depending on the number of channels.
     *
     * @return the channels of this image
     */
    public List<Channel> getChannels() {
        if (channels == null) {
            return List.of();
        } else {
            if (channels.stream().map(Channel::getName).anyMatch(Optional::isEmpty)) {
                for (int i=0; i<channels.size(); ++i) {
                    channels.get(i).setName(String.valueOf(i));
                }
            }
            return channels;
        }
    }
}
