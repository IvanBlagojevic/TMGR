package org.gs4tr.termmanager.service.xls.report.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.service.xls.report.builder.terminology.ReportDescription;

public class ReportEqualsHelper {

    /* Assists in implementing Object.equals(Object) methods. */
    private EqualsBuilder _builder;

    private boolean _ignoreCase;

    public ReportEqualsHelper() {
	_builder = new EqualsBuilder();
	_ignoreCase = true;
    }

    public ReportEqualsHelper(boolean ignoreCase) {
	this();
	_ignoreCase = ignoreCase;
    }

    public boolean containsAllDescriptions(Collection<Description> coll1, Collection<Description> coll2) {
	for (Description d1 : coll1) {
	    if (Objects.isNull(containsDescription(d1, coll2))) {
		return false;
	    }
	}

	return coll1.size() == coll2.size();
    }

    public boolean containsAllReportDescriptions(Collection<ReportDescription> coll1,
	    Collection<ReportDescription> coll2) {
	for (ReportDescription d1 : coll1) {
	    if (Objects.isNull(containsReportDescription(d1, coll2))) {
		return false;
	    }
	}

	return coll1.size() == coll2.size();
    }

    public Description containsDescription(Description desc, Collection<Description> descs) {
	String typeValue = desc.getType() + desc.getValue();

	for (Description d : descs) {
	    String tV = d.getType() + d.getValue();
	    if (isIgnoreCase() ? typeValue.equalsIgnoreCase(tV) : typeValue.equals(tV)) {
		return d;
	    }
	}

	return null;
    }

    public Description containsDescriptionByType(Description desc, Collection<Description> descs) {
	String type = desc.getType();

	for (Description d : descs) {
	    if (isIgnoreCase() ? type.equalsIgnoreCase(d.getType()) : type.equals(d.getType())) {
		return d;
	    }
	}

	return null;
    }

    public ReportDescription containsReportDescription(ReportDescription desc, Collection<ReportDescription> descs) {
	String typeValue = desc.getType() + desc.getNewValue();

	for (ReportDescription d : descs) {
	    String tV = d.getType() + d.getNewValue();
	    if (isIgnoreCase() ? typeValue.equalsIgnoreCase(tV) : typeValue.equals(tV)) {
		return d;
	    }
	}

	return null;
    }

    public ReportDescription containsReportDescriptionByType(ReportDescription desc,
	    Collection<ReportDescription> descs) {
	String type = desc.getType();

	for (ReportDescription d : descs) {
	    if (isIgnoreCase() ? type.equalsIgnoreCase(d.getType()) : type.equals(d.getType())) {
		return d;
	    }
	}

	return null;
    }

    public Term containsTerm(Term term, Collection<Term> terms, boolean checkStatus) {
	return terms.stream().filter(t -> termsEqualByAllText(t, term, checkStatus)).findFirst().orElse(null);
    }

    public Term containsTermByName(Term term, Collection<Term> terms) {
	return terms.stream().filter(t -> termsEqualByName(t, term)).findFirst().orElse(null);
    }

    public boolean equalsString(String st1, String st2) {
	return isIgnoreCase() ? st1.equalsIgnoreCase(st2) : st1.equals(st2);
    }

    private EqualsBuilder getBuilder() {
	return _builder;
    }

    private List<String> getDescriptions(Term t) {
	boolean ignoreCase = isIgnoreCase();

	List<String> descs = new ArrayList<>();
	Set<Description> descriptions = t.getDescriptions();
	if (CollectionUtils.isNotEmpty(descriptions)) {
	    for (Description d : descriptions) {
		descs.add(ignoreCase ? d.getType().toLowerCase() : d.getType());
		descs.add(ignoreCase ? d.getValue().toLowerCase() : d.getValue());
	    }
	}
	return descs;
    }

    private boolean isIgnoreCase() {
	return _ignoreCase;
    }

    private boolean termsEqualByAllText(Term t1, Term t2, boolean checkStatus) {
	boolean ignoreCase = isIgnoreCase();

	String t1Name = ignoreCase ? t1.getName().toLowerCase() : t1.getName();
	String t2Name = ignoreCase ? t2.getName().toLowerCase() : t2.getName();

	List<String> t1Descs = getDescriptions(t1);
	List<String> t2Descs = getDescriptions(t2);

	EqualsBuilder builder = getBuilder();

	builder.append(t1Name, t2Name);
	builder.append(t1Descs, t2Descs);
	if (checkStatus) {
	    builder.append(t1.getStatus(), t2.getStatus());
	}

	boolean equals = builder.isEquals();

	builder.reset();

	return equals;
    }

    private boolean termsEqualByName(Term t1, Term t2) {
	return isIgnoreCase() ? t1.getName().equalsIgnoreCase(t2.getName()) : t1.getName().equals(t2.getName());
    }
}
