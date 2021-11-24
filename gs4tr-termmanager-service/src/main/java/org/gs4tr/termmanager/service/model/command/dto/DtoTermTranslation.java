package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.termmanager.model.dto.DtoBaseUserProfile;
import org.gs4tr.termmanager.model.dto.Language;

public class DtoTermTranslation {

    private DtoBaseUserProfile[] _assignees;

    private int _blackListedTermCount;

    private Language _language;

    private String[] _termEntryTickets;

    private int _termsInTranslationCount;

    private int _totalTargetTermsCount;

    private DtoTranslationPair[] _translationPairs;

    public DtoBaseUserProfile[] getAssignees() {
	return _assignees;
    }

    public int getBlackListedTermCount() {
	return _blackListedTermCount;
    }

    public Language getLanguage() {
	return _language;
    }

    public String[] getTermEntryTickets() {
	return _termEntryTickets;
    }

    public int getTermsInTranslationCount() {
	return _termsInTranslationCount;
    }

    public int getTotalTargetTermsCount() {
	return _totalTargetTermsCount;
    }

    public DtoTranslationPair[] getTranslationPairs() {
	return _translationPairs;
    }

    public void setAssignees(DtoBaseUserProfile[] assignees) {
	_assignees = assignees;
    }

    public void setBlackListedTermCount(int blackListedTermCount) {
	_blackListedTermCount = blackListedTermCount;
    }

    public void setLanguage(Language language) {
	_language = language;
    }

    public void setTermEntryTickets(String[] termEntryTickets) {
	_termEntryTickets = termEntryTickets;
    }

    public void setTermsInTranslationCount(int termsInTranslationCount) {
	_termsInTranslationCount = termsInTranslationCount;
    }

    public void setTotalTargetTermsCount(int totalTargetTermsCount) {
	_totalTargetTermsCount = totalTargetTermsCount;
    }

    public void setTranslationPairs(DtoTranslationPair[] translationPairs) {
	_translationPairs = translationPairs;
    }
}
