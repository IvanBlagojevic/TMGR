package org.gs4tr.termmanager.persistence.solr;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation3.solr.ICloudHttpSolrClient;
import org.gs4tr.termmanager.persistence.solr.util.RouteHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class RouteHelperTest extends AbstractSolrGlossaryTest {

    private static final Log LOGGER = LogFactory.getLog(RouteHelperTest.class);

    private ICloudHttpSolrClient _client;

    private ExecutorService _executor;

    @After
    public void cleanUp() {
	ExecutorService executor = getExecutor();
	if (executor != null && !executor.isShutdown()) {
	    shutdownAndAwaitTermination();
	}
    }

    @Before
    public void setUp() {
	_executor = Executors.newFixedThreadPool(10);
	_client = getRegularConnector().getClient();
    }

    @Test
    public void testGetRoute() {
	for (int i = 0; i < 100; i++) {
	    getExecutor().execute(getRoute(PROJECT_ID));
	}
    }

    private ICloudHttpSolrClient getClient() {
	return _client;
    }

    private ExecutorService getExecutor() {
	return _executor;
    }

    private Runnable getRoute(Long value) {
	return () -> {
	    String route = RouteHelper.getRoute(getClient(), "regular", value);
	    LOGGER.info(String.format("Thread name: %s. Route: %s", Thread.currentThread().getName(), route));
	};
    }

    private void shutdownAndAwaitTermination() {
	ExecutorService executor = getExecutor();
	executor.shutdown();
	try {
	    if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
		executor.shutdownNow();
		if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
		    LOGGER.error("Executor did not terminate");
		}
	    }
	} catch (Exception e) {
	    executor.shutdownNow();
	    Thread.currentThread().interrupt();
	}
    }
}
