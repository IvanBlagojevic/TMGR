package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.termmanager.model.dto.Term;

public class DtoTranslationPair {

    private Term[] _sourceTerms;

    private Term[] _targetTerms;

    private String _termEntryTicket;

    public Term[] getSourceTerms() {
	return _sourceTerms;
    }

    public Term[] getTargetTerms() {
	return _targetTerms;
    }

    public String getTermEntryTicket() {
	return _termEntryTicket;
    }

    public void setSourceTerms(Term[] sourceTerms) {
	_sourceTerms = sourceTerms;
    }

    public void setTargetTerms(Term[] targetTerms) {
	_targetTerms = targetTerms;
    }

    public void setTermEntryTicket(String termEntryTicket) {
	_termEntryTicket = termEntryTicket;
    }
}
