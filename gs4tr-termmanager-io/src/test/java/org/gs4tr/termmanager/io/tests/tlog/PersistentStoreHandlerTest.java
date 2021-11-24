package org.gs4tr.termmanager.io.tests.tlog;

import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.gs4tr.termmanager.io.tlog.config.PersistentStoreHandler;
import org.gs4tr.termmanager.model.test.groups.SolrTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import jetbrains.exodus.entitystore.PersistentEntityStore;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:org/gs4tr/termmanager/dao/spring/applicationContext-dao.xml",
	"classpath:org/gs4tr/termmanager/io/spring/test/applicationContext-hibernate.xml",
	"classpath:org/gs4tr/termmanager/io/spring/test/applicationContext-init.xml",
	"classpath:org/gs4tr/termmanager/io/spring/applicationContext-io.xml" }, loader = TestEnvironmentAwareContextLoader.class)
@Transactional
@Category(SolrTest.class)
public class PersistentStoreHandlerTest {

    private static final Long PROJECT_ID = 1L;

    @Autowired
    private PersistentStoreHandler _handler;

    @Before
    public void setUp() {
	PersistentEntityStore store = getHandler().getOrOpen(PROJECT_ID);
	Assert.assertNotNull(store);
    }

    @After
    public void tearDown() {
	getHandler().closeAndClear(PROJECT_ID);
    }

    @Test
    public void testStoreOpeningAndClosing() {
	PersistentEntityStore store = getHandler().getOrOpen(PROJECT_ID);
	Assert.assertNotNull(store);

	getHandler().closeAndClear(PROJECT_ID);
    }

    private PersistentStoreHandler getHandler() {
	return _handler;
    }
}
