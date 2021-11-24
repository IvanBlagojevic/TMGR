package org.gs4tr.termmanager.service.comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation3.core.diff.DiffMatchPatch.Operation;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.dto.DescriptionDifferences;
import org.gs4tr.termmanager.model.dto.Difference;
import org.gs4tr.termmanager.model.dto.TermDifferences;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.junit.Test;

import com.google.common.collect.Sets;

public class TermComparatorTest {

    private static final String CATEGORY_TYPE = "category";

    private static final String CONTEXT_TYPE = "context";

    private static final boolean FORBIDDEN = false;

    private static final String LANGUAGE_ID = Locale.US.getCode();

    private static final String NEW_ATTRIBUTE_VALUE = "small animal that barks";

    private static final String NEW_NOTE_VALUE = "this animal is tame";

    private static final String OLD_ATTRIBUTE_VALUE = "small animal that says woof";

    private static final String STATUS_BLACKLISTED = ItemStatusTypeHolder.BLACKLISTED.getName();

    private static final String STATUS_PROCESSED = ItemStatusTypeHolder.PROCESSED.getName();

    private static final String TERM_NAME_1 = "barbeque sauce";

    private static final String TERM_NAME_2 = "BBQ sauce";

    private static final String TERM_NAME_3 = "brown sauce";

    private static final String USER_NAME = "beko";

    @Test
    public void compareDiffrentTermsTest() {
	/*
	 * User changed: 1. term status from PROCESSED -> BLACKLISTED 2. term
	 * name form barbeque sauce -> BBQ sauce 3. delete old attribute and add
	 * new one 4. add new note
	 */
	Description previousAttribute = new Description(CATEGORY_TYPE, OLD_ATTRIBUTE_VALUE);
	previousAttribute.setUuid(UUID.randomUUID().toString());
	Description currentAttribute = new Description(CATEGORY_TYPE, NEW_ATTRIBUTE_VALUE);
	currentAttribute.setUuid(UUID.randomUUID().toString());
	Description newNote = new Description(Description.NOTE, CONTEXT_TYPE, NEW_NOTE_VALUE);
	newNote.setUuid(UUID.randomUUID().toString());

	String termUuid = UUID.randomUUID().toString();

	Term previous = new Term(LANGUAGE_ID, TERM_NAME_1, FORBIDDEN, STATUS_PROCESSED, USER_NAME);
	previous.addDescription(previousAttribute);
	previous.setUuId(termUuid);
	Term current = new Term(LANGUAGE_ID, TERM_NAME_2, FORBIDDEN, STATUS_BLACKLISTED, USER_NAME);
	current.addDescription(currentAttribute);
	current.addDescription(newNote);
	current.setUuId(termUuid);

	Set<Term> pTerms = Sets.newHashSet(previous);
	Set<Term> cTerms = Sets.newHashSet(current);

	Collection<TermDifferences> result = TermComparator.INSTANCE.compare(pTerms, cTerms);
	assertEquals(1, result.size());

	TermDifferences termDiff = result.iterator().next();

	assertEquals(STATUS_BLACKLISTED, termDiff.getNewStatus());
	assertEquals(STATUS_PROCESSED, termDiff.getOldStatus());

	List<Difference> diffs = termDiff.getDifferences();
	assertEquals(2, diffs.size());

	// Differences term name: barbequeBBQ sauce
	Iterator<Difference> each = diffs.iterator();
	Difference deleted = each.next();
	assertEquals(0, deleted.getStartIndex());
	assertEquals(8, deleted.getLength());
	assertEquals(Operation.DELETE.name(), deleted.getOperation());

	Difference inserted = each.next();
	assertEquals(8, inserted.getStartIndex());
	assertEquals(3, inserted.getLength());
	assertEquals(Operation.INSERT.name(), inserted.getOperation());

	List<DescriptionDifferences> attrsDiffs = termDiff.getAttributesDifferences();
	List<DescriptionDifferences> notesDiffs = termDiff.getNotesDifferences();

	assertEquals(2, attrsDiffs.size());
	assertEquals(1, notesDiffs.size());

	Iterator<DescriptionDifferences> eachNoteDifferences = notesDiffs.iterator();
	DescriptionDifferences noteDiff = eachNoteDifferences.next();
	assertEquals(CONTEXT_TYPE, noteDiff.getName());
	assertEquals(NEW_NOTE_VALUE, noteDiff.getValue());

	List<Difference> noteDifferences = noteDiff.getDifferences();
	assertEquals(1, noteDifferences.size());

	// Differences note value: this animal is tame
	Difference insertedNoteDiff = noteDifferences.get(0);
	assertEquals(0, insertedNoteDiff.getStartIndex());
	assertEquals(19, insertedNoteDiff.getLength());
	assertEquals(Operation.INSERT.name(), insertedNoteDiff.getOperation());

	Iterator<DescriptionDifferences> eachAttributeDifferences = attrsDiffs.iterator();

	DescriptionDifferences insertedAttributeDifferences = eachAttributeDifferences.next();
	assertEquals(CATEGORY_TYPE, insertedAttributeDifferences.getName());
	assertEquals(NEW_ATTRIBUTE_VALUE, insertedAttributeDifferences.getValue());

	Difference insertedDiffs = insertedAttributeDifferences.getDifferences().get(0);
	assertEquals(0, insertedDiffs.getStartIndex());
	assertEquals(23, insertedDiffs.getLength());
	assertEquals(Operation.INSERT.name(), insertedNoteDiff.getOperation());

	DescriptionDifferences deletedAttributeDifferences = eachAttributeDifferences.next();
	assertEquals(CATEGORY_TYPE, deletedAttributeDifferences.getName());
	assertEquals(OLD_ATTRIBUTE_VALUE, deletedAttributeDifferences.getValue());

	Difference deletedDiffs = deletedAttributeDifferences.getDifferences().get(0);
	assertEquals(0, deletedDiffs.getStartIndex());
	assertEquals(27, deletedDiffs.getLength());
	assertEquals(Operation.DELETE.name(), deletedDiffs.getOperation());
    }

