package org.gs4tr.termmanager.service.reindex;

import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.junit.Assert;
import org.junit.Test;

public class RestoreProcessorV2Test extends AbstractReIndex {

    @Test
    public void testReIndex() throws Exception {
	long num = getNumberOfEntries();
	Assert.assertEquals(0, num);

	long totalCount = getDbTermEntryService().getTotalCount(null);
	Assert.assertTrue(totalCount > 0);

	getRestoreProcessorV2().restore();

	num = getNumberOfEntries();
	Assert.assertEquals(totalCount, num);
    }

    private long getNumberOfEntries() throws Exception {
	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(PROJECT_ID);
	return getBrowser().getNumberOfTermEntriesOnProject(filter);
    }
}
