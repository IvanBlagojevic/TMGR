package org.gs4tr.termmanager.io.edd.api;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.io.exception.EventException;

public class Worker<E extends Event> implements Callable<Boolean> {

    private static final Log LOGGER = LogFactory.getLog(Worker.class);

    private CountDownLatch countDownLatch;

    private E event;

    private Handler<E> handler;

    public Worker(E event, Handler<E> handler, CountDownLatch countDownLatch) {
	this.event = event;
	this.countDownLatch = countDownLatch;
	this.handler = handler;
    }

    @Override
    public Boolean call() throws Exception {
	LOGGER.info("Starting event handling...");

	try {
	    handler.onEvent(event);
	} catch (EventException e) {
	    throw new EventException(e);
	} finally {
	    countDownLatch.countDown();
	}

	LOGGER.info(String.format("Thread [%s] has finished event handling!!!", Thread.currentThread().getName()));
	return Boolean.TRUE;
    }
}
