/**
 * <p>
 *     This package provides UI elements to send/import several entities of the current image to/from the corresponding OMERO server.
 * </p>
 * <p>
 *     The {@link qupath.ext.omero.gui.datatransporters.forms forms} package contains forms used to prompt the user
 *     for parameters when sending/importing entities to/from an OMERO server.
 * </p>
 * <p>
 *     The {@link qupath.ext.omero.gui.datatransporters.importers importers} package contains classes that import
 *     entities from an OMERO server to the currently opened image.
 * </p>
 * <p>
 *     The {@link qupath.ext.omero.gui.datatransporters.senders senders} package contains classes that send
 *     entities to an OMERO server from the currently opened image.
 * </p>
 * <p>
 *     The {@link qupath.ext.omero.gui.datatransporters.DataTransporter DataTransporter} interface defines a
 *     class that can import or export data between the currently opened image and its corresponding OMERO server.
 * </p>
 * <p>
 *     The {@link qupath.ext.omero.gui.datatransporters.DataTransporterMenu DataTransporterMenu} class is a menu
 *     whose items represent a list of {@link qupath.ext.omero.gui.datatransporters.DataTransporter DataTransporter}.
 * </p>
 */
package qupath.ext.omero.gui.datatransporters;