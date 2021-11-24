package org.gs4tr.termmanager.service.comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.dto.DescriptionDifferences;
import org.gs4tr.termmanager.model.dto.LanguageModifications;
import org.gs4tr.termmanager.model.dto.TermDifferences;
import org.gs4tr.termmanager.model.dto.TermEntryDifferences;
import org.gs4tr.termmanager.model.dto.TermEntryModifications;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.junit.Test;
import org.springframework.util.StringUtils;

import junit.framework.Assert;

public class TermEntryComparatorTest {

    private static final String CATEGORY_TYPE = "category";

    private static final String CATEGORY_VALUE_1 = "small animal that says woof";

    private static final String CATEGORY_VALUE_2 = "small animal that barks";

    private static final String ENGLISH_TERM_NAME = "barbeque sauce";

    private static final boolean FORBIDDEN = false;

    private static final String FRENCH_TERM_NAME = "sauce barbecue";

    private static final String SOURCE_ID = Locale.US.getCode();

    private static final String STATUS_PROCESSED = ItemStatusTypeHolder.PROCESSED.getName();

    private static final String TARGET_ID = Locale.FRENCH.getCode();

    private static final String USER_NAME = "beko";

    @Test
    public void compareEmptyTermEntriesTest() {
	TermEntry previous = new TermEntry();
	TermEntry current = new TermEntry();
	assertNull(TermEntryComparator.INSTANCE.compare(previous, current));
    }

    @Test
    public void compareEqualTermEntriesTest() {
	TermEntry same = new TermEntry();
	same.setUserModified(USER_NAME);
	same.setDateModified(Instant.now().toEpochMilli());
	Term term = new Term(SOURCE_ID, ENGLISH_TERM_NAME, FORBIDDEN, STATUS_PROCESSED, USER_NAME);
	term.setUuId(UUID.randomUUID().toString());
	same.addTerm(term);

	assertNull(TermEntryComparator.INSTANCE.compare(same, same));
    }

    @Test(expected = NullPointerException.class)
    public void compareNullTermEntriesTest() {
	TermEntry previous = null;
	TermEntry current = null;
	TermEntryComparator.INSTANCE.compare(previous, current);
	Assert.fail("Fail to validate input parameters.");
    }

    // [issue TERII-4236: History doesn't display correct modification user]
    @Test
    public void compareTermEntriesCheckModificationUserTest() {
	String userModified1 = USER_NAME, userModified2 = "sdulin";

	TermEntry previous = new TermEntry();
	previous.setDateModified(Instant.now().toEpochMilli());
	previous.setUserModified(userModified1);
	TermEntry current = new TermEntry();
	current.setDateModified(Instant.now().toEpochMilli());
	current.setUserModified(userModified2);

	Date sourceDateModified = new Date(1), targetDateModified = new Date(2), targetDateModified1 = new Date(3);

	Term source = new Term(SOURCE_ID, ENGLISH_TERM_NAME, FORBIDDEN, STATUS_PROCESSED, userModified1);
	source.setDateModified(sourceDateModified.getTime());
	Term target = new Term(TARGET_ID, ENGLISH_TERM_NAME, Boolean.TRUE, STATUS_PROCESSED, userModified1);
	target.setDateModified(targetDateModified.getTime());
	Term target1 = new Term(TARGET_ID, ENGLISH_TERM_NAME, FORBIDDEN, STATUS_PROCESSED, userModified2);
	target1.setDateModified(targetDateModified1.getTime());

	current.addTerm(source);
	current.addTerm(target);
	current.addTerm(target1);

	TermEntryDifferences revision = TermEntryComparator.INSTANCE.compare(previous, current);
	assertEquals(userModified2, revision.getModificationUser());
    }

    @Test
    public void compareTermEntriesWithDiffrentTermEntryAttributesTest() {
	final String uuid = UUID.randomUUID().toString();
	Description pDescription = new Description(CATEGORY_TYPE, CATEGORY_VALUE_1);
	pDescription.setUuid(uuid);
	Description cDescription = new Description(CATEGORY_TYPE, CATEGORY_VALUE_2);
	cDescription.setUuid(uuid);

	TermEntry previous = new TermEntry();
	previous.setDateModified(Instant.now().toEpochMilli());
	previous.addDescription(pDescription);

	TermEntry current = new TermEntry();
	current.setDateModified(Instant.now().toEpochMilli());
	current.addDescription(cDescription);

	TermEntryDifferences revision = TermEntryComparator.INSTANCE.compare(previous, current);
	// Correct, only changes are in term entry attributes
	assertTrue(revision.getLanguagesModifications().isEmpty());

	TermEntryModifications termEntryModifications = revision.getTermEntryModifications();
	List<DescriptionDifferences> attrDiffs = termEntryModifications.getAttributesDifferences();

	assertEquals(1, attrDiffs.size());
    }

