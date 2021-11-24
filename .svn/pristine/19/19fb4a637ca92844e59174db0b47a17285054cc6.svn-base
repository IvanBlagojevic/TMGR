package org.gs4tr.termmanager.persistence.solr;

import java.util.HashSet;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;
import org.gs4tr.foundation3.solr.model.SolrConstants;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.util.SolrGlossaryAdapter;
import org.gs4tr.termmanager.solr.plugin.utils.SolrDocHelper;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;
import org.junit.Assert;
import org.junit.Test;

public class SolrGlossaryRequestAdapterTest extends AbstractSolrGlossaryTest {

    @Test
    public void extractNumberTest() {
	String fieldName = "en-US121_ID_STRING_STORE";
	int num = SolrDocHelper.extractNumber(fieldName);
	Assert.assertTrue(num > 0);
	Assert.assertEquals(121, num);
    }

    @Test
    public void test_buildInputDocumentFromTermEntry() {
	SolrGlossaryAdapter adapter = getNewAddapterInstance();

	String type1 = "context";
	String value1 = "This is some dummy term attribute context";
	Description description1 = createDescription(type1, value1);

	String type2 = "definition";
	String value2 = "This is some dummy term attribute definition";
	Description description2 = createDescription(type2, value2);

	String languageId = "sr";
	String name = "Dummy term text";
	boolean isFirst = true;
	boolean forbidden = false;
	String status = "PROCESSED";
	String user = "user";

	Term term1 = createTerm(languageId, name, isFirst, forbidden, status, user);
	term1.addDescription(description1);
	term1.addDescription(description2);

	String nameSynonym = "Synonym dummy term text";
	Term term1Synonym = createTerm(languageId, nameSynonym, false, forbidden, status, user);

	String languageId2 = "en";
	String name2 = "Dummy term text target";
	Term term2 = createTerm(languageId2, name2, false, forbidden, status, user);

	Set<Term> terms = new HashSet<Term>();
	terms.add(term1);
	terms.add(term1Synonym);
	terms.add(term2);

	String type3 = "custom";
	String value3 = "This is some dummy termEntry custom attribute";
	Description description3 = createDescription(type3, value3);

	String shortCode = "PRO000001";
	long projectId = 1l;

	TermEntry termEntry = new TermEntry(projectId, shortCode, user, terms);
	termEntry.addDescription(description3);

	SolrInputDocument parentDoc = adapter.buildInputDocumentFromTermEntry(termEntry);
	Assert.assertNotNull(parentDoc);
	Assert.assertEquals(termEntry.getUuId(), parentDoc.getFieldValue(SolrConstants.ID_FIELD));
	Assert.assertEquals(termEntry.getAction().name(), parentDoc.getFieldValue(SolrParentDocFields.HISTORY_ACTION));
	Assert.assertEquals(shortCode, parentDoc.getFieldValue(SolrParentDocFields.SHORTCODE_INDEX_STORE));
	Assert.assertEquals(termEntry.getDateCreated(), parentDoc.getFieldValue(SolrParentDocFields.DATE_CREATED_INDEX_STORE));
	Assert.assertEquals(termEntry.getDateModified(), parentDoc.getFieldValue(SolrParentDocFields.DATE_MODIFIED_INDEX_STORE));
	Assert.assertEquals(user, parentDoc.getFieldValue(SolrParentDocFields.USER_CREATED_INDEX));
	Assert.assertEquals("user", parentDoc.getFieldValue(SolrParentDocFields.USER_MODIFIED_SORT));
    }

    private Term createTerm(String languageId, String name, boolean isFirst, boolean forbidden, String status,
	    String user) {
	Term term = new Term();
	term.setLanguageId(languageId);
	term.setName(name);
	term.setFirst(isFirst);
	term.setForbidden(forbidden);
	term.setStatus(status);
	term.setUserCreated(user);
	term.setUserModified(user);
	return term;
    }

    private SolrGlossaryAdapter getNewAddapterInstance() {
	return new SolrGlossaryAdapter();
    }
}
