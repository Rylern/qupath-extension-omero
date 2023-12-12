package qupath.ext.omero.gui.sender;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import qupath.ext.omero.gui.UiUtilities;
import qupath.lib.gui.QuPathGUI;

import java.util.ResourceBundle;

/**
 * <p>
 *     Menu allowing to send annotations (see {@link AnnotationSender}) and
 *     key value pairs (see {@link KeyValuesSender}) to an OMERO server.
 * </p>
 */
public class SenderMenu extends Menu {

    private static final ResourceBundle resources = UiUtilities.getResources();

    /**
     * Create the sender menu.
     *
     * @param qupath  the QuPath GUI
     */
    public SenderMenu(QuPathGUI qupath) {
        setText(resources.getString("Sender.sendToOMERO"));

        disableProperty().bind(qupath.imageDataProperty().isNull());

        setItems(qupath);
    }

    private void setItems(QuPathGUI qupath) {
        MenuItem annotationMenuItem = new MenuItem(AnnotationSender.getMenuTitle());
        annotationMenuItem.setOnAction(ignored -> AnnotationSender.sendAnnotations());
        getItems().add(annotationMenuItem);

        MenuItem keyValuesMenuItem = new MenuItem(KeyValuesSender.getMenuTitle());
        keyValuesMenuItem.setOnAction(ignored -> KeyValuesSender.sendKeyValues());
        keyValuesMenuItem.disableProperty().bind(qupath.projectProperty().isNull());
        getItems().add(keyValuesMenuItem);
    }
}
