package qupath.ext.omero.gui.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qupath.ext.omero.gui.UiUtilities;
import qupath.ext.omero.imagesserver.OmeroImageServer;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.viewer.QuPathViewer;
import qupath.lib.objects.PathObject;
import qupath.fx.dialogs.Dialogs;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

/**
 * <p>
 *     Non instantiable class that sends QuPath annotations from the currently opened
 *     image to an OMERO server.
 * </p>
 * <p>
 *     Here, an annotation refers to a QuPath annotation (a path object)
 *     and <b>not</b> an OMERO annotation (some metadata attached to images for example).
 * </p>
 * <p>
 *     This class uses a {@link AnnotationForm} to prompt the user for parameters.
 * </p>
 */
class AnnotationSender {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationSender.class);
    private static final ResourceBundle resources = UiUtilities.getResources();

    private AnnotationSender() {
        throw new AssertionError("This class is not instantiable.");
    }

    /**
     * @return the localized name of this command
     */
    public static String getMenuTitle() {
        return resources.getString("AnnotationsSender.sendAnnotations");
    }

    /**
     * Attempt to send annotations of the currently opened image to the corresponding OMERO server.
     * This method doesn't return anything but will show dialogs and notifications indicating the success of the operation.
     */
    public static void sendAnnotations() {
        QuPathViewer viewer = QuPathGUI.getInstance().getViewer();

        if (viewer != null && viewer.getServer() instanceof OmeroImageServer omeroImageServer) {
            AnnotationForm annotationForm;
            try {
                annotationForm = new AnnotationForm();
            } catch (IOException e) {
                logger.error("Error when creating the annotation form", e);
                Dialogs.showErrorMessage(
                        resources.getString("AnnotationsSender.sendAnnotations"),
                        e.getLocalizedMessage()
                );
                return;
            }

            boolean confirmed = Dialogs.showConfirmDialog(
                    resources.getString("AnnotationsSender.dataToSend"),
                    annotationForm
            );

            if (confirmed) {
                Collection<PathObject> annotations = getAnnotations(viewer, annotationForm.areOnlySelectedAnnotationsSelected());

                if (annotations.isEmpty()) {
                    Dialogs.showErrorMessage(
                            resources.getString("AnnotationsSender.sendAnnotations"),
                            resources.getString("AnnotationsSender.noAnnotations")
                    );
                } else {
                    boolean success = omeroImageServer.sendPathObjects(annotations, annotationForm.deleteExistingDataSelected());
                    if (success) {
                        String title;
                        String message = "";

                        if (annotationForm.deleteExistingDataSelected()) {
                            message += resources.getString("AnnotationsSender.existingAnnotationsDeleted") + "\n";
                        }

                        if (annotations.size() == 1) {
                            title = resources.getString("AnnotationsSender.1WrittenSuccessfully");
                            message += resources.getString("AnnotationsSender.1AnnotationWrittenSuccessfully");
                        } else {
                            title = resources.getString("AnnotationsSender.XWrittenSuccessfully");
                            message += MessageFormat.format(resources.getString("AnnotationsSender.XAnnotationsWrittenSuccessfully"), annotations.size());
                        }

                        Dialogs.showInfoNotification(
                                title,
                                message
                        );
                    } else {
                        Dialogs.showErrorNotification(
                                annotations.size() == 1 ?
                                        resources.getString("AnnotationsSender.1AnnotationFailed") :
                                        MessageFormat.format(resources.getString("AnnotationsSender.XAnnotationFailed"), annotations.size()),
                                resources.getString("AnnotationsSender.seeLogs")
                        );
                    }
                }
            }
        } else {
            Dialogs.showErrorMessage(
                    resources.getString("AnnotationsSender.sendAnnotations"),
                    resources.getString("AnnotationsSender.notFromOMERO")
            );
        }
    }

    private static Collection<PathObject> getAnnotations(QuPathViewer viewer, boolean onlySelected) {
        if (onlySelected) {
            return viewer.getAllSelectedObjects().stream().filter(e -> !e.isDetection()).toList();
        } else {
            return viewer.getHierarchy() == null ? List.of() : viewer.getHierarchy().getAnnotationObjects();
        }
    }
}
