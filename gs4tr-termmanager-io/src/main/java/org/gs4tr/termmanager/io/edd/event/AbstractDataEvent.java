package org.gs4tr.termmanager.io.edd.event;

import java.util.List;

import org.gs4tr.termmanager.io.edd.api.DataEvent;
import org.gs4tr.termmanager.io.edd.api.Event;
import org.gs4tr.termmanager.model.glossary.TermEntry;

/**
 * The {@link AbstractDataEvent} class serves as a base class for defining
 * custom events happening in TMGR. We have two types of events defined.
 * <ul>
 * <li>{@link ProcessDataEvent} - used when we want to store stream of term
 * entries in different systems, like NoSQL or RDBMS.</li>
 * <li>{@link RevertDataEvent} - used when we want to revert changes in
 * different systems, like NoSQL or RDBMS. Usually when writing into one of
 * those fails, and we want to revert changes from the other systems.</li>
 * </ul>
 * Events can be distinguished using the {@link #getType() getType} method.
 */
public class AbstractDataEvent implements DataEvent<TermEntry> {

    private String _collection;

    private List<TermEntry> _entries;

    protected AbstractDataEvent(String collection, List<TermEntry> entries) {
	_collection = collection;
	_entries = entries;
    }

    public String getCollection() {
	return _collection;
    }

    @Override
    public List<TermEntry> getData() {
	return _entries;
    }

    /**
     * Returns the event type as a {@link Class} object. This method is used by the
     * {@link org.gs4tr.termmanager.io.edd.api.EventDispatcher} to dispatch events
     * depending on their type.
     *
     * @return the AbstractDataEvent type as a {@link Class}.
     */
    public Class<? extends Event> getType() {
	return getClass();
    }
}
