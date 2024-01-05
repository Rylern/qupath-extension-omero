import qupath.ext.omero.imagesserver.*
import qupath.lib.gui.tools.*
import qupath.ext.omero.core.entities.repositoryentities.serverentities.image.*
import qupath.lib.objects.*

/*
 * This script send the annotation and detections measurements of the current image to the OMERO server as attachments.
 * Existing attachments can be deleted.
 *
 * A QuPath project and an OMERO image must be currently opened in QuPath through the QuPath GUI or
 * through the command line (see the open_image_from_command_line.groovy script).
 */

// Parameters
def deleteExistingAttachments = true
def sendAnnotationMeasurements = true
def sendDetectionMeasurements = true

// Open server
def imageData = getCurrentImageData()
if (imageData == null) {
    println "An image needs to be opened in QuPath before running this script"
    return
}

// Get project and project entry
def project = getProject()
if (project == null) {
    println "A project needs to be opened in QuPath before running this script"
    return
}
def projectEntry = project.getEntry(imageData)

// Get image server
def server = imageData.getServer()
def omeroServer = (OmeroImageServer) server

// Delete existing attachment
if (deleteExistingAttachments) {
    boolean status = omeroServer.getClient().getApisHandler().deleteAttachments(omeroServer.getId(), Image.class).get()

    if (status) {
        println "Existing attachments deleted"
    } else {
        println "Impossible to delete existing attachments. Check the logs"
    }
}

// Send annotation measurements
if (sendAnnotationMeasurements) {
    try (OutputStream outputStream = new ByteArrayOutputStream()) {
        // The image must be saved first because non saved measures won't be exported
        projectEntry.saveImageData(imageData)

        // Get annotation measurements
        new MeasurementExporter()
                .exportType(PathAnnotationObject.class)
                .imageList(List.of(projectEntry))
                .separator(",")
                .exportMeasurements(outputStream);
        def annotationMeasurements = outputStream.toString()

        // Send annotation measurements
        def status = omeroServer.getClient().getApisHandler().sendAttachment(
                omeroServer.getId(),
                Image.class,
                "annotation_measurements.csv",
                annotationMeasurements
        ).get()

        if (status) {
            println "Annotation measurements sent"
        } else {
            println "Impossible to send annotation measurements. Check the logs"
        }
    } catch (IOException e) {
        println "Error when reading annotation measurements: " + e
    }
}

// Send detection measurements
if (sendDetectionMeasurements) {
    try (OutputStream outputStream = new ByteArrayOutputStream()) {
        // The image must be saved first because non saved measures won't be exported
        projectEntry.saveImageData(imageData)

        // Get detection measurements
        new MeasurementExporter()
                .exportType(PathDetectionObject.class)
                .imageList(List.of(projectEntry))
                .separator(",")
                .exportMeasurements(outputStream);
        def detectionMeasurements = outputStream.toString()

        // Send detection measurements
        def status = omeroServer.getClient().getApisHandler().sendAttachment(
                omeroServer.getId(),
                Image.class,
                "detection_measurements.csv",
                detectionMeasurements
        ).get()

        if (status) {
            println "Detection measurements sent"
        } else {
            println "Impossible to send detection measurements. Check the logs"
        }
    } catch (IOException e) {
        println "Error when reading detection measurements: " + e
    }
}

// Close server
omeroServer.close()