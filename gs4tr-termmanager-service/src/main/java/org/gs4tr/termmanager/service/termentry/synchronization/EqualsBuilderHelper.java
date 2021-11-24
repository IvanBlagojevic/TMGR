package org.gs4tr.termmanager.service.termentry.synchronization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;

/**
 * This class provides customContains method which will serve as a substitute
 * for contains method on <code>Collection<code> classes. Using this
 * implementation we are able to perform custom contains logic without any
 * changes on equals method in model class.
 * 
 * @author TMGR_Backend
 * 
 */
public class EqualsBuilderHelper {

    /* Assists in implementing Object.equals(Object) methods. */
    private final EqualsBuilder _builder;

    private boolean _ignoreCase = true;

    private boolean _includeStatus = true;

    public EqualsBuilderHelper() {
	_builder = new EqualsBuilder();
    }

    /**
     * Returns true if <code>descriptions<code> contains the at least one required
     * description element.
     *
     * @param requiredDescriptions
     * @param descriptions
     * @return
     */
    public boolean containsDescription(Collection<Description> requiredDescriptions,
	    Collection<Description> descriptions) {
	if (CollectionUtils.isEmpty(descriptions)) {
	    return false;
	}
	for (Description requiredDescription : requiredDescriptions) {
	    for (Description description : descriptions) {
		if (isDescriptionsEqual(requiredDescription, description)) {
		    return true;
		}
	    }
	}
	return false;
    }

    public boolean containsDescription(Description desc, Collection<Description> descs) {
	String typeValue = desc.getType() + desc.getValue();

	for (Description d : descs) {
	    if (!desc.getBaseType().equals(d.getBaseType())) {
		continue;
	    }

	    String tV = d.getType() + d.getValue();
	    if (_ignoreCase ? typeValue.equalsIgnoreCase(tV) : typeValue.equals(tV)) {
		return true;
	    }
	}

	return false;
    }

    /**
     * Returns true if this <code>Collection<code> contains the specified term
     * element using custom equals logic.
     *
     * @param terms
     * @param term
     * @return
     */
    public Optional<Term> containsTerm(Collection<Term> terms, Term term) {
	if (CollectionUtils.isEmpty(terms)) {
	    return Optional.empty();
	}

	for (Term tempTerm : terms) {
	    if (isTermsEqual(tempTerm, term)) {
		/* If the Collection contain the element */
		return Optional.of(tempTerm);
	    }
	}
	/* If the Collection does not contain the element */
	return Optional.empty();
    }

    /**
     * Returns true if coll1 contains all elements from coll1 and if coll1 size is
     * equals coll2 size.
     *
     * @param coll1
     * @param coll1
     * @return
     */
    public boolean equalsAll(Collection<Term> coll1, Collection<Term> coll2) {
	if (coll1.size() != coll2.size()) {
	    return false;
	}

	for (Term term2 : coll2) {
	    if (!containsTerm(coll1, term2).isPresent()) {
		return false;
	    }
	}
	return true;
    }

    public boolean isEqual(String text1, String text2) {
	return _ignoreCase ? text1.equalsIgnoreCase(text2) : text1.equals(text2);
    }

    public boolean isEquals(TermEntry e1, TermEntry e2) {
	if (Objects.isNull(e2)) {
	    return false;
	}

	List<Description> descs1 = getDescriptions(e1.getDescriptions());
	List<Description> descs2 = getDescriptions(e2.getDescriptions());

	if (!CollectionUtils.isEqualCollection(descs1, descs2)) {
	    return false;
	}

	List<Term> terms1 = e1.ggetAllTerms();
	List<Term> terms2 = e2.ggetAllTerms();

	if (Objects.isNull(terms1) && Objects.isNull(terms2)) {
	    return true;
	}

	if (Objects.isNull(terms1) || Objects.isNull(terms2)) {
	    return false;
	}

	return equalsAll(terms1, terms2);
    }

