package qupath.ext.omero.gui.datatransporters.forms;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import qupath.ext.omero.gui.UiUtilities;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Form that lets the user choose what image settings parameters
 * to change.
 */
public class ImageSettingsForm extends VBox {

    @FXML
    private CheckBox imageName;
    @FXML
    private CheckBox channelNames;
    @FXML
    private CheckBox channelColors;
    @FXML
    private CheckBox channelDisplayRanges;

    /**
     * Describes which parameters to change.
     */
    public enum Choice {
        IMAGE_NAME,
        CHANNEL_NAMES,
        CHANNEL_COLORS,
        CHANNEL_DISPLAY_RANGES
    }
    private final Map<CheckBox, Choice> checkBoxChoiceMap;

    /**
     * Creates the image settings form.
     *
     * @throws IOException if an error occurs while creating the form
     */
    public ImageSettingsForm() throws IOException {
        UiUtilities.loadFXML(this, ImageSettingsForm.class.getResource("image_settings_form.fxml"));

        checkBoxChoiceMap = Map.of(
                imageName, Choice.IMAGE_NAME,
                channelNames, Choice.CHANNEL_NAMES,
                channelColors, Choice.CHANNEL_COLORS,
                channelDisplayRanges, Choice.CHANNEL_DISPLAY_RANGES
        );
    }

    /**
     * @return the selected choices
     */
    public List<Choice> getSelectedChoices() {
        return checkBoxChoiceMap.entrySet().stream()
                .filter(entry -> entry.getKey().isSelected())
                .map(Map.Entry::getValue)
                .toList();
    }
}
