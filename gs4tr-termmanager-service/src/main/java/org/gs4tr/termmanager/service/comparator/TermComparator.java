package org.gs4tr.termmanager.service.comparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.gs4tr.foundation3.core.diff.DiffMatchPatch.Diff;
import org.gs4tr.foundation3.core.diff.DiffMatchPatch.Operation;
import org.gs4tr.termmanager.model.dto.Difference;
import org.gs4tr.termmanager.model.dto.TermDifferences;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;

/**
 * Package private supporting class which compares two collections of
 * <code>Term</code> objects and returns collection of
 * <code>TermDifferences</code> as result.
 * <p>
 * 
 * @author TMGR_Backend
 */
class TermComparator implements Comparator<Collection<Term>, Collection<TermDifferences>> {

    /**
     * The <code>TermComparator</code> class is stateless: it has no fields
     * (beside compile-time constants), hence all instances of the class are
     * functionally equivalent. <b>Thus, to save on unnecessary object creation
     * costs, <code>TermComparator</code> class is a singleton. </b>
     */
    public static final Comparator<Collection<Term>, Collection<TermDifferences>> INSTANCE = new TermComparator();

    private static final LinkedList<Term> EMPTY_LINKED_LIST = new LinkedList<>();

    private static final int START_INDEX_ZERO = 0;
    private static final int TEXT_LENGTH = 2 << 2;

    private TermComparator() {
    }

    /**
     * Compares two collections where either one or both collections may be
     * {@code null}. <b> This method tries to handle code <code>null</code>
     * input gracefully. An exception will generally not be thrown for a code
     * {@code null} input. </b>
     */
    @Override
    public Collection<TermDifferences> compare(Collection<Term> previousTerms, Collection<Term> currentTerms) {
	LinkedList<Term> pTerms = orderedNullSafeCopy(previousTerms);
	LinkedList<Term> cTerms = orderedNullSafeCopy(currentTerms);

	List<TermDifferences> result = new ArrayList<>();

	for (final Term current : cTerms) {
	    final String termUuid = current.getUuId();
	    Term previous = findPreviousTermByUuId(termUuid, pTerms);
	    TermDifferences diff = findTermDiff(previous, current);
	    if (Objects.nonNull(diff)) {
		result.add(diff);
	    }
	}
	for (final Term previous : pTerms) {
	    result.add(newTermDiff(previous, Operation.DELETE));
	}

	return result;
    }

    /**
     * Compare two <code>Term</code> where both terms are not <code>null</code>.
     * <p>
     * If no changes made on terms between two saves of term entry (i.e there
     * are no changes in term status, term name and term descriptions) this
     * method will return <code>null</code> as result.
     * </p>
     */
    private TermDifferences compare(Term previous, Term current) {
	if (isEquals(previous, current)) {
	    return null;
	}
	String oldName = previous.getName();
	String newName = current.getName();
	List<Diff> diffs = TextComparator.INSTANCE.compare(oldName, newName);

	TermDifferences result = new TermDifferences(previous.getStatus(), current.getStatus(), diffToText(diffs));
	result.getDifferences().addAll(DiffsConverter.convertToDifferences(diffs));

	Map<String, List<Description>> pDescriptionGrouped = groupDescriptionsByBaseType(previous.getDescriptions());
	Map<String, List<Description>> cDescriptionGrouped = groupDescriptionsByBaseType(current.getDescriptions());

	List<Description> pAttributes = pDescriptionGrouped.get(Description.ATTRIBUTE);
	List<Description> cAttributes = cDescriptionGrouped.get(Description.ATTRIBUTE);
	result.getAttributesDifferences().addAll(DescriptionComparator.INSTANCE.compare(pAttributes, cAttributes));

	List<Description> pNotes = pDescriptionGrouped.get(Description.NOTE);
	List<Description> cNotes = cDescriptionGrouped.get(Description.NOTE);
	result.getNotesDifferences().addAll(DescriptionComparator.INSTANCE.compare(pNotes, cNotes));

	return result;
    }