    public boolean isEqualsIgnoreCase(Term existingTerm, Term incomingTerm) {
	String existingName = existingTerm.getName();
	if (StringUtils.isNotEmpty(existingName)) {
	    existingName = existingName.toLowerCase();
	}
	String incomingName = incomingTerm.getName();
	if (StringUtils.isNotEmpty(incomingName)) {
	    incomingName = incomingName.toLowerCase();
	}

	EqualsBuilder builder = getBuilder();
	// Returns true if the fields that have been checked are all equal.
	boolean equals = builder.append(existingTerm.getLanguageId(), incomingTerm.getLanguageId())
		.append(existingName, incomingName).isEquals();
	// Reset the EqualsBuilder so we can use the same object again
	builder.reset();

	return equals;
    }

    public void setIgnoreCase(boolean ignoreCase) {
	_ignoreCase = ignoreCase;
    }

    public void setIncludeStatus(boolean includeStatus) {
	_includeStatus = includeStatus;
    }

    private EqualsBuilder getBuilder() {
	return _builder;
    }

    private List<Description> getDescriptions(Set<Description> descriptions) {
	List<Description> descs = new ArrayList<>();
	if (CollectionUtils.isEmpty(descriptions)) {
	    return descs;
	}

	if (_ignoreCase) {
	    for (Description d : descriptions) {
		descs.add(new Description(d.getBaseType().toLowerCase(), d.getType().toLowerCase(),
			d.getValue().toLowerCase()));
	    }
	} else {
	    descs.addAll(descriptions);
	}

	return descs;
    }

    private String getText(String text) {
	return _ignoreCase ? text.toLowerCase() : text;
    }

    private boolean isDescriptionsEqual(Description reqDesc, Description desc) {
	EqualsBuilder builder = getBuilder();

	String value1 = reqDesc.getValue();
	if (StringUtils.isNotBlank(value1)) {
	    value1 = _ignoreCase ? value1.toLowerCase() : value1;
	    String value2 = _ignoreCase ? desc.getValue().toLowerCase() : desc.getValue();
	    builder.append(value1, value2);
	}

	String baseType1 = _ignoreCase ? reqDesc.getBaseType().toLowerCase() : reqDesc.getBaseType();
	String type1 = _ignoreCase ? reqDesc.getType().toLowerCase() : reqDesc.getType();

	String type2 = _ignoreCase ? desc.getType().toLowerCase() : desc.getType();
	String baseType2 = _ignoreCase ? desc.getBaseType().toLowerCase() : desc.getBaseType();

	boolean equals = builder.append(type1, type2).append(baseType1, baseType2).isEquals();

	builder.reset();

	return equals;
    }

    private boolean isIncludeStatus() {
	return _includeStatus;
    }

    /* Maybe we should get rid of equalsBuilder */
    private boolean isTermsEqual(Term t1, Term t2) {
	EqualsBuilder builder = getBuilder();

	String name1 = getText(t1.getName());
	List<Description> descs1 = getDescriptions(t1.getDescriptions());

	String name2 = getText(t2.getName());
	List<Description> descs2 = getDescriptions(t2.getDescriptions());

	/*
	 *******************************************************************
	 * EquailsBuilder will return false if order of descriptions is not the
	 * same(ArrayList equal method will return false because order is not the same).
	 * Because of this we must use CollectionUtils to compare Term descriptions.
	 *******************************************************************
	 */
	if (!CollectionUtils.isEqualCollection(descs1, descs2)) {
	    return false;
	}

	// Returns true if the fields that have been checked are all equal.
	builder = builder.append(name1, name2).append(t1.getLanguageId(), t2.getLanguageId())
		.append(t1.isForbidden(), t2.isForbidden()).append(t1.isDisabled(), t2.isDisabled());

	if (isIncludeStatus()) {
	    builder.append(t1.getStatus(), t2.getStatus());
	}
	boolean isEquals = builder.isEquals();
	// Reset the EqualsBuilder so we can use the same object again
	builder.reset();

	return isEquals;
    }
}
