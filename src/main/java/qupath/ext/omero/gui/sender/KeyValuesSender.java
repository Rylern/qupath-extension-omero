package qupath.ext.omero.gui.sender;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qupath.ext.omero.gui.UiUtilities;
import qupath.ext.omero.imagesserver.OmeroImageServer;
import qupath.fx.dialogs.Dialogs;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.projects.ProjectImageEntry;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * <p>
 *     Non instantiable class that sends key value pairs from the currently opened
 *     image to an OMERO server.
 * </p>
 * <p>
 *     Since key value pairs are only defined in projects, a project must be opened.
 * </p>
 * <p>
 *     This class uses a {@link KeyValuesForm} to prompt the user for parameters.
 * </p>
 */
class KeyValuesSender {

    private static final Logger logger = LoggerFactory.getLogger(KeyValuesSender.class);
    private static final ResourceBundle resources = UiUtilities.getResources();

    private KeyValuesSender() {
        throw new AssertionError("This class is not instantiable.");
    }

    /**
     * @return the localized name of this command
     */
    public static String getMenuTitle() {
        return resources.getString("KeyValuesSender.sendKeyValues");
    }

    /**
     * Attempt to send key value pairs of the currently opened image to the corresponding OMERO server.
     * This method doesn't return anything but will show dialogs and notifications indicating the success of the operation.
     */
    public static void sendKeyValues() {
        QuPathGUI qupath = QuPathGUI.getInstance();

        if (qupath.getProject() == null) {
            Dialogs.showErrorMessage(
                    resources.getString("KeyValuesSender.sendKeyValues"),
                    resources.getString("KeyValuesSender.projectNotOpened")
            );
        } else if (qupath.getViewer() == null || !(qupath.getViewer().getServer() instanceof OmeroImageServer omeroImageServer)) {
            Dialogs.showErrorMessage(
                    resources.getString("KeyValuesSender.sendKeyValues"),
                    resources.getString("KeyValuesSender.notFromOMERO")
            );
        } else {
            ProjectImageEntry<BufferedImage> entry = qupath.getProject().getEntry(qupath.getImageData());
            Map<String,String> keyValues = entry.getMetadataMap();

            if (keyValues.isEmpty()) {
                Dialogs.showErrorMessage(
                        resources.getString("KeyValuesSender.sendKeyValues"),
                        resources.getString("KeyValuesSender.noValues")
                );
            } else {
                KeyValuesForm keyValuesForm;
                try {
                    keyValuesForm = new KeyValuesForm();
                } catch (IOException e) {
                    logger.error("Error when creating the annotation form", e);
                    Dialogs.showErrorMessage(
                            resources.getString("KeyValuesSender.sendKeyValues"),
                            e.getLocalizedMessage()
                    );
                    return;
                }

                boolean confirmed = Dialogs.showConfirmDialog(
                        resources.getString("AnnotationsSender.dataToSend"),
                        keyValuesForm
                );

                if (confirmed) {
                    omeroImageServer.getClient().getApisHandler().sendKeyValuePairs(
                            omeroImageServer.getId(),
                            keyValues,
                            keyValuesForm.getChoice().equals(KeyValuesForm.Choice.REPLACE_EXITING),
                            keyValuesForm.getChoice().equals(KeyValuesForm.Choice.DELETE_ALL)
                    ).thenAccept(success -> Platform.runLater(() -> {
                        if (success) {
                            Dialogs.showInfoNotification(
                                    resources.getString("KeyValuesSender.sendKeyValues"),
                                    resources.getString("KeyValuesSender.keyValuesSent")
                            );
                        } else {
                            Dialogs.showErrorNotification(
                                    resources.getString("KeyValuesSender.sendKeyValues"),
                                    resources.getString("KeyValuesSender.keyValuesNotSent")
                            );
                        }
                    }));
                }
            }
        }
    }
}