    @Test
    public void compareTermEntriesWithDiffrentTermsTest() {
	/*
	 * Test case: Between two saves of term entry, delete source language
	 * and add new target.
	 */
	TermEntry previous = new TermEntry();
	previous.setUserModified(USER_NAME);
	previous.setDateModified(Instant.now().toEpochMilli());
	Term source = new Term(SOURCE_ID, ENGLISH_TERM_NAME, FORBIDDEN, STATUS_PROCESSED, USER_NAME);
	source.setUuId(UUID.randomUUID().toString());
	previous.addTerm(source);
	previous.setUserModified(USER_NAME);

	TermEntry current = new TermEntry();
	Term target = new Term(TARGET_ID, FRENCH_TERM_NAME, FORBIDDEN, STATUS_PROCESSED, USER_NAME);
	target.setDateModified(new Date().getTime());
	current.addTerm(target);
	current.setUserModified(USER_NAME);

	TermEntryDifferences revision = TermEntryComparator.INSTANCE.compare(previous, current);

	assertEquals(Action.NOT_AVAILABLE.name(), revision.getAction());
	assertEquals(USER_NAME, revision.getModificationUser());

	assertNotNull(revision.getModificationDate());
	// Correct, only changes are in terms
	assertTrue(revision.getTermEntryModifications().getAttributesDifferences().isEmpty());

	Collection<LanguageModifications> lms = revision.getLanguagesModifications();
	assertEquals(2, lms.size());

	// Just try to find language modifications by languageId.
	LanguageModifications lm1 = findLanguageModificationsByLanguage(SOURCE_ID, lms);
	assertNotNull(lm1);
	LanguageModifications lm2 = findLanguageModificationsByLanguage(TARGET_ID, lms);
	assertNotNull(lm2);
    }

    /*
     * 12-December-2016, as per [Bug#TERII-4492]: In case it is first revision,
     * creation and modification dates must be the same. If not, create blank
     * revision with no highlight.
     */
    @Test
    public void compareTermsNoHighlightTest() {
	TermEntry previous = new TermEntry();
	Long currentDateCreated = Long.valueOf(1481633532526l);
	Long currentDateModified = Long.valueOf(1481633542537l);

	TermEntry current = new TermEntry();
	current.setRevisionId(1L);
	current.setDateCreated(currentDateCreated);
	current.setDateModified(currentDateModified);
	current.setUserModified(USER_NAME);

	Term source = new Term(SOURCE_ID, ENGLISH_TERM_NAME, FORBIDDEN, STATUS_PROCESSED, USER_NAME);
	source.addDescription(new Description(CATEGORY_TYPE, CATEGORY_VALUE_1));
	current.addTerm(source);

	TermEntryDifferences revision = TermEntryComparator.INSTANCE.compare(previous, current);

	assertEquals("History info N/A", revision.getAction());
	assertTrue(StringUtils.isEmpty(revision.getModificationUser()));
	assertNull(revision.getModificationDate());

	assertTrue(CollectionUtils.isEmpty(revision.getTermEntryModifications().getAttributesDifferences()));

	Collection<LanguageModifications> languagesModifications = revision.getLanguagesModifications();
	assertEquals(1, languagesModifications.size());

	LanguageModifications lm = languagesModifications.iterator().next();
	assertEquals(lm.getLanguage().getLanguageId(), SOURCE_ID);

	List<TermDifferences> termsDifferences = lm.getTermsDifferences();
	assertEquals(1, termsDifferences.size());

	TermDifferences termDiff = termsDifferences.get(0);

	// Expected because this revision should not be highlighted
	assertTrue(CollectionUtils.isEmpty(termDiff.getDifferences()));

	// But still, term name will be sent in response.
	assertEquals(ENGLISH_TERM_NAME, termDiff.getValue());
	assertEquals(STATUS_PROCESSED, termDiff.getNewStatus());
	assertEquals(STATUS_PROCESSED, termDiff.getOldStatus());

	List<DescriptionDifferences> attributesDifferences = termDiff.getAttributesDifferences();
	assertEquals(1, attributesDifferences.size());

	DescriptionDifferences attrDiff = attributesDifferences.get(0);

	// Expected, term note/attribute's should not be highlighted
	assertTrue(CollectionUtils.isEmpty(attrDiff.getDifferences()));

	assertEquals(CATEGORY_VALUE_1, attrDiff.getValue());
    }

    private LanguageModifications findLanguageModificationsByLanguage(String languageId,
	    Collection<LanguageModifications> lms) {
	return lms.stream().filter(lm -> languageId.equals(lm.getLanguage().getLanguageId())).findFirst().orElse(null);
    }
}