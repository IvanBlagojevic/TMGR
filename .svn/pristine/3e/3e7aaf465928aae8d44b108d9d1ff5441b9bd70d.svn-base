package org.gs4tr.termmanager.service.backup;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrInputDocument;
import org.gs4tr.foundation3.solr.model.SolrConstants;
import org.gs4tr.foundation3.solr.model.update.CommandEnum;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTerm;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryDescription;
import org.gs4tr.termmanager.persistence.solr.util.SolrGlossaryAdapter;
import org.gs4tr.termmanager.service.backup.converter.RegularBackupConverter;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;
import org.junit.Assert;
import org.junit.Test;

public class RegularBackupConverterTest {

    private static final String RS = SolrGlossaryAdapter.RS;

    /* TERII-4311 */
    @Test
    public void test_case1() {

	String id = UUID.randomUUID().toString();
	String termText = "term text";

	SolrInputDocument doc = new SolrInputDocument();
	doc.setField(SolrConstants.ID_FIELD, id);
	doc.setField(SolrParentDocFields.ATTRIBUTE_MULTI_STORE, CommandEnum.SET.getAtomicField(null));
	doc.setField(SolrParentDocFields.DATE_CREATED_INDEX_STORE, new Date().getTime());
	doc.setField(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE, new Date().getTime());

	doc.setField("en-US_" + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX, CommandEnum.SET.getAtomicField(termText));
	doc.setField("en-US" + "_ID_STRING_BASIC_SORT", CommandEnum.SET.getAtomicField(UUID.randomUUID().toString()));
	doc.setField(SolrParentDocFields.REVISION, 2);

	DbTermEntry dbEntry = RegularBackupConverter.convertToDbTermEntry(doc);
	Assert.assertNotNull(dbEntry);
	Assert.assertEquals(id, dbEntry.getUuId());

	Set<DbTerm> terms = dbEntry.getTerms();
	Assert.assertTrue(CollectionUtils.isNotEmpty(terms));

	for (DbTerm term : terms) {
	    Assert.assertEquals("en-US", term.getLanguageId());
	    Assert.assertEquals(termText, term.getNameAsString());
	}
    }

    /* TERII-4311 */
    @Test
    public void test_case2() {
	String id = UUID.randomUUID().toString();
	String termText = "term text";

	Set<String> descs = new HashSet<String>();
	addDescription(descs, "type", "value");

	SolrInputDocument doc = new SolrInputDocument();
	doc.setField(SolrConstants.ID_FIELD, id);
	doc.setField(SolrParentDocFields.ATTRIBUTE_MULTI_STORE, CommandEnum.SET.getAtomicField(descs));
	doc.setField("en-US_" + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX, CommandEnum.SET.getAtomicField(termText));
	doc.setField("en-US" + "_ID_STRING_BASIC_SORT", CommandEnum.SET.getAtomicField(UUID.randomUUID().toString()));
	doc.setField(SolrParentDocFields.REVISION, 1);

	doc.setField(SolrParentDocFields.DATE_CREATED_INDEX_STORE, new Date().getTime());
	doc.setField(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE, new Date().getTime());

	DbTermEntry dbEntry = RegularBackupConverter.convertToDbTermEntry(doc);
	Assert.assertNotNull(dbEntry);
	Assert.assertEquals(id, dbEntry.getUuId());

	Set<DbTermEntryDescription> descriptions = dbEntry.getDescriptions();
	Assert.assertTrue(CollectionUtils.isNotEmpty(descriptions));
	for (DbTermEntryDescription desc : descriptions) {
	    Assert.assertEquals("type", desc.getType());
	    Assert.assertEquals("value", desc.getValueAsString());
	}

	Set<DbTerm> terms = dbEntry.getTerms();
	Assert.assertTrue(CollectionUtils.isNotEmpty(terms));

	for (DbTerm term : terms) {
	    Assert.assertEquals("en-US", term.getLanguageId());
	    Assert.assertEquals(termText, term.getNameAsString());
	}
    }

    /* TERII-4311 */
    @Test
    public void test_case3() {
	String id = UUID.randomUUID().toString();
	String termText = "term text";

	Set<String> descs = new HashSet<String>();
	addDescription(descs, "type", "value");

	SolrInputDocument doc = new SolrInputDocument();
	doc.setField(SolrConstants.ID_FIELD, id);
	doc.setField(SolrParentDocFields.ATTRIBUTE_MULTI_STORE, CommandEnum.SET.getAtomicField(descs));
	doc.setField("en-US_" + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX, CommandEnum.SET.getAtomicField(termText));
	doc.setField("en-US" + "_ID_STRING_BASIC_SORT", CommandEnum.SET.getAtomicField(UUID.randomUUID().toString()));
	doc.setField(SolrParentDocFields.REVISION, 5);
	doc.setField(SolrParentDocFields.DATE_CREATED_INDEX_STORE, new Date().getTime());
	doc.setField(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE, new Date().getTime());

	String deletedTermId = "83158edd-7527-47b9-b9a6-f5e7d63d7655";

	List<String> deletedIds = new ArrayList<String>();
	deletedIds.add(deletedTermId);
	deletedIds.add(deletedTermId);
	doc.setField("fr-FR_deleted_STRING_STORE_MULTI", deletedIds);

	DbTermEntry dbEntry = RegularBackupConverter.convertToDbTermEntry(doc);
	Assert.assertNotNull(dbEntry);
	Assert.assertEquals(id, dbEntry.getUuId());
	
	Set<DbTerm> terms = dbEntry.getTerms();
	Assert.assertTrue(CollectionUtils.isNotEmpty(terms));
	
	for (DbTerm term : terms) {
	    if (term.getLanguageId().equals("fr-FR")) {
		Assert.assertNotNull(term.getUuId());
		Assert.assertNotNull(term.getDateModified());
		Assert.assertNotNull(term.getDateModified());
		Assert.assertEquals(deletedTermId, term.getNameAsString());
	    }
	}
    }

    private void addDescription(Set<String> descs, String type, String value) {

	String uuId = UUID.randomUUID().toString();

	StringBuilder builder = new StringBuilder();
	builder.append(type);
	builder.append(RS);
	builder.append(value);
	builder.append(RS);
	builder.append(uuId);

	descs.add(builder.toString());
    }
}
