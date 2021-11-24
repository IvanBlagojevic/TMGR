package org.gs4tr.termmanager.service.comparator;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.dto.DescriptionDifferences;
import org.gs4tr.termmanager.model.dto.LanguageModifications;
import org.gs4tr.termmanager.model.dto.TermDifferences;
import org.gs4tr.termmanager.model.dto.TermEntryDifferences;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;

/**
 * The <code>TermEntryComparator</code> class is used for highlighting
 * differences in TE History. This class is able to tell precisely which
 * character(s) in the term or attribute/note string were added, deleted and
 * were unchanged.
 * 
 * @author TMGR_Backend
 */
public class TermEntryComparator implements Comparator<TermEntry, TermEntryDifferences> {

    /**
     * The <code>TermEntryComparator</code> class is stateless: it has no fields,
     * hence all instances of the class are functionally equivalent. <b> Thus, to
     * save on unnecessary object creation costs, <code>TermEntryComparator</code>
     * class is a singleton. </b>
     */
    public static final Comparator<TermEntry, TermEntryDifferences> INSTANCE = new TermEntryComparator();

    private static final int DATE_DIFF_LIMIT = 1500;

    private static final Long FIRST_REVISION_ID = 1L;

    private static final String HISTORY_INFO_N_A = "History info N/A";

    private TermEntryComparator() {
    }

    @Override
    public TermEntryDifferences compare(final TermEntry previous, final TermEntry current) {
	String message = Messages.getString("TermEntryComparator.0");
	Objects.requireNonNull(current, message); // $NON-NLS-1$
	Objects.requireNonNull(previous, message); // $NON-NLS-1$
	return compareInternal(previous, current);
    }

    private TermEntryDifferences compareInternal(TermEntry previous, TermEntry current) {
	return shouldHighlightRevision(current) ? createHighlightedRevision(previous, current)
		: createRevisionNoHighlight(current);
    }

    private List<LanguageModifications> compareLanguageTerms(TermEntry previous, TermEntry current) {
	Map<String, Set<Term>> cLanguageTerms = getLanguageTerms(current);
	Map<String, Set<Term>> pLanguageTerms = getLanguageTerms(previous);

	List<LanguageModifications> result = new ArrayList<>();

	for (Entry<String, Set<Term>> entry : cLanguageTerms.entrySet()) {
	    String languageId = entry.getKey();
	    Set<Term> cTerms = entry.getValue();
	    Set<Term> pTerms = pLanguageTerms.get(languageId);
	    Collection<TermDifferences> diffs = TermComparator.INSTANCE.compare(pTerms, cTerms);
	    if (CollectionUtils.isNotEmpty(diffs)) {
		result.add(createLanguageModifications(languageId, diffs));
	    }
	}
	for (Entry<String, Set<Term>> entry : pLanguageTerms.entrySet()) {
	    String languageId = entry.getKey();
	    if (isLanguageDeleted(cLanguageTerms, languageId)) {
		Set<Term> pTerms = entry.getValue();
		Collection<TermDifferences> diffs = TermComparator.INSTANCE.compare(pTerms, emptyList());
		if (CollectionUtils.isNotEmpty(diffs)) {
		    result.add(createLanguageModifications(languageId, diffs));
		}
	    }
	}
	return result;
    }

    private List<DescriptionDifferences> compareTermEntryDescriptions(TermEntry previous, TermEntry current) {
	return DescriptionComparator.INSTANCE.compare(previous.getDescriptions(), current.getDescriptions());
    }

    private List<DescriptionDifferences> createDescriptionDiffsNoHighlight(Collection<Description> descriptions) {
	List<DescriptionDifferences> result = Collections.emptyList();

	if (CollectionUtils.isNotEmpty(descriptions)) {
	    result = new ArrayList<>(descriptions.size());
	    for (Description description : descriptions) {
		String type = description.getType();
		String value = description.getValue();
		result.add(new DescriptionDifferences(type, value));
	    }
	}
	return result;
    }

