package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.reindex.BackupSearchCommand;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DbTermEntryServiceTest extends AbstractSolrGlossaryTest {

    @Autowired
    private DbTermEntryService _dbTermEntryService;

    @Before
    public void prepare() throws Exception {
	populateRegularEntries();
	populateSubmissionEntries();
    }

    @Test
    public void testGetDbTermEntries() throws Exception {
	PagedList<DbTermEntry> page = getDbTermEntryService().getDbTermEntries(new PagedListInfo(),
		new BackupSearchCommand(null, null));
	Assert.assertNotNull(page);
	assertTrue(ArrayUtils.isNotEmpty(page.getElements()));
    }

    @Test
    public void testGetTotalCount() throws Exception {
	long totalCount = getDbTermEntryService().getTotalCount(Collections.EMPTY_LIST);
	assertTrue(totalCount >= 0);
    }

    private DbTermEntryService getDbTermEntryService() {
	return _dbTermEntryService;
    }
}
