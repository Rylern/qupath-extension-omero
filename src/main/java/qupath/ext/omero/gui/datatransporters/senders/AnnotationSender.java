package qupath.ext.omero.gui.datatransporters.senders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qupath.ext.omero.gui.UiUtilities;
import qupath.ext.omero.gui.datatransporters.DataTransporter;
import qupath.ext.omero.gui.datatransporters.forms.SendAnnotationForm;
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
 *     Send QuPath annotations from the currently opened image to an OMERO server.
 * </p>
 * <p>
 *     Here, an annotation refers to a QuPath annotation (a path object)
 *     and <b>not</b> an OMERO annotation (some metadata attached to images for example).
 * </p>
 * <p>
 *     This class uses an {@link SendAnnotationForm} to prompt the user for parameters.
 * </p>
 */
public class AnnotationSender implements DataTransporter {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationSender.class);
    private static final ResourceBundle resources = UiUtilities.getResources();

    @Override
    public String getMenuTitle() {
        return resources.getString("DataTransporters.AnnotationsSender.sendAnnotations");
    }

    @Override
    public boolean requireProject() {
        return false;
    }

    @Override
    public void transportData() {
        QuPathViewer viewer = QuPathGUI.getInstance().getViewer();

        if (viewer != null && viewer.getServer() instanceof OmeroImageServer omeroImageServer) {
            SendAnnotationForm annotationForm;
            try {
                annotationForm = new SendAnnotationForm();
            } catch (IOException e) {
                logger.error("Error when creating the annotation form", e);
                Dialogs.showErrorMessage(
                        resources.getString("DataTransporters.AnnotationsSender.sendAnnotations"),
                        e.getLocalizedMessage()
                );
                return;
            }

            boolean confirmed = Dialogs.showConfirmDialog(
                    resources.getString("DataTransporters.AnnotationsSender.dataToSend"),
                    annotationForm
            );

            if (confirmed) {
                Collection<PathObject> annotations = getAnnotations(viewer, annotationForm.areOnlySelectedAnnotationsSelected());

                if (annotations.isEmpty()) {
                    Dialogs.showErrorMessage(
                            resources.getString("DataTransporters.AnnotationsSender.sendAnnotations"),
                            resources.getString("DataTransporters.AnnotationsSender.noAnnotations")
                    );
                } else {
                    boolean success = omeroImageServer.sendPathObjects(annotations, annotationForm.deleteExistingDataSelected());
                    if (success) {
                        String title;
                        String message = "";

                        if (annotationForm.deleteExistingDataSelected()) {
                            message += resources.getString("DataTransporters.AnnotationsSender.existingAnnotationsDeleted") + "\n";
                        }

                        if (annotations.size() == 1) {
                            title = resources.getString("DataTransporters.AnnotationsSender.1WrittenSuccessfully");
                            message += resources.getString("DataTransporters.AnnotationsSender.1AnnotationWrittenSuccessfully");
                        } else {
                            title = resources.getString("DataTransporters.AnnotationsSender.XWrittenSuccessfully");
                            message += MessageFormat.format(resources.getString("DataTransporters.AnnotationsSender.XAnnotationsWrittenSuccessfully"), annotations.size());
                        }

                        Dialogs.showInfoNotification(
                                title,
                                message
                        );
                    } else {
                        Dialogs.showErrorNotification(
                                annotations.size() == 1 ?
                                        resources.getString("DataTransporters.AnnotationsSender.1AnnotationFailed") :
                                        MessageFormat.format(resources.getString("DataTransporters.AnnotationsSender.XAnnotationFailed"), annotations.size()),
                                resources.getString("DataTransporters.AnnotationsSender.seeLogs")
                        );
                    }
                }
            }
        } else {
            Dialogs.showErrorMessage(
                    resources.getString("DataTransporters.AnnotationsSender.sendAnnotations"),
                    resources.getString("DataTransporters.AnnotationsSender.notFromOMERO")
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
