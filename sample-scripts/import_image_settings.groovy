import qupath.ext.omero.imagesserver.*
import qupath.ext.omero.core.entities.image.*

/*
 * This script imports the image settings (image name, channel names, channel colors, channel display ranges)
 * of an image stored on an OMERO server and add them to the image in QuPath.
 *
 * An OMERO image must be currently opened in QuPath through the QuPath GUI or through
 * the command line (see the open_image_from_command_line.groovy script).
 *
 * If the image name is imported, a QuPath project must be opened.
 * If one of the channel settings is imported, the image must not have the RGB format.
 */

// Parameters
def importImageName = true
def importChannelNames = true
def importChannelColors = true
def importChannelDisplayRanges = true

// Check that a project is opened (if needed)
if (importImageName && getProject() == null) {
    println "A project needs to be opened in QuPath if the image name is to be imported"
    return
}

// Get image data
def imageData = getCurrentImageData()
if (imageData == null) {
    println "An image needs to be opened in QuPath before running this script"
    return
}

// Get image server
def server = imageData.getServer()
def omeroServer = (OmeroImageServer) server

// Check that the image has not the RGB format (if needed)
if ((importChannelNames || importChannelColors || importChannelDisplayRanges) && omeroServer.getMetadata().isRGB()) {
    println "The image must not have the RGB format if one of the channel settings is to be imported"
    return
}

// Retrieve image settings from OMERO
def imageSettings = omeroServer.getClient().getApisHandler().getImageSettings(omeroServer.getId()).get()

if (imageSettings.isPresent()) {
    // Retrieve image name and channel settings from the response
    def imageName = imageSettings.get().getName()
    def channelSettings = imageSettings.get().getChannelSettings()

    if (importImageName) {
        getProject().getEntry(imageData).setImageName(imageName)
        println "Image name imported"
    }

    if (importChannelNames) {
        // Get the channel names from the image settings
        def channelNames = channelSettings.stream().map(ChannelSettings::getName).toArray(String[]::new)

        setChannelNames(channelNames)
        println "Channel names imported"
    }

    if (importChannelColors) {
        // Get the channel colors from the image settings
        def channelColors = channelSettings.stream().map(ChannelSettings::getRgbColor).toArray(Integer[]::new)

        setChannelColors(channelColors)
        println "Channel colors imported"
    }

    if (importChannelDisplayRanges) {
        // Get the the display and the channels of the current QuPath viewer
        def display = getCurrentViewer().getImageDisplay();
        def channels = display.availableChannels();

        for (int i=0; i<channels.size(); i++) {
            display.setMinMaxDisplay(
                    channels.get(i),
                    (float) channelSettings.get(i).getMinDisplayRange(),
                    (float) channelSettings.get(i).getMaxDisplayRange()
            )
        }
        println "Channel display ranges imported"
    }
} else {
    println "Error when retrieving image settings"
}

// Close server
omeroServer.close()