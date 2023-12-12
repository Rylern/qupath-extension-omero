import qupath.ext.omero.imagesserver.*

/*
 * This script sends all key value pairs of a QuPath image to the
 * corresponding image on an OMERO server.
 *
 * A QuPath project and an OMERO image must be currently opened in QuPath through
 * the QuPath GUI or through the command line (see the open_image_from_command_line.groovy script).
 */

// Parameters
def deleteExistingKeyValuePairs = false
def replaceExistingKeyValuesPairs = true

// Get project
def project = getProject()
if (project == null) {
    println "A project needs to be opened in QuPath before running this script"
    return
}

// Get image and project entry
def imageData = getCurrentImageData()
if (imageData == null) {
    println "An image needs to be opened in QuPath before running this script"
    return
}

// Get key value pairs of the QuPath image
def keyValues = project.getEntry(imageData).getMetadataMap()

// Get image server
def server = imageData.getServer()
def omeroServer = (OmeroImageServer) server

// Attempt to send key value pairs to OMERO
def status = omeroServer.getClient().getApisHandler().sendKeyValuePairs(
        omeroServer.getId(),
        keyValues,
        replaceExistingKeyValuesPairs,
        deleteExistingKeyValuePairs
).get()

if (status) {
    println "Key value pairs sent"
} else {
    println "Error when sending key value pairs. Check that the server is reachable and you have sufficient permissions."
}

// Close server
omeroServer.close()