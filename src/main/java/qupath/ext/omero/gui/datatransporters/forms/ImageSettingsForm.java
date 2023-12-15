package qupath.ext.omero.gui.datatransporters.forms;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import qupath.ext.omero.gui.UiUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Form that lets the user choose what image settings parameters
 * to change.
 */
public class ImageSettingsForm extends VBox {

    @FXML
    private CheckBox imageName;
    @FXML
    private CheckBox channelNames;

    /**
     * Describes which parameters to change.
     */
    public enum Choice {
        IMAGE_NAME,
        CHANNEL_NAMES
    }

    /**
     * Creates the image settings form.
     *
     * @throws IOException if an error occurs while creating the form
     */
    public ImageSettingsForm() throws IOException {
        UiUtilities.loadFXML(this, ImageSettingsForm.class.getResource("image_settings_form.fxml"));
    }

    /**
     * @return the selected choices
     */
    public List<Choice> getSelectedChoices() {
        List<Choice> selectedChoices = new ArrayList<>();

        if (imageName.isSelected()) {
            selectedChoices.add(Choice.IMAGE_NAME);
        }
        if (channelNames.isSelected()) {
            selectedChoices.add(Choice.CHANNEL_NAMES);
        }

        return selectedChoices;
    }
}
