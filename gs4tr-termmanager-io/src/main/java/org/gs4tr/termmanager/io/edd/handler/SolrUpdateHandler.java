package org.gs4tr.termmanager.io.edd.handler;

import org.gs4tr.termmanager.io.edd.api.Handler;
import org.gs4tr.termmanager.io.config.IndexConnectionHandler;
import org.gs4tr.termmanager.io.edd.event.ProcessDataEvent;
import org.gs4tr.termmanager.io.exception.EventException;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.tm3.api.TmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SolrUpdateHandler extends AbstractHandler implements Handler<ProcessDataEvent> {

    @Autowired
    private IndexConnectionHandler _connectionHandler;

    @Override
    public void onEvent(ProcessDataEvent event) throws EventException {
        validate(event);
        logMessage(String.format("Writing [%d] entities into solr.", event.getData().size()));
	try {
            update(event);
	} catch (TmException e) {
	    throw new EventException(e.getMessage(), e);
	}
    }

    private void update(ProcessDataEvent event) throws TmException {
        ITmgrGlossaryConnector connector = getConnectionHandler().connect(event.getCollection());
        connector.getTmgrUpdater().update(event.getData());
    }

    private IndexConnectionHandler getConnectionHandler() {
	return _connectionHandler;
    }

    @Override
    protected void logMessage(String message) {
	LOGGER.info(message);
    }
}
