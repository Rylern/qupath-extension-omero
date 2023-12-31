package qupath.ext.omero.gui.connectionsmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import qupath.ext.omero.core.ClientsPreferencesManager;
import qupath.ext.omero.core.WebClient;
import qupath.ext.omero.core.WebClients;
import qupath.ext.omero.gui.UiUtilities;

import java.net.URI;

/**
 * <p>
 *     The model of the connections manager. It contains lists which determine
 *     parts of the UI rendered by the connections manager.
 * </p>
 * <p>
 *     In effect, this class acts as an intermediate between a connections manager and a
 *     {@link WebClients WebClients} and a
 *     {@link ClientsPreferencesManager ClientsPreferencesManager}.
 *     Lists of these classes can be updated from any thread but the connections manager can
 *     only be accessed from the UI thread, so this class propagates changes made to these elements
 *     from any thread to the UI thread.
 * </p>
 */
class ConnectionsManagerModel {

    private static final ObservableList<URI> storedServersURIs = FXCollections.observableArrayList();
    private static final ObservableList<URI> storedServersURIsImmutable = FXCollections.unmodifiableObservableList(storedServersURIs);
    private static final ObservableList<WebClient> clients = FXCollections.observableArrayList();
    private static final ObservableList<WebClient> clientsImmutable = FXCollections.unmodifiableObservableList(clients);

    private ConnectionsManagerModel() {
        throw new RuntimeException("This class is not instantiable.");
    }

    static {
        UiUtilities.bindListInUIThread(storedServersURIs, ClientsPreferencesManager.getURIs());
        UiUtilities.bindListInUIThread(clients, WebClients.getClients());
    }

    /**
     * See {@link ClientsPreferencesManager#getURIs()}.
     */
    public static ObservableList<URI> getStoredServersURIs() {
        return storedServersURIsImmutable;
    }

    /**
     * See {@link WebClients#getClients()}.
     */
    public static ObservableList<WebClient> getClients() {
        return clientsImmutable;
    }
}
