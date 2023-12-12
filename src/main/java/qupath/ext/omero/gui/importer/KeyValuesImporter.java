package qupath.ext.omero.gui.importer;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qupath.ext.omero.core.entities.annotations.MapAnnotation;
import qupath.ext.omero.core.entities.repositoryentities.serverentities.image.Image;
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
 *     Non instantiable class that sends key value pairs to the currently opened
 *     image from the corresponding OMERO server.
 * </p>
 * <p>
 *     Since key value pairs are only defined in projects, a project must be opened.
 * </p>
 * <p>
 *     This class uses a {@link KeyValuesForm} to prompt the user for parameters.
 * </p>
 */
class KeyValuesImporter {

    private static final Logger logger = LoggerFactory.getLogger(KeyValuesImporter.class);
    private static final ResourceBundle resources = UiUtilities.getResources();

    private KeyValuesImporter() {
        throw new AssertionError("This class is not instantiable.");
    }

    /**
     * @return the localized name of this command
     */
    public static String getMenuTitle() {
        return resources.getString("KeyValuesImporter.importKeyValues");
    }

    /**
     * Attempt to import key value pairs to the currently opened image from the corresponding OMERO server.
     * This method doesn't return anything but will show dialogs and notifications indicating the success of the operation.
     */
    public static void importKeyValues() {
        QuPathGUI qupath = QuPathGUI.getInstance();

        if (qupath.getProject() == null) {
            Dialogs.showErrorMessage(
                    resources.getString("KeyValuesImporter.importKeyValues"),
                    resources.getString("KeyValuesImporter.projectNotOpened")
            );
        } else if (qupath.getViewer() == null || !(qupath.getViewer().getServer() instanceof OmeroImageServer omeroImageServer)) {
            Dialogs.showErrorMessage(
                    resources.getString("KeyValuesImporter.importKeyValues"),
                    resources.getString("KeyValuesImporter.notFromOMERO")
            );
        } else {
            KeyValuesForm keyValuesForm;
            try {
                keyValuesForm = new KeyValuesForm();
            } catch (IOException e) {
                logger.error("Error when creating the key values form", e);
                Dialogs.showErrorMessage(
                        resources.getString("KeyValuesImporter.importKeyValues"),
                        e.getLocalizedMessage()
                );
                return;
            }

            boolean confirmed = Dialogs.showConfirmDialog(
                    resources.getString("KeyValuesImporter.importKeyValues"),
                    keyValuesForm
            );
            if (confirmed) {
                omeroImageServer.getClient().getApisHandler()
                        .getAnnotations(omeroImageServer.getId(), Image.class).thenAccept(annotationGroup -> Platform.runLater(() -> {
                            if (annotationGroup.isPresent()) {
                                Map<String,String> keyValues = MapAnnotation.getCombinedValues(
                                        annotationGroup.get().getAnnotationsOfClass(MapAnnotation.class)
                                );

                                ProjectImageEntry<BufferedImage> projectEntry = qupath.getProject().getEntry(qupath.getImageData());

                                if (keyValuesForm.getChoice().equals(KeyValuesForm.Choice.DELETE_ALL)) {
                                    projectEntry.clearMetadata();
                                }
                                for (Map.Entry<String, String> entry : keyValues.entrySet()) {
                                    if (!keyValuesForm.getChoice().equals(KeyValuesForm.Choice.KEEP_EXISTING) || !projectEntry.containsMetadata(entry.getKey())) {
                                        projectEntry.putMetadataValue(entry.getKey(), entry.getValue());
                                    }
                                }

                                Dialogs.showInfoNotification(
                                        resources.getString("KeyValuesImporter.importKeyValues"),
                                        resources.getString("KeyValuesImporter.keyValuesImported")
                                );
                            } else {
                                Dialogs.showErrorMessage(
                                        resources.getString("KeyValuesImporter.importKeyValues"),
                                        resources.getString("KeyValuesImporter.couldNotRetrieveAnnotations")
                                );
                            }
                        }));
            }
        }
    }
}
