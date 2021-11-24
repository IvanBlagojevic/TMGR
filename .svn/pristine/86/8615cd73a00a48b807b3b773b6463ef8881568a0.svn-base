package org.gs4tr.termmanager.dao.hibernate.backup;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.dao.backup.DbTermEntryDAO;
import org.gs4tr.termmanager.model.glossary.backup.converter.DbTermEntryConverter;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTerm;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryHistory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class DbTermEntryHistoryDAOTest extends DbTermEntryDAOTest {

    // Issue: TERII-5616
    @Ignore
    @Test
    public void testHistory() {
	DbTermEntryDAO dao = getDbTermEntryDAO();

	// save new entry
	String termEntryId = UUID.randomUUID().toString();
	DbTermEntry incoming = createTermEntry(termEntryId);

	DbTerm termSynonym = createTerm(incoming);
	termSynonym.setFirst(false);
	termSynonym.setName("term synonym".getBytes());
	incoming.getTerms().add(termSynonym);
	update(incoming);

	// check history
	DbTermEntry existing = dao.findByUuid(termEntryId, true);
	Set<DbTermEntryHistory> termEntryHistory = existing.getHistory();

	Assert.assertTrue(CollectionUtils.isNotEmpty(termEntryHistory));
	Assert.assertEquals(1, existing.getRevisionId().intValue());
	Assert.assertEquals(1, DbTermEntryConverter.getLastRevisionId(termEntryHistory));

	// disable synonym term
	DbTerm synonym = existing.getTerms().stream().filter(t -> t.getUuId().equals(termSynonym.getUuId())).findFirst()
		.get();
	synonym.setDisabled(true);
	synonym.setDateModified(new Date());
	existing.setDateModified(new Date());
	update(existing);

	// check history
	DbTermEntry afterUpdateEntry = dao.findByUuid(termEntryId, true);
	Set<DbTermEntryHistory> adterUpdateEntryHistory = afterUpdateEntry.getHistory();

	Assert.assertTrue(CollectionUtils.isNotEmpty(adterUpdateEntryHistory));
	Assert.assertEquals(2, afterUpdateEntry.getRevisionId().intValue());
	Assert.assertEquals(2, DbTermEntryConverter.getLastRevisionId(adterUpdateEntryHistory));
	Assert.assertEquals(2, adterUpdateEntryHistory.size());
    }

    // Issue: TERII-5616
    @Test
    public void testHistorySingleRevision() {
	DbTermEntryDAO dao = getDbTermEntryDAO();

	// save new entry
	String termEntryId = UUID.randomUUID().toString();
	DbTermEntry incoming = createTermEntry(termEntryId);

	DbTerm termSynonym = createTerm(incoming);
	termSynonym.setFirst(false);
	termSynonym.setName("term synonym".getBytes());
	incoming.getTerms().add(termSynonym);

	update(incoming);

	// check history
	DbTermEntry existing = dao.findByUuid(termEntryId, true);
	Set<DbTermEntryHistory> termEntryHistory = existing.getHistory();

	Assert.assertTrue(CollectionUtils.isNotEmpty(termEntryHistory));
	Assert.assertEquals(1, existing.getRevisionId().intValue());
	Assert.assertEquals(1, DbTermEntryConverter.getLastRevisionId(termEntryHistory));
    }

    private void update(DbTermEntry termEntry) {
	DbTermEntryDAO dao = getDbTermEntryDAO();
	dao.saveOrUpdateLocked(Arrays.asList(termEntry));
	dao.flush();
	dao.clear();
    }
}