    @Test
    public void compareEqualTermsTest() {
	Term term = new Term(LANGUAGE_ID, TERM_NAME_1, FORBIDDEN, STATUS_PROCESSED, USER_NAME);
	term.setUuId(UUID.randomUUID().toString());
	Set<Term> cTerms = Sets.newHashSet(term);
	Set<Term> pTerms = Sets.newHashSet(term);
	assertTrue(TermComparator.INSTANCE.compare(pTerms, cTerms).isEmpty());
    }

    @Test
    public void compareNullTermsTest() {
	Set<Term> cTerms = null;
	Set<Term> pTerms = null;
	/*
	 * Tries to handle null input gracefully. An exception will generally
	 * not be thrown for a null input.
	 */
	assertTrue(TermComparator.INSTANCE.compare(pTerms, cTerms).isEmpty());
    }

    @Test
    public void compareTermsCheckTermOrderTest() {
	Term main = new Term(LANGUAGE_ID, TERM_NAME_1, FORBIDDEN, STATUS_PROCESSED, USER_NAME);
	main.setFirst(Boolean.TRUE);

	Term synonym1 = new Term(LANGUAGE_ID, TERM_NAME_2, FORBIDDEN, STATUS_PROCESSED, USER_NAME);
	Term synonym2 = new Term(LANGUAGE_ID, TERM_NAME_3, FORBIDDEN, STATUS_PROCESSED, USER_NAME);

	Set<Term> currentTerms = Sets.newHashSet(synonym1, main, synonym2);

	Collection<TermDifferences> result = TermComparator.INSTANCE.compare(Collections.emptySet(), currentTerms);

	TermDifferences firstTermDiff = result.iterator().next();

	assertEquals(main.getName(), firstTermDiff.getValue());
    }

    @Test
    public void compareTermsCurrentTermIsDeletedTest() {
	Term previous = new Term(LANGUAGE_ID, TERM_NAME_1, FORBIDDEN, STATUS_PROCESSED, USER_NAME);
	previous.setUuId(UUID.randomUUID().toString());

	Set<Term> pTerms = Sets.newHashSet(previous);

	Collection<TermDifferences> result = TermComparator.INSTANCE.compare(pTerms, Collections.emptyList());
	assertEquals(1, result.size());

	TermDifferences termDiff = result.iterator().next();

	assertEquals(termDiff.getOldStatus(), termDiff.getNewStatus());

	assertTrue(termDiff.getAttributesDifferences().isEmpty());
	assertTrue(termDiff.getNotesDifferences().isEmpty());

	List<Difference> diffs = termDiff.getDifferences();
	assertEquals(1, diffs.size());

	Iterator<Difference> each = diffs.iterator();
	Difference deleted = each.next();
	assertEquals(0, deleted.getStartIndex());
	assertEquals(14, deleted.getLength());
	assertEquals(Operation.DELETE.name(), deleted.getOperation());
    }

