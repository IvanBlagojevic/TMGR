package org.gs4tr.termmanager.io.edd.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.io.exception.EventException;
import org.gs4tr.termmanager.io.tlog.impl.TransactionLogHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

@Component
public class EventDispatcher implements InitializingBean, DisposableBean {

    private static final Log LOGGER = LogFactory.getLog(EventDispatcher.class);

    private ListeningExecutorService _executor;

    @Resource(name = "handlersMap")
    private Map<Class<? extends Event>, List<Handler<? extends Event>>> _handlersMap;

    @Override
    public void afterPropertiesSet() throws Exception {
	_executor = MoreExecutors
		.listeningDecorator(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1));
    }

    @Override
    public void destroy() throws Exception {
	shutdownAndAwaitTermination();
    }

    /**
     * Dispatches an {@link Event} depending on it's type.
     *
     * @param event
     *            The {@link Event} to be dispatched.
     */
    @SuppressWarnings("unchecked")
    public <E extends Event> void dispatch(E event) throws Exception {
	List<Handler<? extends Event>> handlers = _handlersMap.get(event.getClass());
	if (Objects.isNull(handlers)) {
	    return;
	}

	List<ListenableFuture<Boolean>> futures = new ArrayList<>();

	CountDownLatch countDownLatch = new CountDownLatch(handlers.size());

	for (Handler<? extends Event> h : handlers) {
	    Handler<E> handler = (Handler<E>) h;

	    ListenableFuture<Boolean> future = _executor.submit(new Worker(event, handler, countDownLatch));
	    Futures.addCallback(future, new FutureCallback<Boolean>() {
		@Override
		public void onFailure(Throwable throwable) {
		    throw new EventException(throwable);
		}

		@Override
		public void onSuccess(Boolean success) {
		    LOGGER.info(String.format("CONSUMER Thread: [%s], has finished the task successfully!!!",
			    Thread.currentThread().getName()));
		}
	    });
	    futures.add(future);
	}

	countDownLatch.await();

	ListenableFuture<List<Boolean>> allAsList = Futures.allAsList(futures);
	allAsList.get();
    }

    private void awaitTermination() throws InterruptedException {
	if (!_executor.awaitTermination(30, TimeUnit.SECONDS)) {
	    _executor.shutdownNow();
	    if (!_executor.awaitTermination(30, TimeUnit.SECONDS)) {
		LOGGER.error("Executor did not terminate");
	    }
	}
    }

    private void shutdownAndAwaitTermination() {
	_executor.shutdown();
	try {
	    awaitTermination();
	} catch (Exception e) {
	    _executor.shutdownNow();
	    Thread.currentThread().interrupt();
	}
    }
}
