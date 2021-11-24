package org.gs4tr.termmanager.service.comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation3.core.diff.DiffMatchPatch.Operation;
import org.gs4tr.termmanager.model.dto.DescriptionDifferences;
import org.gs4tr.termmanager.model.dto.Difference;
import org.gs4tr.termmanager.model.glossary.Description;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;

public class DescriptionComparatorTest {

    private static final String CATEGORY_TYPE = "category";

    private static final String CATEGORY_VALUE_1 = "food related";

    private static final String CATEGORY_VALUE_2 = "small animal that says woof";

    private static final String CATEGORY_VALUE_3 = "small animal that barks";

    private static final String CONTEXT_TYPE = "context";

    private static final String CONTEXT_VALUE = "this animal is tame";

    private static final int START_INDEX_ZERO = 0;

    @Test(expected = IllegalArgumentException.class)
    public void compareDescriptionsWithDiffrentBaseTypeTest() {
	final String uuid = UUID.randomUUID().toString();

	Description previous = new Description(Description.NOTE, CATEGORY_TYPE, StringUtils.EMPTY);
	previous.setUuid(uuid);
	Description current = new Description(Description.ATTRIBUTE, CATEGORY_TYPE, CATEGORY_VALUE_1);
	current.setUuid(uuid);

	Set<Description> pDescriptions = Sets.newHashSet(previous);
	Set<Description> cDescriptions = Sets.newHashSet(current);

	DescriptionComparator.INSTANCE.compare(pDescriptions, cDescriptions);

	Assert.fail("Fail to validate input parameters.");
    }

    @Test
    public void compareDifferentDescriptionsTest() {
	final String uuid = UUID.randomUUID().toString();

	Description previous = new Description(CATEGORY_TYPE, CATEGORY_VALUE_2);
	previous.setUuid(uuid);
	Description current = new Description(CATEGORY_TYPE, CATEGORY_VALUE_3);
	current.setUuid(uuid);
	Description newContextDescription = new Description(CONTEXT_TYPE, CONTEXT_VALUE);
	newContextDescription.setUuid(UUID.randomUUID().toString());

	Set<Description> pDescriptions = Sets.newHashSet(previous);
	Set<Description> cDescriptions = Sets.newHashSet(current, newContextDescription);

	List<DescriptionDifferences> results = DescriptionComparator.INSTANCE.compare(pDescriptions, cDescriptions);
	assertEquals(2, results.size());

	DescriptionDifferences modified = findDescriptionDifferencesByName(CATEGORY_TYPE, results);
	assertEquals("small animal that says woofbarks", modified.getValue());
	assertEquals(CATEGORY_TYPE, modified.getName());

	List<Difference> diffs = modified.getDifferences();
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

	DescriptionDifferences added = findDescriptionDifferencesByName(CONTEXT_TYPE, results);
	assertEquals(CONTEXT_VALUE, added.getValue());
	assertEquals(CONTEXT_TYPE, added.getName());

	diffs = added.getDifferences();
	assertEquals(1, diffs.size());

	inserted = diffs.iterator().next();
	assertEquals(0, inserted.getStartIndex());
	assertEquals(19, inserted.getLength());
	assertEquals(Operation.INSERT.name(), inserted.getOperation());
    }

    @Test
    public void compareEmptyPreviousAndNonEmptyCurrentDescriptionTest() {
	final String uuid = UUID.randomUUID().toString();

	Description current = new Description(CATEGORY_TYPE, CATEGORY_VALUE_1);
	current.setUuid(uuid);
	Description previous = new Description(CATEGORY_TYPE, StringUtils.EMPTY);
	previous.setUuid(uuid);

	Set<Description> cDescriptions = Sets.newHashSet(current);
	Set<Description> pDescriptions = Sets.newHashSet(previous);

	List<DescriptionDifferences> results = DescriptionComparator.INSTANCE.compare(pDescriptions, cDescriptions);
	assertEquals(1, results.size());

	DescriptionDifferences descriptionDifferences = results.iterator().next();
	assertEquals(CATEGORY_TYPE, descriptionDifferences.getName());
	assertEquals(CATEGORY_VALUE_1, descriptionDifferences.getValue());

	List<Difference> diffs = descriptionDifferences.getDifferences();
	assertEquals(1, diffs.size());

	Difference diff = diffs.iterator().next();
	assertEquals(START_INDEX_ZERO, diff.getStartIndex());
	assertEquals(CATEGORY_VALUE_1.length(), diff.getLength());
	assertEquals(Operation.INSERT.name(), diff.getOperation());
    }

    @Test
    public void compareEqualDescriptionsTest() {
	Set<Description> pDescriptions = Sets.newHashSet(new Description(CATEGORY_TYPE, CATEGORY_VALUE_1));
	Set<Description> cDescriptions = Sets.newHashSet(new Description(CATEGORY_TYPE, CATEGORY_VALUE_1));
	/*
	 * If two collections are equal (i.e there are no differences in
	 * description base type, description type and description value between
	 * the corresponding description), return empty list as result.
	 */
	assertTrue(DescriptionComparator.INSTANCE.compare(pDescriptions, cDescriptions).isEmpty());
    }

    @Test
    public void compareNonEmptyPreviousAndEmptyCurrentDescriptionTest() {
	final String uuid = UUID.randomUUID().toString();

	Description current = new Description(CATEGORY_TYPE, StringUtils.EMPTY);
	current.setUuid(uuid);
	Description previous = new Description(CATEGORY_TYPE, CATEGORY_VALUE_1);
	previous.setUuid(uuid);

	Set<Description> cDescriptions = Sets.newHashSet(current);
	Set<Description> pDescriptions = Sets.newHashSet(previous);

	List<DescriptionDifferences> results = DescriptionComparator.INSTANCE.compare(pDescriptions, cDescriptions);
	assertEquals(1, results.size());

	DescriptionDifferences descriptionDifferences = results.iterator().next();
	assertEquals(CATEGORY_TYPE, descriptionDifferences.getName());
	assertEquals(CATEGORY_VALUE_1, descriptionDifferences.getValue());

	List<Difference> diffs = descriptionDifferences.getDifferences();
	assertEquals(1, diffs.size());

	Difference diff = diffs.iterator().next();
	assertEquals(START_INDEX_ZERO, diff.getStartIndex());
	assertEquals(CATEGORY_VALUE_1.length(), diff.getLength());
	assertEquals(Operation.DELETE.name(), diff.getOperation());
    }

    @Test
    public void compareNullDescriptionsTest() {
	Set<Description> cDescriptions = null;
	Set<Description> pDescriptions = null;
	/*
	 * Tries to handle null input gracefully. An exception will generally
	 * not be thrown for a null input, instead empty list will be returned.
	 */
	assertTrue(DescriptionComparator.INSTANCE.compare(pDescriptions, cDescriptions).isEmpty());
    }

    private DescriptionDifferences findDescriptionDifferencesByName(String name, List<DescriptionDifferences> list) {
	return list.stream().filter(d -> name.equals(d.getName())).findFirst().orElse(null);
    }
}
