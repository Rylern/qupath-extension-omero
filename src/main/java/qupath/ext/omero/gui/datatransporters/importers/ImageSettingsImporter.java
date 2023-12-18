package qupath.ext.omero.gui.datatransporters.importers;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qupath.ext.omero.core.entities.repositoryentities.serverentities.image.Image;
import qupath.ext.omero.gui.UiUtilities;
import qupath.ext.omero.gui.datatransporters.DataTransporter;
import qupath.ext.omero.gui.datatransporters.forms.ImageSettingsForm;
import qupath.ext.omero.imagesserver.OmeroImageServer;
import qupath.fx.dialogs.Dialogs;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.viewer.QuPathViewer;
import qupath.lib.images.ImageData;
import qupath.lib.images.servers.ImageChannel;
import qupath.lib.images.servers.ImageServerMetadata;
import qupath.lib.projects.Project;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

/**
 * <p>
 *     Import image settings from an OMERO server to the currently opened image.
 * </p>
 * <p>
 *     This class uses an {@link ImageSettingsForm} to prompt the user for parameters.
 * </p>
 */
public class ImageSettingsImporter implements DataTransporter {

    private static final Logger logger = LoggerFactory.getLogger(ImageSettingsImporter.class);
    private static final ResourceBundle resources = UiUtilities.getResources();

    @Override
    public String getMenuTitle() {
        return resources.getString("DataTransporters.ImageSettingsImporter.importImageSettings");
    }

    @Override
    public boolean requireProject() {
        return false;
    }

    @Override
    public void transportData() {
        QuPathGUI quPathGUI = QuPathGUI.getInstance();
        QuPathViewer viewer = quPathGUI.getViewer();

        if (viewer.getServer() instanceof OmeroImageServer omeroImageServer) {
            ImageSettingsForm imageSettingsForm;
            try {
                imageSettingsForm = new ImageSettingsForm();
            } catch (IOException e) {
                logger.error("Error when creating the image settings form", e);
                Dialogs.showErrorMessage(
                        resources.getString("DataTransporters.ImageSettingsImporter.importImageSettings"),
                        e.getLocalizedMessage()
                );
                return;
            }

            boolean confirmed = Dialogs.showConfirmDialog(
                    resources.getString("DataTransporters.ImageSettingsImporter.dataToSend"),
                    imageSettingsForm
            );
            List<ImageSettingsForm.Choice> selectedChoices = imageSettingsForm.getSelectedChoices();

            if (confirmed && !selectedChoices.isEmpty()) {
                omeroImageServer.getClient().getApisHandler().getImage(omeroImageServer.getId()).thenAccept(image -> Platform.runLater(() -> {
                    if (image.isPresent()) {
                        StringBuilder successMessage = new StringBuilder();
                        StringBuilder errorMessage = new StringBuilder();

                        if (selectedChoices.contains(ImageSettingsForm.Choice.IMAGE_NAME)) {
                            if (changeImageName(quPathGUI, viewer.getImageData(), image.get())) {
                                successMessage
                                        .append(resources.getString("DataTransporters.ImageSettingsImporter.imageNameUpdated"))
                                        .append("\n");
                            } else {
                                errorMessage
                                        .append(resources.getString("DataTransporters.ImageSettingsImporter.imageNameNotUpdated"))
                                        .append("\n");
                            }
                        }

                        if (selectedChoices.contains(ImageSettingsForm.Choice.CHANNEL_NAMES)) {
                            if (changeChannelNames(omeroImageServer, viewer, image.get())) {
                                successMessage
                                        .append(resources.getString("DataTransporters.ImageSettingsImporter.channelNamesUpdated"))
                                        .append("\n");
                            } else {
                                errorMessage
                                        .append(resources.getString("DataTransporters.ImageSettingsImporter.channelNamesNotUpdated"))
                                        .append("\n");
                            }
                        }

                        if (!errorMessage.isEmpty()) {
                            Dialogs.showErrorMessage(
                                    resources.getString("DataTransporters.ImageSettingsImporter.importImageSettings"),
                                    errorMessage.toString()
                            );
                        }

                        if (!successMessage.isEmpty()) {
                            Dialogs.showInfoNotification(
                                    resources.getString("DataTransporters.ImageSettingsImporter.importImageSettings"),
                                    successMessage.toString()
                            );
                        }
                    } else {
                        Dialogs.showErrorMessage(
                                resources.getString("DataTransporters.ImageSettingsImporter.importImageSettings"),
                                resources.getString("DataTransporters.ImageSettingsImporter.couldNotGetImage")
                        );
                    }
                }));
            }
        } else {
            Dialogs.showErrorMessage(
                    resources.getString("DataTransporters.ImageSettingsImporter.importImageSettings"),
                    resources.getString("DataTransporters.ImageSettingsImporter.notFromOMERO")
            );
        }
    }

    private static boolean changeImageName(QuPathGUI quPathGUI, ImageData<BufferedImage> imageData, Image image) {
        Project<BufferedImage> project = quPathGUI.getProject();

        if (project != null && project.getEntry(imageData) != null) {
            project.getEntry(imageData).setImageName(image.getName());
            return true;
        } else {
            return false;
        }
    }

    private static boolean changeChannelNames(OmeroImageServer omeroImageServer, QuPathViewer viewer, Image image) {
        List<ImageChannel> channels = omeroImageServer.getMetadata().getChannels();
        List<String> newChannelNames = image.getChannelsName();

        if (channels.size() == image.getChannelsName().size()) {
            viewer.getImageData().updateServerMetadata(new ImageServerMetadata.Builder(omeroImageServer.getMetadata())
                    .channels(IntStream.range(0, channels.size())
                            .mapToObj(i -> ImageChannel.getInstance(newChannelNames.get(i), channels.get(i).getColor()))
                            .toList()
                    )
                    .build()
            );
            return true;
        } else {
            return false;
        }
    }
}
