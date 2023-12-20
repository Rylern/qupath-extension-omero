package qupath.ext.omero.core.pixelapis.web;

import org.junit.jupiter.api.*;
import qupath.ext.omero.OmeroServer;
import qupath.ext.omero.core.WebClient;
import qupath.ext.omero.core.WebClients;
import qupath.ext.omero.core.pixelapis.PixelAPIReader;
import qupath.ext.omero.imagesserver.OmeroImageServer;
import qupath.ext.omero.imagesserver.OmeroImageServerBuilder;
import qupath.lib.analysis.stats.Histogram;
import qupath.lib.common.ColorTools;
import qupath.lib.images.servers.ImageServerMetadata;
import qupath.lib.images.servers.TileRequest;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class TestWebReader extends OmeroServer {

    abstract static class GenericImage {

        protected static WebClient client;
        protected static TileRequest tileRequest;
        protected static PixelAPIReader reader;

        @AfterAll
        static void removeClient() throws Exception {
            reader.close();
            WebClients.removeClient(client);
        }

        @Test
        void Check_Image_Can_Be_Read() throws IOException {
            BufferedImage image = reader.readTile(tileRequest);

            Assertions.assertNotNull(image);
        }

        @Test
        abstract void Check_Image_Histogram() throws IOException;
    }

    @Nested
    class RgbImage extends GenericImage {

        @BeforeAll
        static void createClient() throws ExecutionException, InterruptedException {
            client = OmeroServer.createAuthenticatedClient();

            ImageServerMetadata metadata;
            try (OmeroImageServer imageServer = (OmeroImageServer) new OmeroImageServerBuilder().buildServer(
                    OmeroServer.getRGBImageURI(),
                    "--pixelAPI", "Web",
                    "--jpegQuality", "1.0")
            ) {
                tileRequest = imageServer.getTileRequestManager().getTileRequest(0, 0, 0, 0, 0);

                metadata = imageServer.getMetadata();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            reader = client.getPixelAPI(WebAPI.class).createReader(
                    OmeroServer.getRGBImage().getId(),
                    metadata
            );
        }

        @Test
        @Override
        void Check_Image_Histogram() throws IOException {
            double expectedMean = OmeroServer.getRGBImageRedChannelMean();
            double expectedStdDev = OmeroServer.getRGBImageRedChannelStdDev();

            BufferedImage image = reader.readTile(tileRequest);

            Histogram histogram = new Histogram(
                    Arrays.stream(image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()))
                            .map(ColorTools::red)
                            .toArray(),
                    256,
                    Double.NaN,
                    Double.NaN
            );

            Assertions.assertEquals(expectedMean, histogram.getMeanValue(), 0.1);
            Assertions.assertEquals(expectedStdDev, histogram.getStdDev(), 0.1);
        }
    }

    @Nested
    class UInt8Image extends GenericImage {

        @BeforeAll
        static void createClient() throws ExecutionException, InterruptedException {
            client = OmeroServer.createAuthenticatedClient();

            ImageServerMetadata metadata;
            try (OmeroImageServer imageServer = (OmeroImageServer) new OmeroImageServerBuilder().buildServer(
                    OmeroServer.getUInt8ImageURI(),
                    "--pixelAPI", "Web",
                    "--jpegQuality", "1.0")
            ) {
                tileRequest = imageServer.getTileRequestManager().getTileRequest(0, 0, 0, 0, 0);

                metadata = imageServer.getMetadata();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            reader = client.getPixelAPI(WebAPI.class).createReader(
                    OmeroServer.getUInt8Image().getId(),
                    metadata
            );
        }

        @Test
        @Override
        void Check_Image_Histogram() throws IOException {
            double expectedMean = OmeroServer.getUInt8ImageRedChannelMean();
            double expectedStdDev = OmeroServer.getUInt8ImageRedChannelStdDev();

            BufferedImage image = reader.readTile(tileRequest);

            Histogram histogram = new Histogram(
                    Arrays.stream(image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()))
                            .map(ColorTools::red)
                            .toArray(),
                    256,
                    Double.NaN,
                    Double.NaN
            );

            Assertions.assertEquals(expectedMean, histogram.getMeanValue(), 3);
            Assertions.assertEquals(expectedStdDev, histogram.getStdDev(), 11);
        }
    }
}