    @Test
    public void compareTermsWithDiffrentDescriptionsTest() {
	String uuid = UUID.randomUUID().toString();
	Description previousDescription = new Description(CATEGORY_TYPE, OLD_ATTRIBUTE_VALUE);
	previousDescription.setUuid(uuid);
	Description currentDescription = new Description(CATEGORY_TYPE, NEW_ATTRIBUTE_VALUE);
	currentDescription.setUuid(uuid);

	String termUuid = UUID.randomUUID().toString();
	Term previous = new Term(LANGUAGE_ID, TERM_NAME_1, FORBIDDEN, STATUS_PROCESSED, USER_NAME);
	previous.setUuId(termUuid);
	previous.addDescription(previousDescription);

	Term current = new Term(LANGUAGE_ID, TERM_NAME_1, FORBIDDEN, STATUS_PROCESSED, USER_NAME);
	current.setUuId(termUuid);
	current.addDescription(currentDescription);

	Set<Term> pTerms = Sets.newHashSet(previous);
	Set<Term> cTerms = Sets.newHashSet(current);
	Collection<TermDifferences> result = TermComparator.INSTANCE.compare(pTerms, cTerms);
	assertEquals(1, result.size());

	TermDifferences termDiff = result.iterator().next();
	assertEquals(STATUS_PROCESSED, termDiff.getNewStatus());
	assertEquals(termDiff.getOldStatus(), termDiff.getNewStatus());
	assertEquals(TERM_NAME_1, termDiff.getValue());
	assertTrue(termDiff.getNotesDifferences().isEmpty());

	// Correct, terms have the same value (i.e same term names)
	assertTrue(termDiff.getDifferences().isEmpty());

	List<DescriptionDifferences> attrDiffs = termDiff.getAttributesDifferences();
	assertEquals(1, attrDiffs.size());

	DescriptionDifferences attributeDifferences = attrDiffs.get(0);
	assertEquals(CATEGORY_TYPE, attributeDifferences.getName());

	List<Difference> diffs = attributeDifferences.getDifferences();
	assertEquals(2, diffs.size());

	Iterator<Difference> each = diffs.iterator();
	Difference deleted = each.next();
	assertEquals(18, deleted.getStartIndex());
	assertEquals(9, deleted.getLength());
	assertEquals(Operation.DELETE.name(), deleted.getOperation());

	Difference inserted = each.next();
	assertEquals(27, inserted.getStartIndex());
	assertEquals(5, inserted.getLength());
	assertEquals(Operation.INSERT.name(), inserted.getOperation());
    }

    @Test
    public void compareTermsWithDiffrentTermNamesTest() {
	String termUuid = UUID.randomUUID().toString();
	Term previous = new Term(LANGUAGE_ID, TERM_NAME_1, FORBIDDEN, STATUS_PROCESSED, USER_NAME);
	previous.setUuId(termUuid);
	Term current = new Term(LANGUAGE_ID, TERM_NAME_2, FORBIDDEN, STATUS_PROCESSED, USER_NAME);
	current.setUuId(termUuid);

	Set<Term> pTerms = Sets.newHashSet(previous);
	Set<Term> cTerms = Sets.newHashSet(current);
	Collection<TermDifferences> result = TermComparator.INSTANCE.compare(pTerms, cTerms);
	assertEquals(1, result.size());

	TermDifferences termDiff = result.iterator().next();

	assertEquals(termDiff.getOldStatus(), termDiff.getNewStatus());

	assertTrue(termDiff.getAttributesDifferences().isEmpty());
	assertTrue(termDiff.getNotesDifferences().isEmpty());

	List<Difference> diffs = termDiff.getDifferences();
	assertEquals(2, diffs.size());

	// Differences text value: barbequeBBQ sauce
	Iterator<Difference> each = diffs.iterator();
	Difference deleted = each.next();
	assertEquals(0, deleted.getStartIndex());
	assertEquals(8, deleted.getLength());
	assertEquals(Operation.DELETE.name(), deleted.getOperation());

	Difference inserted = each.next();
	assertEquals(8, inserted.getStartIndex());
	assertEquals(3, inserted.getLength());
	assertEquals(Operation.INSERT.name(), inserted.getOperation());
    }

    @Test
    public void compareTermsWithDiffrentTermStatusTest() {
	final String uuid = UUID.randomUUID().toString();
	Term previous = new Term(LANGUAGE_ID, TERM_NAME_1, FORBIDDEN, STATUS_PROCESSED, USER_NAME);
	previous.setUuId(uuid);
	Term current = new Term(LANGUAGE_ID, TERM_NAME_1, FORBIDDEN, STATUS_BLACKLISTED, USER_NAME);
	current.setUuId(uuid);

	Set<Term> cTerms = Sets.newHashSet(current);
	Set<Term> pTerms = Sets.newHashSet(previous);

	Collection<TermDifferences> result = TermComparator.INSTANCE.compare(pTerms, cTerms);
	assertEquals(1, result.size());

	TermDifferences termDiff = result.iterator().next();
	assertEquals(TERM_NAME_1, termDiff.getValue());
	assertEquals(STATUS_PROCESSED, termDiff.getOldStatus());
	assertEquals(STATUS_BLACKLISTED, termDiff.getNewStatus());

	// Correct, terms have the same value (i.e same term names)
	assertTrue(termDiff.getDifferences().isEmpty());
	assertTrue(termDiff.getAttributesDifferences().isEmpty());
	assertTrue(termDiff.getNotesDifferences().isEmpty());
    }
}
