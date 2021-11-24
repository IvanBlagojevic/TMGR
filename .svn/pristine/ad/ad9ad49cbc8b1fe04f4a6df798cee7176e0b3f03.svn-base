package org.gs4tr.termmanager.service.comparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.foundation3.core.diff.DiffMatchPatch.Diff;
import org.gs4tr.foundation3.core.diff.DiffMatchPatch.Operation;
import org.gs4tr.termmanager.model.dto.DescriptionDifferences;
import org.gs4tr.termmanager.model.dto.Difference;
import org.gs4tr.termmanager.model.glossary.Description;

/**
 * Package private supporting class which compares two collections of
 * <code>Description</code> objects and returns list of
 * <code>DescriptionDifferences</code> as result.
 * 
 * @author TMGR_Backend
 */
class DescriptionComparator implements Comparator<Collection<Description>, List<DescriptionDifferences>> {
    /**
     * The <code>DescriptionComparator</code> class is stateless: it has no
     * fields (beside compile-time constants), hence all instances of the class
     * are functionally equivalent. <b>Thus, to save on unnecessary object
     * creation costs, <code>DescriptionComparator</code> class is a
     * singleton.</b>
     */
    public static final Comparator<Collection<Description>, List<DescriptionDifferences>> INSTANCE = new DescriptionComparator();

    private static final int START_INDEX_ZERO = 0;
    private static final int TEXT_LENGTH = 2 << 2;

    private DescriptionComparator() {
    }

    /**
     * Compares two collections where either one or both collections may be
     * {@code null}. This method tries to handle code {@code null} input
     * gracefully. An exception will generally not be thrown for a code
     * {@code null} input.
     */
    @Override
    public List<DescriptionDifferences> compare(Collection<Description> pDescriptions,
	    Collection<Description> cDescriptions) {
	if (Objects.equals(pDescriptions, cDescriptions)) {
	    return Collections.emptyList();
	}
	Set<Description> pCopied = nullSafeCopy(pDescriptions);

	List<DescriptionDifferences> result = new ArrayList<>();
	if (CollectionUtils.isNotEmpty(cDescriptions)) {
	    for (final Description current : cDescriptions) {
		Description previous = findPreviousDescriptionById(current.getUuid(), pCopied);
		if (previous == null) {
		    String diffName = current.getType();
		    String diffText = current.getValue();
		    String operation = Operation.INSERT.name();
		    result.add(createDescriptionDifferences(diffName, diffText, operation));
		    continue;
		}
		result.add(compare(previous, current));
	    }
	}
	if (CollectionUtils.isNotEmpty(pCopied)) {
	    for (final Description previous : pCopied) {
		String diffName = previous.getType();
		String diffText = previous.getValue();
		String operation = Operation.DELETE.name();
		result.add(createDescriptionDifferences(diffName, diffText, operation));
	    }
	}
	result.removeIf(Objects::isNull);

	return result;
    }

    private DescriptionDifferences compare(Description previous, Description current) {
	Validate.isTrue(previous.getBaseType().equals(current.getBaseType()));
	if (previous.equals(current)) {
	    /*
	     * Both non-null descriptions are equal (i.e there are no
	     * differences in description base type, description type and
	     * description value). Nothing to return as result.
	     */
	    return null;
	}
	return compareInternal(previous, current);
    }

    private DescriptionDifferences compareInternal(Description previous, Description current) {
	List<Diff> diffs = TextComparator.INSTANCE.compare(previous.getValue(), current.getValue());
	String diffText = diffToText(diffs);
	String diffName = current.getType();

	DescriptionDifferences result = new DescriptionDifferences(diffName, diffText);
	result.getDifferences().addAll(DiffsConverter.convertToDifferences(diffs));
	return result;
    }

    private DescriptionDifferences createDescriptionDifferences(String diffName, String diffText, String color) {
	DescriptionDifferences result = new DescriptionDifferences(diffName, diffText);
	Difference diff = new Difference(START_INDEX_ZERO, diffText.length(), color);
	result.getDifferences().add(diff);
	return result;
    }

    private String diffToText(final List<Diff> diffs) {
	StringBuilder text = new StringBuilder(diffs.size() * TEXT_LENGTH);
	diffs.stream().map(diff -> diff.text).forEach(text::append);
	return text.toString();
    }

    private Description findPreviousDescriptionById(String uuid, Collection<Description> pDescriptions) {
	Iterator<Description> each = pDescriptions.iterator();
	while (each.hasNext()) {
	    Description candidate = each.next();
	    if (uuid.equals(candidate.getUuid())) {
		each.remove();
		return candidate;
	    }
	}
	return null;
    }

    private Set<Description> nullSafeCopy(final Collection<Description> descriptions) {
	return descriptions == null ? Collections.emptySet() : new HashSet<>(descriptions);
    }
}
