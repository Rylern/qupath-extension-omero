package qupath.ext.omero.core.entities.repositoryentities;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;
import qupath.ext.omero.core.entities.permissions.Group;
import qupath.ext.omero.core.entities.permissions.Owner;

import java.util.function.Predicate;

/**
 * An element belonging to the OMERO entity hierarchy.
 */
public interface RepositoryEntity {

    /**
     * @return whether this entity has children
     */
    boolean hasChildren();

    /**
     * <p>Returns the list of children of this element.</p>
     * <p>
     *     Usually, the initial call to this function returns an empty list but
     *     starts populating it in the background, so changes to this list should
     *     be listened. The {@link #isPopulatingChildren()} function indicates
     *     if the populating process is currently happening.
     * </p>
     * <p>This list may be updated from any thread.</p>
     *
     * @return an unmodifiable list of children of this element
     */
    ObservableList<? extends RepositoryEntity> getChildren();

    /**
     * @return a read only property describing the entity. This property may be updated from any thread
     */
    ReadOnlyStringProperty getLabel();

    /**
     * Indicates if this entity belongs to the provided group, the provided owner,
     * and match the provided predicate based on its label.
     *
     * @param groupFilter  the group the entity should belong to
     * @param ownerFilter  the owner the entity should belong to
     * @param labelPredicate  the predicate (based on the entity label) the entity should fulfil
     * @return whether this entity matches all the filters
     */
    boolean isFilteredByGroupOwnerName(Group groupFilter, Owner ownerFilter, Predicate<RepositoryEntity> labelPredicate);

    /**
     * @return whether this entity is currently populating its children
     */
    boolean isPopulatingChildren();
}