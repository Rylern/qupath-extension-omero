package qupath.ext.omero.core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *     Utility classes that monitors all active connections to servers.
 * </p>
 * <p>
 *     {@link WebClient Webclients} should be created and removed from this class.
 * </p>
 */
public class WebClients {

    private static final Logger logger = LoggerFactory.getLogger(WebClients.class);
    private static final ObservableList<WebClient> clients = FXCollections.observableArrayList();
    private static final ObservableList<WebClient> clientsImmutable = FXCollections.unmodifiableObservableList(clients);
    private static final Set<URI> clientsBeingCreated = ConcurrentHashMap.newKeySet();
    private enum Operation {
        ADD,
        REMOVE
    }

    private WebClients() {
        throw new AssertionError("This class is not instantiable.");
    }

    /**
     * <p>
     *     Create a WebClient from the server URI provided. Basically, this function will
     *     call {@link WebClient#create(URI, boolean, String...) WebClient.create()}
     *     and internally stores the newly created client.
     * </p>
     * <p>
     *     Note that this function is not guaranteed to create a valid client. Call the
     *     {@link WebClient#getStatus()} function to check the validity of the returned client
     *     before using it.
     * </p>
     * <p>This function is asynchronous.</p>
     *
     * @param url  the URL of the server. It doesn't have to be the base URL of the server
     * @param canSkipAuthentication  whether authentication can be skipped if the server allows it
     * @param args  optional arguments to login. See {@link WebClient#create(URI, boolean, String...) WebClient.create()}
     * @return a CompletableFuture with the client
     */
    public static CompletableFuture<WebClient> createClient(String url, boolean canSkipAuthentication, String... args) {
        var serverURI = getServerURI(url);

        if (serverURI.isPresent()) {
            var existingClient = getExistingClient(serverURI.get());

            return existingClient.map(CompletableFuture::completedFuture).orElseGet(() -> {
                if (clientsBeingCreated.contains(serverURI.get())) {
                    logger.warn(String.format("Client for %s already being created", serverURI.get()));
                    return CompletableFuture.completedFuture(WebClient.createInvalidClient(WebClient.FailReason.ALREADY_CREATING));
                } else {
                    clientsBeingCreated.add(serverURI.get());

                    return WebClient.create(serverURI.get(), canSkipAuthentication, args).thenApply(client -> {
                        if (client.getStatus().equals(WebClient.Status.SUCCESS)) {
                            ClientsPreferencesManager.addURI(client.getApisHandler().getWebServerURI());
                            updateClients(client, Operation.ADD);
                        }
                        clientsBeingCreated.remove(serverURI.get());

                        return client;
                    });
                }
            });
        } else {
            return CompletableFuture.completedFuture(WebClient.createInvalidClient(WebClient.FailReason.INVALID_URI_FORMAT));
        }
    }

    /**
     * <p>
     *     Synchronous version of {@link #createClient(String, boolean, String...)} that calls
     *     {@link WebClient#createSync(URI, boolean, String...) WebClient.createSync()}.
     * </p>
     * <p>
     *     Note that this function is not guaranteed to create a valid client. Call the
     *     {@link WebClient#getStatus()} function to check the validity of the returned client
     *     before using it.
     * </p>
     * <p>This function may block the calling thread for around a second.</p>
     */
    public static WebClient createClientSync(String url, boolean canSkipAuthentication, String... args) {
        var serverURI = getServerURI(url);

        if (serverURI.isPresent()) {
            var existingClient = getExistingClient(serverURI.get());

            if (existingClient.isEmpty()) {
                if (clientsBeingCreated.contains(serverURI.get())) {
                    logger.warn(String.format("Client for %s already being created", serverURI.get()));
                    return WebClient.createInvalidClient(WebClient.FailReason.ALREADY_CREATING);
                } else {
                    clientsBeingCreated.add(serverURI.get());

                    var client = WebClient.createSync(serverURI.get(), canSkipAuthentication, args);
                    if (client.getStatus().equals(WebClient.Status.SUCCESS)) {
                        ClientsPreferencesManager.addURI(client.getApisHandler().getWebServerURI());
                        updateClients(client, Operation.ADD);
                    }
                    clientsBeingCreated.remove(serverURI.get());

                    return client;
                }
            } else {
                return existingClient.get();
            }
        } else {
            return WebClient.createInvalidClient(WebClient.FailReason.INVALID_URI_FORMAT);
        }
    }

    /**
     * Close the given client connection.
     *
     * @param client  the client to remove
     */
    public static void removeClient(WebClient client) {
        try {
            client.close();
        } catch (Exception e) {
            logger.error("Error when closing web client", e);
        }
        updateClients(client, Operation.REMOVE);
    }

    /**
     * <p>Returns an unmodifiable list of all connected (but not necessarily authenticated) clients.</p>
     * <p>This list may be updated from any thread.</p>
     *
     * @return the connected clients
     */
    public static ObservableList<WebClient> getClients() {
        return clientsImmutable;
    }

    private static synchronized void updateClients(WebClient client, Operation operation) {
        if (operation.equals(Operation.ADD)) {
            clients.add(client);
        } else {
            clients.remove(client);
        }
    }

    private static Optional<URI> getServerURI(String url) {
        var uri = WebUtilities.createURI(url);
        if (uri.isPresent()) {
            return WebUtilities.getServerURI(uri.get());
        } else {
            return Optional.empty();
        }
    }

    private static Optional<WebClient> getExistingClient(URI uri) {
        return clients.stream().filter(e -> e.getApisHandler().getWebServerURI().equals(uri)).findAny();
    }
}