    private TermEntryDifferences createHighlightedRevision(TermEntry previous, TermEntry current) {
	String userModified = current.getUserModified();
	Long dateModified = current.getDateModified();
	String actionName = current.getAction().name();

	TermEntryDifferences revision = new TermEntryDifferences(actionName, dateModified, userModified);
	revision.addAllAttributeModifications(compareTermEntryDescriptions(previous, current));
	revision.addAllLanguagesModifications(compareLanguageTerms(previous, current));
	return responseData(revision);
    }

    private LanguageModifications createLanguageModifications(String languageId, Collection<TermDifferences> diffs) {
	LanguageModifications result = new LanguageModifications(languageId);
	result.getTermsDifferences().addAll(diffs);
	return result;
    }

    private TermEntryDifferences createRevisionNoHighlight(TermEntry current) {
	TermEntryDifferences revision = new TermEntryDifferences(HISTORY_INFO_N_A, null, StringUtils.EMPTY);

	revision.addAllAttributeModifications(createDescriptionDiffsNoHighlight(current.getDescriptions()));

	Map<String, Set<Term>> cLanguageTerms = current.getLanguageTerms();
	if (MapUtils.isNotEmpty(cLanguageTerms)) {
	    List<LanguageModifications> lms = new ArrayList<>(cLanguageTerms.size());
	    for (Entry<String, Set<Term>> entry : cLanguageTerms.entrySet()) {
		final Set<Term> cTerms = entry.getValue();
		if (CollectionUtils.isNotEmpty(cTerms)) {
		    List<TermDifferences> diffs = new ArrayList<>(cTerms.size());
		    for (final Term term : cTerms) {
			diffs.add(createTermDiffsNoHighlight(term));
		    }
		    lms.add(createLanguageModifications(entry.getKey(), diffs));
		}
	    }
	    revision.addAllLanguagesModifications(lms);
	}
	return revision;
    }

    private TermDifferences createTermDiffsNoHighlight(Term term) {
	Map<String, List<Description>> descriptionsByBaseType = groupDescriptionsByBaseType(term.getDescriptions());

	TermDifferences termDiff = new TermDifferences(term.getStatus(), term.getStatus(), term.getName());
	termDiff.getAttributesDifferences()
		.addAll(createDescriptionDiffsNoHighlight(descriptionsByBaseType.get(Description.ATTRIBUTE)));
	termDiff.getNotesDifferences()
		.addAll(createDescriptionDiffsNoHighlight(descriptionsByBaseType.get(Description.NOTE)));
	return termDiff;
    }

    /*
     * TERII-4504 - Compare modification and creation date and if the difference is
     * bellow the limit, consider it as equal.
     */
    private boolean datesDiffValid(Long dateCreated, Long dateModified) {
	long diff = dateModified.longValue() - dateCreated.longValue();
	return diff < DATE_DIFF_LIMIT;
    }

    private Map<String, Set<Term>> getLanguageTerms(final TermEntry termEntry) {
	return termEntry.getLanguageTerms() == null ? emptyMap() : termEntry.getLanguageTerms();
    }

    private Map<String, List<Description>> groupDescriptionsByBaseType(Set<Description> descriptions) {
	Map<String, List<Description>> groupedDescriptions = CollectionUtils.isEmpty(descriptions)
		? Collections.emptyMap()
		: descriptions.stream().collect(Collectors.groupingBy(Description::getBaseType));
	return groupedDescriptions;
    }

    private boolean isLanguageDeleted(Map<String, Set<Term>> cLanguageTerms, String languageId) {
	return !cLanguageTerms.containsKey(languageId);
    }

    private TermEntryDifferences responseData(TermEntryDifferences revision) {
	if (revision.hasNoChange()) {
	    /*
	     * If there are no changes in language terms (i.e there are no changes in term
	     * status, term name and term descriptions) and term entry attributes, discard
	     * this revision.
	     */
	    return null;
	}
	return revision;
    }

    private boolean shouldHighlightRevision(TermEntry current) {
	boolean highlightRevision = true;
	if (FIRST_REVISION_ID.equals(current.getRevisionId())) {
	    Long dateCreated = current.getDateCreated();
	    Long dateModified = current.getDateModified();
	    if (!datesDiffValid(dateCreated, dateModified)) {
		// 12-December-2016, as per [Bug#TERII-4492]
		highlightRevision = false;
	    }
	}
	return highlightRevision;
    }
}
