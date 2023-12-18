package qupath.ext.omero.gui.datatransporters.senders;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qupath.ext.omero.gui.UiUtilities;
import qupath.ext.omero.gui.datatransporters.DataTransporter;
import qupath.ext.omero.gui.datatransporters.forms.ImageSettingsForm;
import qupath.ext.omero.imagesserver.OmeroImageServer;
import qupath.fx.dialogs.Dialogs;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.viewer.QuPathViewer;
import qupath.lib.images.servers.ImageChannel;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 *     Send image settings from the currently opened image to an OMERO server.
 * </p>
 * <p>
 *     This class uses an {@link ImageSettingsForm} to prompt the user for parameters.
 * </p>
 */
public class ImageSettingsSender implements DataTransporter {

    private static final Logger logger = LoggerFactory.getLogger(ImageSettingsSender.class);
    private static final ResourceBundle resources = UiUtilities.getResources();
    private enum MessageType {
        SUCCESS,
        ERROR
    }

    @Override
    public String getMenuTitle() {
        return resources.getString("DataTransporters.ImageSettingsSender.sendImageSettings");
    }

    @Override
    public boolean requireProject() {
        return false;
    }

    @Override
    public void transportData() {
        QuPathViewer viewer = QuPathGUI.getInstance().getViewer();

        if (viewer != null && viewer.getServer() instanceof OmeroImageServer omeroImageServer) {
            ImageSettingsForm imageSettingsForm;
            try {
                imageSettingsForm = new ImageSettingsForm();
            } catch (IOException e) {
                logger.error("Error when creating the image settings form", e);
                Dialogs.showErrorMessage(
                        resources.getString("DataTransporters.ImageSettingsSender.sendImageSettings"),
                        e.getLocalizedMessage()
                );
                return;
            }

            boolean confirmed = Dialogs.showConfirmDialog(
                    resources.getString("DataTransporters.ImageSettingsSender.dataToSend"),
                    imageSettingsForm
            );

            if (confirmed && !imageSettingsForm.getSelectedChoices().isEmpty()) {
                Map<ImageSettingsForm.Choice, CompletableFuture<Boolean>> requests = createRequests(
                        imageSettingsForm.getSelectedChoices(),
                        omeroImageServer
                );

                CompletableFuture.supplyAsync(() -> requests.entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().join()
                ))).thenAccept(statuses -> Platform.runLater(() -> {
                    String successMessage = createMessageFromResponses(statuses, MessageType.SUCCESS);
                    String errorMessage = createMessageFromResponses(statuses, MessageType.ERROR);

                    if (!errorMessage.isEmpty()) {
                        errorMessage += String.format("\n\n%s", resources.getString("DataTransporters.ImageSettingsSender.notAllParametersSent"));
                        Dialogs.showErrorMessage(
                                resources.getString("DataTransporters.ImageSettingsSender.sendImageSettings"),
                                errorMessage
                        );
                    }

                    if (!successMessage.isEmpty()) {
                        Dialogs.showInfoNotification(
                                resources.getString("DataTransporters.ImageSettingsSender.sendImageSettings"),
                                successMessage
                        );
                    }
                }));
            }
        } else {
            Dialogs.showErrorMessage(
                    resources.getString("DataTransporters.ImageSettingsSender.sendImageSettings"),
                    resources.getString("DataTransporters.ImageSettingsSender.notFromOMERO")
            );
        }
    }

    private static Map<ImageSettingsForm.Choice, CompletableFuture<Boolean>> createRequests(
            List<ImageSettingsForm.Choice> selectedChoices,
            OmeroImageServer omeroImageServer
    ) {
        return selectedChoices.stream()
                .collect(Collectors.toMap(Function.identity(), choice -> switch (choice) {
                    case IMAGE_NAME -> omeroImageServer.getClient().getApisHandler().changeImageName(
                            omeroImageServer.getId(),
                            omeroImageServer.getMetadata().getName()
                    );
                    case CHANNEL_NAMES -> omeroImageServer.getClient().getApisHandler().changeChannelsName(
                            omeroImageServer.getId(),
                            omeroImageServer.getMetadata().getChannels().stream().map(ImageChannel::getName).toList()
                    );
                }));
    }

    private static String createMessageFromResponses(Map<ImageSettingsForm.Choice, Boolean> statuses, MessageType messageType) {
        return statuses.entrySet().stream()
                .filter(entry -> switch (messageType) {
                    case SUCCESS -> entry.getValue();
                    case ERROR -> !entry.getValue();
                })
                .map(entry -> MessageFormat.format(
                        resources.getString(entry.getValue() ? "DataTransporters.ImageSettingsSender.parameterSent" : "DataTransporters.ImageSettingsSender.parameterNotSent"),
                        resources.getString(switch (entry.getKey()) {
                            case IMAGE_NAME -> "DataTransporters.ImageSettingsSender.imageName";
                            case CHANNEL_NAMES -> "DataTransporters.ImageSettingsSender.channelNames";
                        })
                ))
                .collect(Collectors.joining("\n"));
    }
}
