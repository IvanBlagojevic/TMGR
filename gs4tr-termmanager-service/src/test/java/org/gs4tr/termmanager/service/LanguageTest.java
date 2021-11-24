package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.Language;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/*
 * Language class is  designed to be thread-safe and to improve performance of
 * {@code Locale} class. In these tests i am trying to prove that. 
 * All tests performs parallel operations on Language class trying to break data consistency.
 * First request takes approximately 50-60ms to get all available languages, 
 * every additional request takes less than 1ms.  
 * 
 * NOTE: These results are from tests that have been executed on my local machine. 
 * 
 */
public class LanguageTest {

    private static final int LIMIT = 10;

    private static final int LOCALE_LENGTH = Locale.getAvailableLocales().length;

    private static final Locale[] LOCALES = Locale.getAvailableLocales();

    private ExecutorService _executor;

    @After
    public void cleanUp() {
	if (Objects.nonNull(getExecutor()) && !getExecutor().isShutdown()) {
	    shutdownAndAwaitTermination();
	}
    }

    @Test
    public void getAllAvailableLanguagesSizeInParallelTest() throws InterruptedException {

	List<Callable<Integer>> tasks = IntStream.rangeClosed(1, LIMIT).mapToObj(k -> getLangugesLenghtCallable())
		.collect(Collectors.toList());

	List<Future<Integer>> futures = getExecutor().invokeAll(tasks, LIMIT, TimeUnit.MINUTES);

	for (Future<Integer> future : futures) {
	    try {
		Integer length = future.get();
		assertEquals(LOCALE_LENGTH, length.intValue());
	    } catch (ExecutionException e) {
		throw new RuntimeException(e);
	    }
	}
	shutdownAndAwaitTermination();
    }

    @Before
    public void setUp() {
	_executor = Executors.newFixedThreadPool(LIMIT);
    }

    @Test
    public void valueOfAndGetAllAvailableLanguagesInParallelTest() throws InterruptedException, ExecutionException {

	List<Callable<Language>> tasks = IntStream.rangeClosed(1, LIMIT).mapToObj(k -> getValueOfCallable("zu-ZA"))
		.collect(Collectors.toList());

	List<Future<Language>> futures = getExecutor().invokeAll(tasks);

	Collection<Language> languages = Language.getAllAvailableLanguages();
	assertEquals(LOCALE_LENGTH, languages.size());

	languages.forEach((Assert::assertNotNull));

	for (Future<Language> future : futures) {
	    Language language = future.get();
	    assertNotNull(language);
	    assertNotNull(language.getDisplayName());
	    assertNotNull(language.getLanguageId());
	    assertNotNull(language.getLanguageAlignment());
	}
	shutdownAndAwaitTermination();
    }

    @Test
    public void valueOfInParallelTest() throws InterruptedException {

	List<Callable<Language>> tasks = IntStream.rangeClosed(1, LIMIT)
		.mapToObj(i -> getValueOfCallable(LOCALES[i].getCode())).collect(Collectors.toList());

	List<Future<Language>> futures = getExecutor().invokeAll(tasks, LIMIT, TimeUnit.MINUTES);

	for (Future<Language> future : futures) {
	    try {
		Language language = future.get();
		assertNotNull(language);
		assertNotNull(language.getDisplayName());
		assertNotNull(language.getLanguageId());
		assertNotNull(language.getLanguageAlignment());
	    } catch (ExecutionException e) {
		throw new RuntimeException(e);
	    }
	}
	shutdownAndAwaitTermination();
    }

    private void awaitTermination() throws InterruptedException {
	if (!getExecutor().awaitTermination(30, TimeUnit.SECONDS)) {
	    getExecutor().shutdownNow();
	    if (!getExecutor().awaitTermination(30, TimeUnit.SECONDS)) {
		System.err.println("Executor did not terminate");
	    }
	}
    }

    private ExecutorService getExecutor() {
	return _executor;
    }

    private Callable<Integer> getLangugesLenghtCallable() {
	return () -> Language.getAllAvailableLanguages().size();
    }

    private Callable<Language> getValueOfCallable(String code) {
	return () -> Language.valueOf(code);
    }

    private void shutdownAndAwaitTermination() {
	getExecutor().shutdown();
	try {
	    awaitTermination();
	} catch (Exception e) {
	    getExecutor().shutdownNow();
	    Thread.currentThread().interrupt();
	}
    }
}
