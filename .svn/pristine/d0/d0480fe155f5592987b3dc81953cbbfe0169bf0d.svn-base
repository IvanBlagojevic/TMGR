package org.gs4tr.termmanager.service.xls.report.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.service.xls.report.builder.terminology.ReportTerm;

public class ReportTermConverter {

    public static List<ReportTerm> convertTo(Collection<Term> terms) {
	List<ReportTerm> reportTerms = new ArrayList<>();
	if (CollectionUtils.isNotEmpty(terms)) {
	    terms.forEach(t -> reportTerms.add(convertTo(t)));
	}

	return reportTerms;
    }

    public static ReportTerm convertTo(Term term) {
	return new ReportTerm(term);
    }

    public static ReportTerm convertTo(Term oldTerm, Term newTerm) {
	return new ReportTerm(oldTerm, newTerm);
    }

    private ReportTermConverter() {

    }
}