    private TermDifferences createTermDiff(Term term, String operation) {
	String status = term.getStatus(), name = nullSafeTermName(term);
	TermDifferences result = new TermDifferences(status, status, name);
	List<Difference> differences = result.getDifferences();
	differences.add(new Difference(START_INDEX_ZERO, name.length(), operation));
	return result;
    }

    private String diffToText(final List<Diff> diffs) {
	StringBuilder text = new StringBuilder(diffs.size() * TEXT_LENGTH);
	diffs.stream().map(diff -> diff.text).forEach(text::append);
	return text.toString();
    }

    private Term findPreviousTermByUuId(String termUuid, Collection<Term> pTerms) {
	Iterator<Term> each = pTerms.iterator();
	while (each.hasNext()) {
	    Term candidate = each.next();
	    if (termUuid.equals(candidate.getUuId())) {
		each.remove();
		return candidate;
	    }
	}
	return null;
    }

    private TermDifferences findTermDiff(Term previous, Term current) {
	return previous == null ? newTermDiff(current, Operation.INSERT) : compare(previous, current);
    }

    private Map<String, List<Description>> groupDescriptionsByBaseType(Set<Description> descriptions) {
	Map<String, List<Description>> groupedDescriptions = CollectionUtils.isEmpty(descriptions)
		? Collections.emptyMap()
		: descriptions.stream().collect(Collectors.groupingBy(Description::getBaseType));
	return groupedDescriptions;
    }

    private boolean isEquals(Term previous, Term current) {
	final EqualsBuilder eb = new EqualsBuilder();
	eb.append(previous.getStatus(), current.getStatus());
	eb.append(previous.getName(), current.getName());
	eb.append(previous.getDescriptions(), current.getDescriptions());
	return eb.isEquals();
    }

    private TermDifferences newTermDiff(final Term term, final Operation operation) {
	List<Description> emptyList = Collections.emptyList();
	if (Operation.INSERT == operation) {
	    TermDifferences result = createTermDiff(term, Operation.INSERT.name());
	    Map<String, List<Description>> descriptionGrouped = groupDescriptionsByBaseType(term.getDescriptions());
	    result.getAttributesDifferences().addAll(
		    DescriptionComparator.INSTANCE.compare(emptyList, descriptionGrouped.get(Description.ATTRIBUTE)));
	    result.getNotesDifferences().addAll(
		    DescriptionComparator.INSTANCE.compare(emptyList, descriptionGrouped.get(Description.NOTE)));
	    return result;
	} else if (Operation.DELETE == operation) {
	    TermDifferences result = createTermDiff(term, Operation.DELETE.name());
	    Map<String, List<Description>> descriptionGrouped = groupDescriptionsByBaseType(term.getDescriptions());
	    result.getAttributesDifferences().addAll(
		    DescriptionComparator.INSTANCE.compare(descriptionGrouped.get(Description.ATTRIBUTE), emptyList));
	    result.getNotesDifferences().addAll(
		    DescriptionComparator.INSTANCE.compare(descriptionGrouped.get(Description.NOTE), emptyList));
	    return result;
	}
	throw new IllegalArgumentException(String.format(Messages.getString("TermComparator.0"), operation.name())); //$NON-NLS-1$
    }

    private String nullSafeTermName(final Term term) {
	return Objects.isNull(term.getName()) ? StringUtils.EMPTY : term.getName();
    }

    private LinkedList<Term> orderedNullSafeCopy(Collection<Term> terms) {
	if (CollectionUtils.isEmpty(terms)) {
	    return EMPTY_LINKED_LIST;
	}
	LinkedList<Term> result = new LinkedList<>();
	for (Term term : terms) {
	    if (term.isFirst()) {
		result.addFirst(term);
		continue;
	    }
	    result.add(term);
	}
	return result;
    }
}
