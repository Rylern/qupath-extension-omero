package qupath.ext.omero.gui.sender;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import qupath.ext.omero.gui.UiUtilities;

import java.io.IOException;

/**
 * Form that can be used in a dialog to let the user choose
 * some parameters when sending key value pairs to an OMERO server.
 */
class KeyValuesForm extends VBox {

    @FXML
    private RadioButton keepExisting;
    @FXML
    private RadioButton replaceExisting;
    @FXML
    private RadioButton deleteAll;

    /**
     * Describes how to deal with existing key value pairs
     * when sending some to an OMERO server.
     */
    public enum Choice {
        KEEP_EXISTING,
        REPLACE_EXITING,
        DELETE_ALL
    }

    /**
     * Creates the key values form.
     *
     * @throws IOException if an error occurs while creating the form
     */
    public KeyValuesForm() throws IOException {
        UiUtilities.loadFXML(this, KeyValuesForm.class.getResource("key_values_form.fxml"));

        ToggleGroup group = new ToggleGroup();
        keepExisting.setToggleGroup(group);
        replaceExisting.setToggleGroup(group);
        deleteAll.setToggleGroup(group);
    }

    /**
     * @return the choice of the user regarding existing key value pairs
     */
    public Choice getChoice() {
        if (keepExisting.isSelected()) {
            return Choice.KEEP_EXISTING;
        } else if (replaceExisting.isSelected()) {
            return Choice.REPLACE_EXITING;
        } else {
            return Choice.DELETE_ALL;
        }
    }
}
