package qupath.ext.omero.gui.importer;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import qupath.ext.omero.gui.UiUtilities;
import qupath.lib.gui.QuPathGUI;

import java.util.ResourceBundle;

/**
 * <p>
 *     Menu allowing to import annotations (see {@link AnnotationImporter}) and
 *     key values pairs (see {@link KeyValuesImporter}) from an OMERO server.
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

        setItems(qupath);
    }

    private void setItems(QuPathGUI qupath) {
        MenuItem annotationMenuItem = new MenuItem(AnnotationImporter.getMenuTitle());
        annotationMenuItem.setOnAction(ignored -> AnnotationImporter.importAnnotations());
        getItems().add(annotationMenuItem);

        MenuItem keyValuesMenuItem = new MenuItem(KeyValuesImporter.getMenuTitle());
        keyValuesMenuItem.setOnAction(ignored -> KeyValuesImporter.importKeyValues());
        keyValuesMenuItem.disableProperty().bind(qupath.projectProperty().isNull());
        getItems().add(keyValuesMenuItem);
    }
}
