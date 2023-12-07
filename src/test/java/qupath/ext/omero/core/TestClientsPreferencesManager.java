package qupath.ext.omero.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import qupath.ext.omero.TestUtilities;

import java.net.URI;
import java.util.List;
import java.util.Optional;

public class TestClientsPreferencesManager {

    @BeforeEach
    void clearPreferences() {
        ClientsPreferencesManager.clearAllPreferences();
    }

    @Test
    void Check_URIs_Empty() {
        List<URI> expectedURIs = List.of();

        List<URI> uris = ClientsPreferencesManager.getURIs();

        TestUtilities.assertCollectionsEqualsWithoutOrder(expectedURIs, uris);
    }

    @Test
    void Check_URIs_Added() {
        URI uri = URI.create("https://github.com/qupath");
        List<URI> expectedURIs = List.of(uri);

        ClientsPreferencesManager.addURI(uri);

        List<URI> uris = ClientsPreferencesManager.getURIs();
        TestUtilities.assertCollectionsEqualsWithoutOrder(expectedURIs, uris);
    }

    @Test
    void Check_URIs_Removed() {
        URI uri = URI.create("https://github.com/qupath");
        List<URI> expectedURIs = List.of();

        ClientsPreferencesManager.addURI(uri);
        ClientsPreferencesManager.removeURI(uri);

        List<URI> uris = ClientsPreferencesManager.getURIs();
        TestUtilities.assertCollectionsEqualsWithoutOrder(expectedURIs, uris);
    }

    @Test
    void Check_Last_Server_URI_Empty() {
        String expectedLastURI = "";

        String lastURI = ClientsPreferencesManager.getLastServerURI();

        Assertions.assertEquals(expectedLastURI, lastURI);
    }

    @Test
    void Check_Last_Server_URI() {
        URI uri = URI.create("https://github.com/qupath");
        String expectedLastURI = uri.toString();
        ClientsPreferencesManager.addURI(uri);

        String lastURI = ClientsPreferencesManager.getLastServerURI();

        Assertions.assertEquals(expectedLastURI, lastURI);
    }

    @Test
    void Check_Last_Username_Empty() {
        String expectedLastUsername = "";

        String lastUsername = ClientsPreferencesManager.getLastUsername();

        Assertions.assertEquals(expectedLastUsername, lastUsername);
    }

    @Test
    void Check_Last_Username_When_Set() {
        String expectedUsername = "username";

        ClientsPreferencesManager.setLastUsername(expectedUsername);

        String lastUsername = ClientsPreferencesManager.getLastUsername();
        Assertions.assertEquals(expectedUsername, lastUsername);
    }

    @Test
    void Check_Last_Username_When_Set_Twice() {
        String unexpectedUsername = "unexpected username";
        String expectedUsername = "username";

        ClientsPreferencesManager.setLastUsername(unexpectedUsername);
        ClientsPreferencesManager.setLastUsername(expectedUsername);

        String lastUsername = ClientsPreferencesManager.getLastUsername();
        Assertions.assertEquals(expectedUsername, lastUsername);
    }

    @Test
    void Check_Ms_Pixel_Buffer_Port_Empty() {
        URI uri = URI.create("https://github.com/qupath");

        Optional<Integer> msPixelBufferPort = ClientsPreferencesManager.getMsPixelBufferPort(uri);

        Assertions.assertTrue(msPixelBufferPort.isEmpty());
    }

    @Test
    void Check_Ms_Pixel_Buffer_Port() {
        int expectedPort = 8080;
        URI uri = URI.create("https://github.com/qupath");
        ClientsPreferencesManager.setMsPixelBufferPort(uri, expectedPort);

        int msPixelBufferPort = ClientsPreferencesManager.getMsPixelBufferPort(uri).orElse(-1);

        Assertions.assertEquals(expectedPort, msPixelBufferPort);
    }

    @Test
    void Check_Ms_Pixel_Buffer_Port_When_Set_Twice() {
        int unexpectedPort = 1000;
        int expectedPort = 8080;
        URI uri = URI.create("https://github.com/qupath");
        ClientsPreferencesManager.setMsPixelBufferPort(uri, unexpectedPort);
        ClientsPreferencesManager.setMsPixelBufferPort(uri, expectedPort);

        int msPixelBufferPort = ClientsPreferencesManager.getMsPixelBufferPort(uri).orElse(-1);

        Assertions.assertEquals(expectedPort, msPixelBufferPort);
    }

    @Test
    void Check_Ms_Pixel_Buffer_Port_When_Other_URI_Set() {
        URI otherUri = URI.create("https://qupath.readthedocs.io");
        int otherPort = 1000;
        ClientsPreferencesManager.setMsPixelBufferPort(otherUri, otherPort);
        int expectedPort = 8080;
        URI uri = URI.create("https://github.com/qupath");
        ClientsPreferencesManager.setMsPixelBufferPort(uri, expectedPort);

        int msPixelBufferPort = ClientsPreferencesManager.getMsPixelBufferPort(uri).orElse(-1);

        Assertions.assertEquals(expectedPort, msPixelBufferPort);
    }

    @Test
    void Check_Web_Jpeg_Quality_Empty() {
        URI uri = URI.create("https://github.com/qupath");

        Optional<Float> jpegQuality = ClientsPreferencesManager.getWebJpegQuality(uri);

        Assertions.assertTrue(jpegQuality.isEmpty());
    }

    @Test
    void Check_Web_Jpeg_Quality() {
        float expectedJpegQuality = 0.46f;
        URI uri = URI.create("https://github.com/qupath");
        ClientsPreferencesManager.setWebJpegQuality(uri, expectedJpegQuality);

        float jpegQuality = ClientsPreferencesManager.getWebJpegQuality(uri).orElse(-1f);

        Assertions.assertEquals(expectedJpegQuality, jpegQuality);
    }

    @Test
    void Check_Web_Jpeg_Quality_When_Set_Twice() {
        float unexpectedJpegQuality = 0.12f;
        float expectedJpegQuality = 0.46f;
        URI uri = URI.create("https://github.com/qupath");
        ClientsPreferencesManager.setWebJpegQuality(uri, unexpectedJpegQuality);
        ClientsPreferencesManager.setWebJpegQuality(uri, expectedJpegQuality);

        float jpegQuality = ClientsPreferencesManager.getWebJpegQuality(uri).orElse(-1f);

        Assertions.assertEquals(expectedJpegQuality, jpegQuality);
    }

    @Test
    void Check_Web_Jpeg_Quality_When_Other_URI_Set() {
        URI otherUri = URI.create("https://qupath.readthedocs.io");
        float otherJpegQuality = 0.78f;
        ClientsPreferencesManager.setWebJpegQuality(otherUri, otherJpegQuality);
        float expectedJpegQuality = 0.46f;
        URI uri = URI.create("https://github.com/qupath");
        ClientsPreferencesManager.setWebJpegQuality(uri, expectedJpegQuality);

        float jpegQuality = ClientsPreferencesManager.getWebJpegQuality(uri).orElse(-1f);

        Assertions.assertEquals(expectedJpegQuality, jpegQuality);
    }
}
