package qupath.ext.omero.gui.importer;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import qupath.ext.omero.gui.UiUtilities;
import qupath.lib.gui.QuPathGUI;

import java.util.ResourceBundle;

/**
 * <p>
 *     Menu allowing to send annotations (see {@link AnnotationImporter})
 *     from an OMERO server.
 * </p>
 */
public class ImporterMenu extends Menu {

    private static final ResourceBundle resources = UiUtilities.getResources();

    /**
     * Create the importer menu.
     *
     * @param qupath  the QuPath GUI
     */
    public ImporterMenu(QuPathGUI qupath) {
        setText(resources.getString("Importer.importFromOMERO"));

        disableProperty().bind(qupath.imageDataProperty().isNull());

        setItems();
    }

    private void setItems() {
        MenuItem annotationMenuItem = new MenuItem(AnnotationImporter.getMenuTitle());
        annotationMenuItem.setOnAction(ignored -> AnnotationImporter.importAnnotations());
        getItems().add(annotationMenuItem);
    }
}
