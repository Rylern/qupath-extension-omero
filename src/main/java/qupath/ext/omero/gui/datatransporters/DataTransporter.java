package qupath.ext.omero.gui.datatransporters;

/**
 * A class that can import or export data between the currently opened image and its corresponding OMERO server.
 */
public interface DataTransporter {

    /**
     * @return the localized name of this command
     */
    String getMenuTitle();

    /**
     * @return whether this transporter needs an opened project in order to work
     */
    boolean requireProject();

    /**
     * Attempt to import or export data between the currently opened image and its corresponding OMERO server.
     * This method doesn't return anything but will show dialogs and notifications indicating the success of the operation.
     */
    void transportData();
}
